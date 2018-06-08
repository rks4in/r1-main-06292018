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

#include "RemovableMediaStateNotifierByAndroidIntent.h"
#include "RemovableMediaState.h"
#include "AndroidJniResourceManagement.h"
#include <android/log.h>
#include <string>
#include <cassert>

using namespace NProcessHost::NAdaptation;
using namespace NNavKit::NAdaptation;

static char const* TAG        = "CRemovableMediaStateNotifierByAndroidIntent";

extern "C" JNIEXPORT void JNICALL
Java_com_tomtom_navkit_adaptations_RemovableMediaStateNotifierByAndroidIntent_informNavKit(
  JNIEnv* aEnvironment,
  jclass aClass,
  jlong aRemovableMediaStateNotifier,
  jboolean aMounted,
  jstring aPath
)
{
  assert(aRemovableMediaStateNotifier != 0);
  if (aRemovableMediaStateNotifier != 0)
  {
    CRemovableMediaStateNotifierByAndroidIntent* removableMediaStateNotifier =
      reinterpret_cast<CRemovableMediaStateNotifierByAndroidIntent*>(aRemovableMediaStateNotifier)
    ;
    assert(removableMediaStateNotifier != NULL);
    if (removableMediaStateNotifier != NULL)
    {
      // Convert mounted
      TiRemovableMediaNotificationType notificationType = EiRemovableMediaNotificationTypeUnmounted;
      if (aMounted)
      {
        notificationType = EiRemovableMediaNotificationTypeMounted;
      }

      // Convert path
      const char *pPath = aEnvironment->GetStringUTFChars(aPath, NULL);

      if (pPath != NULL)
      {
        const std::string path(pPath);
        aEnvironment->ReleaseStringUTFChars(aPath, pPath);
        removableMediaStateNotifier->UpdateState(notificationType, path);
      }
      else
      {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: GetStringUTFChars returned NULL",
            __FUNCTION__);
      }
    }
  }
}

using namespace NJniUtils;

static char const* CLASS_NAME = "com/tomtom/navkit/adaptations/RemovableMediaStateNotifierByAndroidIntent";

CRemovableMediaStateNotifierByAndroidIntent::CRemovableMediaStateNotifierByAndroidIntent(
  JavaVM* aVM
)
: CAndroidJNIUtil(aVM)
{
  GetState().SetNumberOfRemovableMedia(0);  // set initial state change
}

CRemovableMediaStateNotifierByAndroidIntent::~CRemovableMediaStateNotifierByAndroidIntent()
{
  __android_log_write(ANDROID_LOG_DEBUG, TAG, "destructor");
}

bool CRemovableMediaStateNotifierByAndroidIntent::AcquireJniResources()
{
  assert(mProxy == NULL);

  if (mProxy != NULL)
  {
    return false;
  }

  // Find the Java class & create an object.
  if (!CreateProxyWithContext(CLASS_NAME))
  {
    return false;
  }

  JNIEnv *env = RetrieveEnv();
  if (env == NULL)
  {
    return false;
  }

  __android_log_write(ANDROID_LOG_DEBUG, TAG, "::Construct: created & initialized the Java proxy object");

  return AttachToJavaClass();
}

void CRemovableMediaStateNotifierByAndroidIntent::ReleaseJniResources()
{
  DetachFromJavaClass();
  ReleaseProxy();
}

bool CRemovableMediaStateNotifierByAndroidIntent::AttachToJavaClass()
{
  JNIEnv *env = RetrieveEnv();
  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not attach observer\n", __PRETTY_FUNCTION__);
    return false;
  }

  CJniJClassPtr proxyClass (env, env->GetObjectClass(mProxy));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not retrieve proxy object\n", __PRETTY_FUNCTION__);
    return false;
  }

  const char* AttachNotifierMethodName = "AttachNotifier";
  jmethodID AttachNotifierMethodId = env->GetMethodID(proxyClass, AttachNotifierMethodName, "(J)V");

  if (CJniException::Occured(env) || (AttachNotifierMethodId == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, AttachNotifierMethodName);
    return false;
  }

  __android_log_print(ANDROID_LOG_DEBUG, TAG,"%s: calling the Java method %s\n", __FUNCTION__, AttachNotifierMethodName);
  env->CallVoidMethod(mProxy, AttachNotifierMethodId,reinterpret_cast<jlong>(this));

  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Java exception thrown from %s\n", __PRETTY_FUNCTION__, AttachNotifierMethodName);
    return false;
  }
  return true;
}

void CRemovableMediaStateNotifierByAndroidIntent::DetachFromJavaClass()
{
  JNIEnv *env = RetrieveEnv();
  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not detach\n", __PRETTY_FUNCTION__);
    return;
  }

  CJniJClassPtr proxyClass (env, env->GetObjectClass(mProxy));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not retrieve proxy object\n", __PRETTY_FUNCTION__);
    return;
  }

  static const char* METHOD_NAME = "DetachNotifier";
  jmethodID DetachNotifierMethodId = env->GetMethodID(proxyClass, METHOD_NAME, "(J)V");

  if (CJniException::Occured(env) || (DetachNotifierMethodId == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, METHOD_NAME);
    return;
  }

  __android_log_print(ANDROID_LOG_DEBUG, TAG,"%s: calling the Java method %s\n", __FUNCTION__, METHOD_NAME);
  env->CallVoidMethod(mProxy,DetachNotifierMethodId,reinterpret_cast<jlong>(this));

  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Java exception thrown from %s\n", __PRETTY_FUNCTION__, METHOD_NAME);
    return;
  }
}

void CRemovableMediaStateNotifierByAndroidIntent::UpdateState(
    TiRemovableMediaNotificationType aNotificationType, const std::string& aPath)
{
  __android_log_write(ANDROID_LOG_DEBUG, TAG, "::UpdateState");

  CRemovableMediaState& state = GetState();

  // We may be overwriting a previous notification that has not been propagated yet.
  // During NavKit initialization this should be safe.
  // After NavKit startup it should be rare, but would be a race condition issue.
  state.SetNotificationType(aNotificationType);
  state.SetPath(aPath);
  // Note: state.SetNumberOfRemovableMedia() is not used and ignored here.

  NotifyObserver();
}
