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

#include "AndroidResourceCategoryLayout.h"
#include "AndroidJniResourceManagement.h"
#include "AdaptationsMutexLocker.h"

#include <android/log.h>
#include <ctype.h>
#include <cassert>
#include <sstream>
#include <iostream>
#include <sys/types.h>
#include <sys/stat.h>


static char const* TAG        = "CAndroidResourceCategory";
static char const* CLASS_NAME = "com/tomtom/navkit/adaptations/AndroidResourceCategoryLayout";


namespace
{
  using namespace NJniUtils;
  jmethodID GetMethodID(JNIEnv *aEnv, CJniJClassPtr& aProxyClass, const char* aMethodName)
  {
    return aEnv->GetMethodID(aProxyClass, aMethodName, "()Ljava/lang/String;");
  }
}


namespace NProcessHost
{
namespace NAdaptation
{
using namespace NJniUtils;

CAndroidResourceCategoryLayout::CAndroidResourceCategoryLayout(JavaVM *aVM)
      : CAndroidJNIUtil(aVM)
      , iMethodIDs()
      , iDirectories()
{
}

CAndroidResourceCategoryLayout::~CAndroidResourceCategoryLayout()
{
}

bool CAndroidResourceCategoryLayout::AcquireJniResources()
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

  bool result = true;
  result = result && ((iMethodIDs[EResourceCategoryShared]
                       = GetMethodID(env, proxyClass,"getCategorySharedDirectory")) != NULL);
  result = result && ((iMethodIDs[EResourceCategoryTemporary]
                       = GetMethodID(env, proxyClass, "getCategoryTemporaryDirectory")) != NULL) ;
  result = result && ((iMethodIDs[EResourceCategoryState]
                       = GetMethodID(env, proxyClass, "getCategoryStateDirectory")) != NULL);
  result = result && ((iMethodIDs[EResourceCategoryConfiguration]
                       = GetMethodID(env, proxyClass, "getCategoryConfigurationDirectory")) != NULL);
  result = result && ((iMethodIDs[EResourceCategoryRuntime]
                       = GetMethodID(env, proxyClass, "getCategoryRuntimeDirectory")) != NULL);
  result = result && ((iMethodIDs[EResourceCategoryMaps]
                       = GetMethodID(env, proxyClass, "getCategoryMapsDirectory")) != NULL);
  result = result && ((iMethodIDs[EResourceCategoryPOIs]
                       = GetMethodID(env, proxyClass, "getCategoryPOIsDirectory")) != NULL);
  result = result && ((iMethodIDs[EResourceCategorySpeedCameras]
                       = GetMethodID(env, proxyClass, "getCategorySpeedCamerasDirectory")) != NULL);

  return result;
}

void CAndroidResourceCategoryLayout::ReleaseJniResources()
{
  ReleaseProxy();
  for(int i = 0; i < ENumberOfDirectoriesValue; i++)
  {
    iMethodIDs[i] = NULL;
  }
}

const char* CAndroidResourceCategoryLayout::GetDirectory(TResourceCategory aCategory) const
{
  switch (aCategory)
  {
  case EResourceCategoryShared:
  case EResourceCategoryTemporary:
  case EResourceCategoryState:
  case EResourceCategoryConfiguration:
  case EResourceCategoryRuntime:
  case EResourceCategoryMaps:
  case EResourceCategoryPOIs:
  case EResourceCategorySpeedCameras:
    return iDirectories[aCategory].c_str();

  default:
    return NULL;
  }
}
  
bool CAndroidResourceCategoryLayout::SetDirectory(JNIEnv *aEnv, std::string& aDirectory, jmethodID aMethod)
{
  CJniStringPtr retVal(
      aEnv,
      static_cast<jstring>(aEnv->CallObjectMethod(mProxy,aMethod)));

  if (CJniException::Occured(aEnv))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: Java exception thrown from iGetMachineUniqueIdMethodId\n", __FUNCTION__);
    return false;
  }
  aDirectory = retVal.GetStringUTFChars();

  return true;
}

void CAndroidResourceCategoryLayout::EnsureDirectoryExists(const std::string& aRelPath)
{
  std::istringstream stream(aRelPath);
  std::string token;
  std::string path = "";
  while (getline(stream, token, '/'))
  {
    path = path + token + "/";
    mkdir(path.c_str(), S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
  }
}

NNavKit::NAdaptation::IResourceCategoryLayout* CAndroidResourceCategoryLayout::OnAcquire()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  int status;
  JNIEnv *env;
  status = RetrieveVM()->AttachCurrentThread(&env, NULL);
  if (status != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not attach to thread");
    return NULL;
  }

  env = RetrieveEnv();
  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,
      "%s: Could not retrieve environment (%s)\n",
      __FUNCTION__,
      (const char*) ((env == NULL) ? "environment == NULL" : "mProxy == NULL")
    );
    return NULL;
  }

  for (int i = 0; i < ENumberOfDirectoriesValue; i++)
  {
    if (SetDirectory(env, iDirectories[i], iMethodIDs[i]))
    {
      EnsureDirectoryExists(iDirectories[i]);
    }
  }

  __android_log_print(ANDROID_LOG_DEBUG, TAG, "CAndroidResourceCategoryLayout::OnAcquire:");

  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   Shared        Path = %s    ",
                      iDirectories[EResourceCategoryShared].c_str());

  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   Temporary     Path = %s    ",
                      iDirectories[EResourceCategoryTemporary].c_str());

  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   State         Path = %s    ",
                      iDirectories[EResourceCategoryState].c_str());

  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   Configuration Path = %s    ",
                      iDirectories[EResourceCategoryConfiguration].c_str());

  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   Runtime       Path = %s    ",
                      iDirectories[EResourceCategoryRuntime].c_str());

  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   Maps          Path = %s    ",
                      iDirectories[EResourceCategoryMaps].c_str());

  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   POIs          Path = %s    ",
                      iDirectories[EResourceCategoryPOIs].c_str());

  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   SpeedCameras  Path = %s    ",
                      iDirectories[EResourceCategorySpeedCameras].c_str());

  return this;
}

void CAndroidResourceCategoryLayout::OnRelease()
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "CAndroidResourceCategoryLayout::OnRelease:");
}

} // NAdaptation 
} // NProcessHost
