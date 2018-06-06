/*!
 * \file
 * \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its
 * subsidiaries and may be used for internal evaluation purposes or commercial
 * use strictly subject to separate licensee agreement between you and TomTom.
 * If you are the licensee, you are only permitted to use this Software in
 * accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and
 * should immediately return it to TomTom N.V.
 */

#include "AndroidDnsConfiguration.h"
#include "AdaptationsMutexLocker.h"
#include "AndroidJniResourceManagement.h"

#include <android/log.h>
#include <cassert>
#include <jni.h>

namespace {
const char TAG[]         = "CAndroidDnsConfiguration";
const char CLASS_NAME[]  = "com/tomtom/navkit/adaptations/AndroidDnsConfiguration";
const char METHOD_NAME[] = "getDnsAddresses";
const char SIGNATURE[]   = "()[Ljava/lang/String;";

/*!
 * RAII object to get the JNI Environment from the current thread context.
 * It fetches the environment, but also attaches / detaches the current thread, when required.
 */
class CJniEnvFromAttachedThread
{
public:
  /*! Construct RAII object.
   * aJavaVm should not be nullptr, and remain valid throughout the lifetime of the object.
   */
  CJniEnvFromAttachedThread(JavaVM* aJavaVm)
    : iNeedToDetach(false)
    , iJavaVm(aJavaVm)
    , iJniEnv(nullptr)
  {
    if (iJavaVm != nullptr)
    {
      jint status = iJavaVm->GetEnv((void**)&iJniEnv, JNI_VERSION_1_6);

      if (status == JNI_EDETACHED)
      {
        status        = iJavaVm->AttachCurrentThread(&iJniEnv, nullptr);
        iNeedToDetach = (status == JNI_OK);
      }

      if (status != JNI_OK)
      {
        __android_log_print(
          ANDROID_LOG_ERROR, TAG, "Could not get JNI Environment or attach thread to VM");
        iJniEnv       = nullptr;
        iNeedToDetach = false;
      }
    }
  }

  ~CJniEnvFromAttachedThread()
  {
    if (iNeedToDetach)
    {
      iJavaVm->DetachCurrentThread();
    }
  }

  /*!
   * Check whether the current thread is properly attached to the VM.
   */
  bool IsAttached() const
  {
    return (iJniEnv != nullptr);
  }

  /*!
   * Get the JNI Environment pointer.
   * May return nullptr if currently not attached.
   * The returned pointer is only valid as long as the object it was retrieved from is alive.
   */
  JNIEnv* GetJniEnv() const
  {
    return iJniEnv;
  }

private:
  bool    iNeedToDetach;
  JavaVM* iJavaVm;
  JNIEnv* iJniEnv;
};

} // namespace

namespace NProcessHost {
namespace NAdaptation {
using namespace NJniUtils;

CAndroidDnsConfiguration::CAndroidDnsConfiguration(JavaVM* aVM)
  : CAndroidJNIUtil(aVM)
  , iGetDnsAddresses(nullptr)
{
}

CAndroidDnsConfiguration::~CAndroidDnsConfiguration() {}

bool CAndroidDnsConfiguration::AcquireJniResources()
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s", __PRETTY_FUNCTION__);

  JNIEnv* env = nullptr;
  assert(mProxy == nullptr);

  if (mProxy != nullptr)
  {
    return false;
  }

  // Find the Java class & create an object.
  if (!CreateProxyWithContext(CLASS_NAME))
  {
    return false;
  }

  env = RetrieveEnv();
  if (env == nullptr)
  {
    return false;
  }

  CJniJClassPtr proxyClass(env, env->GetObjectClass(mProxy));
  if (CJniException::Occured(env))
  {
    __android_log_print(
      ANDROID_LOG_ERROR, TAG, "%s: Could not retrieve proxy object", __PRETTY_FUNCTION__);
    return false;
  }

  iGetDnsAddresses = env->GetMethodID(proxyClass, METHOD_NAME, SIGNATURE);
  if (CJniException::Occured(env) || (iGetDnsAddresses == nullptr))
  {
    __android_log_print(ANDROID_LOG_ERROR,
                        TAG,
                        "%s: couldn't find the method %s",
                        __PRETTY_FUNCTION__,
                        METHOD_NAME);
    return false;
  }
  return true;
}

void CAndroidDnsConfiguration::ReleaseJniResources()
{
  iGetDnsAddresses = nullptr;
  ReleaseProxy();
}

NNavKit::NAdaptation::IDnsConfiguration* CAndroidDnsConfiguration::OnAcquire()
{
  return this;
}

void CAndroidDnsConfiguration::OnRelease()
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s", __PRETTY_FUNCTION__);
}

std::vector<std::string> CAndroidDnsConfiguration::GetDnsAddresses() const
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s", __PRETTY_FUNCTION__);

  CJniEnvFromAttachedThread jniEnvFromAttachedThread(RetrieveVM());
  if (!jniEnvFromAttachedThread.IsAttached())
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not get JNI Environment");
    return {};
  }

  JNIEnv* env = jniEnvFromAttachedThread.GetJniEnv();
  assert(env != nullptr);

  CJniArrayPtr retVal(env, static_cast<jobjectArray>(env->CallObjectMethod(mProxy, iGetDnsAddresses)));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR,
                        TAG,
                        "%s: Java exception thrown from %s",
                        __PRETTY_FUNCTION__,
                        METHOD_NAME);
    return {};
  }

  const size_t arrayLength = retVal.GetArrayLength();
  if (arrayLength == 0)
  {
    __android_log_print(
      ANDROID_LOG_DEBUG, TAG, "%s: %s returned empty array", __PRETTY_FUNCTION__, METHOD_NAME);
    return {};
  }
  std::vector<std::string> hosts;
  hosts.reserve(arrayLength);

  for (size_t i = 0; i < arrayLength; ++i)
  {
    CJniStringPtr strPtr(env, (jstring) (env->GetObjectArrayElement(retVal, i)));
    if (CJniException::Occured(env))
    {
      __android_log_print(ANDROID_LOG_ERROR,
                          TAG,
                          "%s: Java exception thrown from %s",
                          __PRETTY_FUNCTION__,
                          METHOD_NAME);
      return {};
    }

    if (strPtr.GetStringUTFChars() != nullptr && strPtr.GetStringUTFLength() > 0)
    {
      hosts.emplace_back(strPtr.GetStringUTFChars(), strPtr.GetStringUTFLength());

      __android_log_print(ANDROID_LOG_DEBUG,
                          TAG,
                          "%s: %s returned host %s",
                          __PRETTY_FUNCTION__,
                          METHOD_NAME,
                          hosts.back().c_str());
    }
  }
  return hosts;
}
} // namespace NAdaptation
} // namespace NProcessHost
