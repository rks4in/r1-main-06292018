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

#include "TimeZoneChangedObserverByAndroidIntent.h"
#include "AndroidJniResourceManagement.h"
#include "AdaptationsMutexLocker.h"
#include <android/log.h>
#include <ctype.h>
#include <cassert>
#include <cstring>

static char const* TAG        = "TimeZoneChangedObserverByAndroidIntent";
static char const* CLASS_NAME = "com/tomtom/navkit/adaptations/TimeZoneChangedObserverByAndroidIntent";

namespace NProcessHost
{
namespace NAdaptation
{
using namespace NJniUtils;

CTimeZoneChangedObserverByAndroidIntent::CTimeZoneChangedObserverByAndroidIntent(
  JavaVM* aVM,
  const char* aReceiver,
  const char* aIntentName
)
: CAndroidJNIUtil(aVM)
, iTimeZoneMethodID(NULL)
{
  assert(aReceiver != NULL);
  assert(aIntentName != NULL);
  iReceiver.assign(aReceiver);
  iIntentName.assign(aIntentName);
}

CTimeZoneChangedObserverByAndroidIntent::~CTimeZoneChangedObserverByAndroidIntent()
{
  __android_log_write(ANDROID_LOG_DEBUG, TAG, "destructor");
}

bool CTimeZoneChangedObserverByAndroidIntent::AcquireJniResources()
{
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

  const char* timeZoneMethodName = "notifyTimeZoneChanged";

  iTimeZoneMethodID = env->GetMethodID(proxyClass,timeZoneMethodName,"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
  if (CJniException::Occured(env) || (iTimeZoneMethodID == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, timeZoneMethodName);
    return false;
  }

  return true;
}

void CTimeZoneChangedObserverByAndroidIntent::ReleaseJniResources()
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "ReleaseJniResources (%p)", this);
  ReleaseProxy();
  iTimeZoneMethodID = NULL;
};


NNavKit::NAdaptation::IControlAdaptation* CTimeZoneChangedObserverByAndroidIntent::OnAcquire()
{
  // No need to guard this with iMutex
  return this;
}

void CTimeZoneChangedObserverByAndroidIntent::OnRelease()
{
  // No need to guard this with iMutex
}

void CTimeZoneChangedObserverByAndroidIntent::NotifyTimeZoneChanged(const char* aTimeZone)
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  int status;
  JNIEnv *env;
  status = RetrieveVM()->AttachCurrentThread(&env, NULL);
  if (status != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not attach to thread");
    return;
  }

  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not notify time-zone change %s", aTimeZone);
    return;
  }

  CJniStringPtr jReceiver(env, env->NewStringUTF(iReceiver.c_str()));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not allocate string (Receiver)\n", __PRETTY_FUNCTION__);
    return;
  }

  CJniStringPtr jIntentName(env, env->NewStringUTF(iIntentName.c_str()));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not allocate string (IntentName)\n", __PRETTY_FUNCTION__);
    return;
  }

  CJniStringPtr jTimeZone(env, env->NewStringUTF(aTimeZone));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not allocate string (TimeZone)\n", __PRETTY_FUNCTION__);
    return;
  }

  __android_log_print(ANDROID_LOG_DEBUG, TAG,"%s: calling the Java method notifyTimeZoneChanged\n", __FUNCTION__);

  env->CallVoidMethod(mProxy,iTimeZoneMethodID,static_cast<jstring>(jReceiver),
                      static_cast<jstring>(jIntentName), static_cast<jstring>(jTimeZone));

  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Java exception thrown from notifyTimeZoneChanged\n", __PRETTY_FUNCTION__);
    return;
  }
}

} // NAdaptation
} // NProcessHost
