/*!
 * \file
 * \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 * <br>
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

#include "AndroidProxyConfiguration.h"
#include "AndroidJniResourceManagement.h"
#include "AdaptationsMutexLocker.h"
#include "IProxyConfiguration.h"

#include "jni.h"
#include <android/log.h>
#include <cassert>
#include <stdint.h>
#include <string.h>

static const char* TAG        = "CAndroidProxyConfiguration";
static const char* CLASS_NAME = "com/tomtom/navkit/adaptations/AndroidProxyConfiguration";

namespace NProcessHost
{

  namespace NAdaptation
  {

    using namespace NJniUtils;

    CAndroidProxyConfiguration::CAndroidProxyConfiguration(JavaVM *aVM)
      : CAndroidJNIUtil(aVM)
      , iGetProxyConfigurationForUrl(NULL)
    {
    }
  
    CAndroidProxyConfiguration::~CAndroidProxyConfiguration()
    {
    }

    bool CAndroidProxyConfiguration::AcquireJniResources()
    {
      __android_log_print(ANDROID_LOG_DEBUG, TAG, "CAndroidProxyConfiguration::AcquireJniResources");

      JNIEnv *env = NULL;
      assert (mProxy == NULL);

      if (mProxy != NULL)
      {
        return false;
      }

      // Find the Java class & create an object.
      if (!CreateProxyWithContext(CLASS_NAME))
      {
        return false;
      }

      env = RetrieveEnv();
      if (env == NULL)
      {
        return false;
      }

      CJniJClassPtr proxyClass (env, env->GetObjectClass(mProxy));
      if (CJniException::Occured(env))
      {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not retrieve proxy object\n", __PRETTY_FUNCTION__);
        return false;
      }

      const char* getProxyConfigurationForUrlMethodName = "getProxyConfigurationForUrl";
      iGetProxyConfigurationForUrl = env->GetMethodID(proxyClass, getProxyConfigurationForUrlMethodName, "(Ljava/lang/String;)Ljava/lang/String;");
      if (CJniException::Occured(env) || (iGetProxyConfigurationForUrl == NULL))
      {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, getProxyConfigurationForUrlMethodName);
        return false;
      }
      return true;
    }

    void CAndroidProxyConfiguration::ReleaseJniResources()
    {
      iGetProxyConfigurationForUrl = NULL;
      ReleaseProxy();
    }

    NNavKit::NAdaptation::IProxyConfiguration* CAndroidProxyConfiguration::OnAcquire()
    {
      return this;
    }
  
    void CAndroidProxyConfiguration::OnRelease()
    {
      __android_log_print(ANDROID_LOG_DEBUG, TAG, "CAndroidProxyConfiguration::OnRelease");
    }

    namespace
    {
      // RAII object to get the JNI Environment from current thread context.
      // If current thread is already attached, then the environment is just fetched, and
      // when object goes out of scope, the thread is not detached either.
      // If current thread is not yet attached, then it is attached, and when the object
      // goes out of scope, the thread is detached again.
      class CJniEnvFromAttachedThread
      {
      public:
        // Construct RAII object.
        // aJavaVm should not be NULL, and remain valid throughout the lifetime of the object.
        CJniEnvFromAttachedThread(JavaVM* aJavaVm)
          : iNeedToDetach(false)
          , iJavaVm(aJavaVm)
          , iJniEnv(NULL)
        {
          if (iJavaVm != NULL)
          {
            jint status = iJavaVm->GetEnv((void**)&iJniEnv, JNI_VERSION_1_6);

            if (status == JNI_EDETACHED)
            {
              status = iJavaVm->AttachCurrentThread(&iJniEnv, NULL);
              iNeedToDetach = (status == JNI_OK);
            }

            if (status != JNI_OK)
            {
              __android_log_print(ANDROID_LOG_ERROR, TAG,
                  "Could not get JNI Environment or attach thread to VM");
              iJniEnv = NULL;
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

        // Check whether the current thread is properly attached to the VM.
        bool IsAttached() const
        {
          return (iJniEnv != NULL);
        }

        // Get the JNI Environment pointer.
        // May return NULL if currently not attached.
        // The returned JNI Environment is only valid as long as the object it was
        // retrieved from is alive.
        JNIEnv* GetJniEnv() const
        {
          return iJniEnv;
        }

        private:
          bool iNeedToDetach;
          JavaVM* iJavaVm;
          JNIEnv* iJniEnv;
      };
    }

    void CAndroidProxyConfiguration::GetProxyConfigurationForUrl(const char* aUrl, char* aProxyUrl)
    {
      NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);
      __android_log_print(ANDROID_LOG_DEBUG, TAG, "CAndroidProxyConfiguration::GetProxyURLForResourceURL1");
      aProxyUrl[0] = '\0';

      CJniEnvFromAttachedThread jniEnvFromAttachedThread(RetrieveVM());
      if (!jniEnvFromAttachedThread.IsAttached())
      {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not get JNI Environment");
        return;
      }
      JNIEnv* env = jniEnvFromAttachedThread.GetJniEnv();
      assert(env != NULL);

      CJniStringPtr jsUrl(env, env->NewStringUTF(aUrl));
      CJniStringPtr retVal(env, static_cast<jstring>(env->CallObjectMethod(mProxy,
                                                                           iGetProxyConfigurationForUrl,
                                                                           static_cast<jstring>(jsUrl))));
      if (CJniException::Occured(env))
      {
        __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: Java exception thrown from GetProxyConfigurationForUrl\n", __FUNCTION__);
        return;
      }

      const char* retValUTF8Ptr = retVal.GetStringUTFChars();
      if (!retValUTF8Ptr)
      {
        return;
      }
      const size_t cStrSize = strlen(retValUTF8Ptr);
      if (cStrSize + 1 < NNavKit::NAdaptation::IProxyConfiguration::KProxyUrlMaxSize)
      {
        memcpy(aProxyUrl, retValUTF8Ptr, cStrSize);
        aProxyUrl[cStrSize + 1] = '\0';
      }
      else
      {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Proxy string is longer than expected: %zu", cStrSize);
        return;
      }
    }
  } //namespace
} //namespace
