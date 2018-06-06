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

#include "RDSTMCServiceAndroid.h"

#include "AndroidJniResourceManagement.h"
#include <android/log.h>
#include <cassert>

static const char* proxyClassName = "com/tomtom/navkit/adaptations/RDSTMCServiceProxy";
static const char* TAG = "CRDSTMCServiceAndroid";
static const char* onConstructMethod = "onConstruct";
static const char* onDestructMethod = "onDestruct";
static const char* startRemoteServiceMethod = "startRemoteService";
static const char* stopRemoteServiceMethod = "stopRemoteService";


extern "C" JNIEXPORT void JNICALL Java_com_tomtom_navkit_adaptations_RDSTMCServiceProxy_RdsTmcReceiverEvent(
  JNIEnv *aEnv,
  jobject thiz,
  jlong aRDSTMCService,
  jboolean aActive
)
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG,"%s: %s\n", __FUNCTION__, aActive ? "Active" : "Not Active");

  assert(aRDSTMCService != 0);
  if (aRDSTMCService != 0)
  {
    NRDSTMC::CRDSTMCServiceAndroid* rdstmcService = reinterpret_cast<NRDSTMC::CRDSTMCServiceAndroid*>(aRDSTMCService);
    if (aActive)
    {
      rdstmcService->OnReceiverConnected();
    }
    else
    {
      rdstmcService->OnReceiverDisconnected();
    }
  }
  else
  {
    __android_log_print(ANDROID_LOG_DEBUG, TAG,"%s: ERROR: aRDSTMCService %d\n", __FUNCTION__, (int)aRDSTMCService);
  }
}

using namespace NJniUtils;
using namespace NRDSTMC;

CRDSTMCServiceAndroid::CRDSTMCServiceAndroid(JavaVM* aVM)
: CRDSTMCService()
, CAndroidJNIUtil(aVM)
, iStartRemoteServiceMethodID(NULL)
, iStopRemoteServiceMethodID(NULL)
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s\n", __FUNCTION__);
}

CRDSTMCServiceAndroid::~CRDSTMCServiceAndroid()
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s\n", __FUNCTION__);

  TryStopService();
}


bool CRDSTMCServiceAndroid::AcquireJniResources()
{
  assert(mProxy == NULL);

  if (mProxy != NULL)
  {
    return false;
  }

  if (!CreateProxyWithContext(proxyClassName))
  {
    return false;
  }

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

  jmethodID methodId = env->GetMethodID(proxyClass, onConstructMethod, "(J)V");
  if (CJniException::Occured(env) || (methodId == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not find the method %s\n", __PRETTY_FUNCTION__, onConstructMethod);
    return false;
  }

  __android_log_print(ANDROID_LOG_DEBUG, TAG,"%s: calling the Java method %s\n", __FUNCTION__, onConstructMethod);
  env->CallVoidMethod(mProxy, methodId, reinterpret_cast<jlong>(this));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Java exception thrown from %s\n", __PRETTY_FUNCTION__, onConstructMethod);
    return NULL;
  }
  /////////////////////////////////////////////////////////////////////////

  iStartRemoteServiceMethodID = env->GetMethodID(proxyClass,startRemoteServiceMethod,"()V");
  if (CJniException::Occured(env) || (iStartRemoteServiceMethodID == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, startRemoteServiceMethod);
    return false;
  }


  iStopRemoteServiceMethodID = env->GetMethodID(proxyClass,stopRemoteServiceMethod,"()V");
  if (CJniException::Occured(env) || (iStopRemoteServiceMethodID == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, stopRemoteServiceMethod);
    return false;
  }
  return true;
}

void CRDSTMCServiceAndroid::ReleaseJniResources()
{
  DoOnRelease();
  iStartRemoteServiceMethodID = NULL;
  iStopRemoteServiceMethodID  = NULL;
}

NNavKit::NAdaptation::IControlAdaptation* CRDSTMCServiceAndroid::DoOnAcquire()
{
  return this;
}

void CRDSTMCServiceAndroid::DoOnRelease()
{
  if (mProxy != NULL)
  {
    JNIEnv *env = RetrieveEnv();
    if (env == NULL)
    {
      __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not detach observer\n", __PRETTY_FUNCTION__);
      return;
    }

    CJniJClassPtr proxyClass (env, env->GetObjectClass(mProxy));
    if (CJniException::Occured(env))
    {
      __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not retrieve proxy object\n", __PRETTY_FUNCTION__);
      return;
    }

    jmethodID methodId = env->GetMethodID(proxyClass, onDestructMethod, "(J)V");
    if (CJniException::Occured(env) || (methodId == NULL))
    {
      __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Could not find the method %s\n", __PRETTY_FUNCTION__, onDestructMethod);
      return;
    }

    __android_log_print(ANDROID_LOG_DEBUG, TAG,"%s: calling the Java method %s\n", __FUNCTION__, onDestructMethod);
    env->CallVoidMethod(mProxy, methodId, reinterpret_cast<jlong>(this));
    if (CJniException::Occured(env))
    {
      __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Java exception thrown from %s\n", __PRETTY_FUNCTION__, onDestructMethod);
      return;
    }
  }
  ReleaseProxy();
}

bool CRDSTMCServiceAndroid::StartRemoteService()
{
  int status;
  JNIEnv *env;
  status = RetrieveVM()->AttachCurrentThread(&env, NULL);
  if (status != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not attach to thread");
    return false;
  }

  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not start Remote RDSTMC remote service.");
    return false;
  }


  __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s\n", __FUNCTION__);

  // start RdsTmcService
  env->CallVoidMethod(mProxy,iStartRemoteServiceMethodID);
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s, return true\n", __FUNCTION__);
  return true;
}

void CRDSTMCServiceAndroid::StopRemoteService()
{
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
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not stop Remote RDSTMC remote service.");
    return;
  }
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s\n", __FUNCTION__);
  // stop RdsTmcService
  env->CallVoidMethod(mProxy,iStopRemoteServiceMethodID);
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s, return\n", __FUNCTION__);
}
