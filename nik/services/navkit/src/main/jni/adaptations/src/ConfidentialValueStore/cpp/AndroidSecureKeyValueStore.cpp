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

#include "AndroidSecureKeyValueStore.h"
#include "AndroidJniResourceManagement.h"
#include "AdaptationsMutexLocker.h"

#include <cassert>
#include <android/log.h>


static char const* TAG = "AndroidSecureKeyValueStore";
static char const* CLASS_NAME = "com/tomtom/navkit/adaptations/AndroidSecureKeyValueStore";

namespace NProcessHost
{
namespace NAdaptation
{
using namespace NJniUtils;

CAndroidSecureKeyValueStore::CAndroidSecureKeyValueStore(JavaVM *aVM)
: CAndroidJNIUtil(aVM)
, iStoreValueMethodId(NULL)
, iRetrieveValueMethodId(NULL)
{
}


CAndroidSecureKeyValueStore::~CAndroidSecureKeyValueStore()
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "destructor (%p)", this);
}


void CAndroidSecureKeyValueStore::ReleaseJniResources()
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "ReleaseJniResources (%p)", this);
  ReleaseProxy();
  iStoreValueMethodId    = NULL;
  iRetrieveValueMethodId = NULL;
};

bool CAndroidSecureKeyValueStore::AcquireJniResources()
{
  JNIEnv *env = NULL;
  assert (mProxy == NULL);

  if (mProxy != NULL)
  {
    return false;
  }

  if (!CreateProxyWithContext(CLASS_NAME))
  {
    return false;
  }

  env = RetrieveEnv();
  if (env == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "RetrieveEnv failed!\n");
    return false;
  }

  CJniJClassPtr proxyClass(env, env->GetObjectClass(mProxy));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not retrieve proxy object\n", __FUNCTION__);
    return false;
  }

  const char* storeValueMethodName = "storeValue";
  iStoreValueMethodId = env->GetMethodID(proxyClass, storeValueMethodName, "(Ljava/lang/String;[B)Z");
  if (CJniException::Occured(env) || (iStoreValueMethodId == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __FUNCTION__, storeValueMethodName);
    return 0;
  }

  const char* retrieveValueMethodName = "retrieveValue";
  iRetrieveValueMethodId = env->GetMethodID(proxyClass, retrieveValueMethodName, "(Ljava/lang/String;)[B");
  if (CJniException::Occured(env) || (iRetrieveValueMethodId == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __FUNCTION__, retrieveValueMethodName);
    return 0;
  }

  return true;
}


NNavKit::NAdaptation::IControlAdaptation* CAndroidSecureKeyValueStore::OnAcquire()
{
  // No need to guard this with iMutex
  return this;
}


void CAndroidSecureKeyValueStore::OnRelease()
{
  // No need to guard this with iMutex
}


bool CAndroidSecureKeyValueStore::StoreValue(const char* aKey, const void* aValue, const int aValueSize)
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  assert(aKey != NULL);
  assert(aValue != NULL);
  assert(aValueSize >= 0);
  __android_log_print(ANDROID_LOG_VERBOSE, TAG, "StoreValue(%s): mProxy == %p", aKey, mProxy);

  JNIEnv *env = NULL;
  int status = RetrieveVM()->AttachCurrentThread(&env, NULL);
  if (status != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not attach to thread");
    return false;
  }

  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,
      "%s: Could not retrieve environment (%s)\n",
      __FUNCTION__,
      (const char*) ((env == NULL) ? "environment == NULL" : "mProxy == NULL")
    );
    return false;
  }

  // Call Java method "boolean storeValue(String alias, byte[] keyValues)"

  CJniStringPtr jKey(env, env->NewStringUTF(aKey));
  const int arraySize = aValueSize ? aValueSize : 1;

  CJniJPtr<_jbyteArray> jBytes(env, env->NewByteArray(arraySize));
  if (jBytes == NULL)
  {
    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s: couldn't create byte array\n", __FUNCTION__);
    return false;
  }
  env->SetByteArrayRegion(jBytes, 0, arraySize, aValueSize ? (jbyte*)aValue : (jbyte*)"");

  jboolean result = env->CallBooleanMethod(mProxy, iStoreValueMethodId, static_cast<jstring>(jKey), static_cast<jbyteArray>(jBytes));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Java exception thrown from iStoreValueMethodId\n", __FUNCTION__);
    return false;
  }

  return (result == JNI_FALSE ? false : true);
}


/*
 * RetrieveValue will return the size of the value that has been retrieved.
 * If the buffer is to small, the size of the retrieved value will be returned.
 * In all other cases (no key/error) -1 is returned.
 */
int CAndroidSecureKeyValueStore::RetrieveValue(const char* aKey, void* aValue, const int aValueSize)
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  assert(aKey != NULL);
  assert(aValue != NULL);
  assert(aValueSize > 0);
  __android_log_print(ANDROID_LOG_VERBOSE, TAG, "RetrieveValue(%s): mProxy == %p", aKey, mProxy);

  JNIEnv *env = NULL;
  int status = RetrieveVM()->AttachCurrentThread(&env, NULL);
  if (status != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not attach to thread");
    return false;
  }

  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,
      "%s: Could not retrieve environment (%s)\n",
      __FUNCTION__,
      (const char*) ((env == NULL) ? "environment == NULL" : "mProxy == NULL")
    );
    return -1;
  }

  // Call Java method "byte[] retrieveValue(String alias)"

  CJniStringPtr jKey(env, env->NewStringUTF(aKey));

  CJniJPtr<_jbyteArray> jBytes(env, static_cast<jbyteArray>(env->CallObjectMethod(mProxy, iRetrieveValueMethodId, static_cast<jstring>(jKey))));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Java exception thrown from iRetrieveValueMethodId\n", __FUNCTION__);
    return -1;
  }
  if (jBytes == NULL)
  {
    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s: couldn't retrieve value\n", __FUNCTION__);
    return -1;
  }

  jint len = env->GetArrayLength(jBytes);
  if (aValueSize < len)
  {
    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s: Key does not fit in supplied buffer\n", __FUNCTION__);
  }
  else
  {
    env->GetByteArrayRegion(jBytes, 0, len, (jbyte *)aValue);
  }

  return len;
}

} // NAdaptation
} // NProcessHost
