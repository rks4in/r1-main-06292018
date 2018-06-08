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

#include "AndroidJniUtil.h"
#include "jni.h"
#include "AndroidJniResourceManagement.h"
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>

using namespace NJniUtils;

static const char* navKitContextStoreClassname = "com/tomtom/navkit/NavKitContextStore";
static const char* getContextMethod = "getContext";
static char const* TAG = "navkit-AndroidJniUtil";

CAndroidJNIUtil::CAndroidJNIUtil(JavaVM *aVm)
  : mProxy (NULL)
  , iVm (aVm)
{
}

JavaVM* CAndroidJNIUtil::RetrieveVM() const
{
  return iVm;
}

JNIEnv* CAndroidJNIUtil::RetrieveEnv() const
{
  JNIEnv *env = 0;

  if (iVm == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: VM is NULL\n", __FUNCTION__);
    return NULL;
  }

  jint getEnvResult = iVm->GetEnv((void **)(&env), JNI_VERSION_1_6);
  if (getEnvResult != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: GetEnv() returned error %d\n",
        __FUNCTION__, getEnvResult);
  }

  return env;
}

bool CAndroidJNIUtil::CreateProxyWithContext(const char* aClassName)
{
  JNIEnv *env = RetrieveEnv();
  if ((aClassName == NULL) || (env == NULL) || (mProxy != NULL))
  {
    return false;
  }

  // Create Context object
  CJniJClassPtr navKitContextStoreClass(env, env->FindClass(navKitContextStoreClassname));
  if (navKitContextStoreClass == NULL)
  {
    __android_log_print (ANDROID_LOG_ERROR, TAG, "%s: class not found: %s\n",
        __FUNCTION__, navKitContextStoreClassname);
    return false;
  }
  jmethodID methodId = env->GetStaticMethodID(navKitContextStoreClass, getContextMethod,
      "()Landroid/content/Context;");
  if (methodId == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: <init> of %s not found\n",
        __FUNCTION__, navKitContextStoreClassname);
    return false;
  }
  CJniObjectPtr jContext(env, env->CallStaticObjectMethod(navKitContextStoreClass, methodId));

  // Create proxy object, passing Context to constructor
  CJniJClassPtr proxyClass(env, env->FindClass(aClassName));
  if (proxyClass == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: class not found: %s\n",
        __FUNCTION__, aClassName);
    return false;
  }
  methodId = env->GetMethodID(proxyClass, "<init>", "(Landroid/content/Context;)V");
  if (methodId == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: <init> with Context of %s not found\n",
        __FUNCTION__, aClassName);
    return false;
  }
  CJniObjectPtr jResult(env, env->NewObject(proxyClass, methodId, static_cast<jobject>(jContext)));
  if (jResult == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: cannot create proxy %s\n",
        __FUNCTION__, aClassName);
    return false;
  }

  mProxy = env->NewGlobalRef(jResult);
  if (mProxy == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: out of memory creating proxy %s\n",
        __FUNCTION__, aClassName);
    return false;
  }

  return true;
}

bool CAndroidJNIUtil::CreateProxy(const char *aClassName)
{
  JNIEnv *env = RetrieveEnv();
  if ((aClassName == NULL) || (env == NULL) || (mProxy != NULL))
  {
    return false;
  }

  CJniJClassPtr proxyClass(env, env->FindClass(aClassName));
  if (proxyClass == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: class not found: %s\n",
        __FUNCTION__, aClassName);
    return false;
  }
  jmethodID methodId = env->GetMethodID(proxyClass, "<init>", "()V");
  if (methodId == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: <init> of %s not found\n",
        __FUNCTION__, aClassName);
    return false;
  }

  CJniObjectPtr jResult(env, env->NewObject(proxyClass, methodId));
  if (jResult == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: cannot create proxy %s\n",
        __FUNCTION__, aClassName);
    return false;
  }

  mProxy = env->NewGlobalRef(jResult);
  if (mProxy == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: out of memory creating proxy %s\n",
        __FUNCTION__, aClassName);
    return false;
  }

  return true;
}

void CAndroidJNIUtil::ReleaseProxy()
{
  JNIEnv *env = RetrieveEnv();
  if ((env == NULL) || (mProxy == NULL))
  {
    mProxy = NULL;
    return;
  }

  __android_log_write(ANDROID_LOG_DEBUG, TAG, "Releasing Java proxy object");

  env->DeleteGlobalRef(mProxy);
  if (CJniException::Occured(env))
  {
    __android_log_write(ANDROID_LOG_ERROR, TAG, "Could not delete Java proxy object");
  }

  mProxy = NULL;
}
