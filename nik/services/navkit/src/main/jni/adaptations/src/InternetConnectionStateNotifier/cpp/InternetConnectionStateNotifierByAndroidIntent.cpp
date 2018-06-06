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

#include "InternetConnectionStateNotifierByAndroidIntent.h"
#include "AndroidJniResourceManagement.h"
#include <android/log.h>
#include <cassert>

using NNavKit::NAdaptation::CInternetConnectionState;
using namespace NJniUtils;
using namespace NProcessHost::NAdaptation;

// Logger prefix
static const char TAG[] = "CInternetConnectionStateNotifierByAndroidIntent";
// Java class names
static const char KAndroidConnectivityManagerClassName[] = "android/net/ConnectivityManager";
static const char KNotifierAdaptationClassName[]         = "com/tomtom/navkit/adaptations/InternetConnectionStateNotifierByAndroidIntent";
// Android network types
static const char KAndroidWifiType[]      = "TYPE_WIFI";
static const char KAndroidEthernetType[]  = "TYPE_ETHERNET";
static const char KAndroidMobileType[]    = "TYPE_MOBILE";
static const char KAndroidWiMaxType[]     = "TYPE_WIMAX";
static const char KAndroidBluetoothType[] = "TYPE_BLUETOOTH";
static const jint KUnknownJavaField       = -1;

static jint GetStaticInteger(JNIEnv* aEnvironment, jclass cls, const char* name)
{
  jfieldID id = aEnvironment->GetStaticFieldID(cls, name, "I");
  assert(id != NULL);
  if (CJniException::Occured(aEnvironment) || id == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Cannot find static field %s, won't be able to identify this network type\n",
                        __PRETTY_FUNCTION__, name);
    return KUnknownJavaField;
  }
  return aEnvironment->GetStaticIntField(cls, id);
}

static CInternetConnectionState::TNetworkType GetAndroidNetworkType(JNIEnv* aEnvironment,
                                                                    jint aNetworkType,
                                                                    jboolean aRoaming)
{
  static const jclass cls = aEnvironment->FindClass(KAndroidConnectivityManagerClassName);
  if (CJniException::Occured(aEnvironment) || cls == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Cannot find class %s, unable to detect network type\n",
                        __PRETTY_FUNCTION__, KAndroidConnectivityManagerClassName);
    return CInternetConnectionState::ENetworkTypeUnknown;
  }

  static const jint KWifi      = GetStaticInteger(aEnvironment, cls, KAndroidWifiType);
  static const jint KEthernet  = GetStaticInteger(aEnvironment, cls, KAndroidEthernetType);
  static const jint KBluetooth = GetStaticInteger(aEnvironment, cls, KAndroidBluetoothType);
  static const jint KMobile    = GetStaticInteger(aEnvironment, cls, KAndroidMobileType);
  static const jint KWiMax     = GetStaticInteger(aEnvironment, cls, KAndroidWiMaxType);

  if (aNetworkType == KWifi || aNetworkType == KEthernet)
  {
    return CInternetConnectionState::ENetworkTypeWifi;
  }
  else if (aNetworkType == KMobile || aNetworkType == KWiMax)
  {
    if (aRoaming)
    {
      return CInternetConnectionState::ENetworkTypeMobileRoaming;
    }
    else
    {
      return CInternetConnectionState::ENetworkTypeMobile;
    }
  }
  else if (aNetworkType == KBluetooth)
  {
    return CInternetConnectionState::ENetworkTypeTethered;
  }
  return CInternetConnectionState::ENetworkTypeUnknown;
}

extern "C" JNIEXPORT void JNICALL
Java_com_tomtom_navkit_adaptations_InternetConnectionStateNotifierByAndroidIntent_informNavKit(
    JNIEnv* aEnvironment, jclass aClass, jlong aInternetConnectionStateNotifier,
    jboolean aConnected, jint aNetworkType, jboolean aRoaming)
{
  assert(aInternetConnectionStateNotifier != 0);
  if (aInternetConnectionStateNotifier != 0)
  {
    CInternetConnectionStateNotifierByAndroidIntent* internetConnectionStateNotifier
        = reinterpret_cast<CInternetConnectionStateNotifierByAndroidIntent*>(
            aInternetConnectionStateNotifier);
    assert(internetConnectionStateNotifier != NULL);
    if (internetConnectionStateNotifier != NULL)
    {
      CInternetConnectionState::TNetworkType type = GetAndroidNetworkType(aEnvironment, aNetworkType, aRoaming);
      __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s: Detected network type = %d\n", __PRETTY_FUNCTION__, type);
      internetConnectionStateNotifier->SetState(aConnected, type);
    }
  }
}

CInternetConnectionStateNotifierByAndroidIntent::CInternetConnectionStateNotifierByAndroidIntent(
    JavaVM* aVM)
: CAndroidJNIUtil(aVM)
{
}

CInternetConnectionStateNotifierByAndroidIntent::~CInternetConnectionStateNotifierByAndroidIntent()
{
  __android_log_write(ANDROID_LOG_DEBUG, TAG, "destructor");
}

bool CInternetConnectionStateNotifierByAndroidIntent::AcquireJniResources()
{
  assert(mProxy == NULL);

  if (mProxy != NULL)
  {
    return false;
  }

  // Find the Java class & create an object.
  if (!CreateProxyWithContext(KNotifierAdaptationClassName))
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
void CInternetConnectionStateNotifierByAndroidIntent::ReleaseJniResources()
{
  DetachFromJavaClass();
  ReleaseProxy();
};


bool CInternetConnectionStateNotifierByAndroidIntent::AttachToJavaClass()
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
  env->CallVoidMethod(mProxy,AttachNotifierMethodId,reinterpret_cast<jlong>(this));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: Java exception thrown from %s\n", __PRETTY_FUNCTION__, AttachNotifierMethodName);
    return false;
  }
  return true;
}

void CInternetConnectionStateNotifierByAndroidIntent::DetachFromJavaClass()
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

void CInternetConnectionStateNotifierByAndroidIntent::SetState(bool aConnected, CInternetConnectionState::TNetworkType aNetworkType)
{
  if (aConnected)
  {
    GetState().Connect();
  }
  else
  {
    GetState().Disconnect();
  }
  GetState().SetNetworkType(aNetworkType);
  NotifyObserver();
}
