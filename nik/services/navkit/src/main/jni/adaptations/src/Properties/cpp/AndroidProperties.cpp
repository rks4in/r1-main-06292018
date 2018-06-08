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

#include "AndroidProperties.h"
#include "AndroidJniResourceManagement.h"
#include "AdaptationsMutexLocker.h"

#include <android/log.h>
#include <ctype.h>
#include <cassert>
#include <sstream>
#include <iostream>
#include <sys/types.h>
#include <sys/stat.h>

static char const* TAG        = "CAndroidProperties";
static char const* CLASS_NAME = "com/tomtom/navkit/adaptations/AndroidProperties";

namespace NProcessHost
{
namespace NAdaptation
{
using namespace NJniUtils;

CAndroidProperties::CAndroidProperties(JavaVM *aVM)
      : CAndroidJNIUtil(aVM)
      , iGetWorkingDirectoryID (NULL)
      , iGetPersistentDirectoryID(NULL)
      , iGetPrivateContentDirectoryID(NULL)
      , iGetSharedContentDirectoryID(NULL)
      , iGetTemporaryFilesDirectoryID(NULL)
      , iGetReflectionListenerAddressID(NULL)
{
}

CAndroidProperties::~CAndroidProperties()
{
  ReleaseJniResources();
}

unsigned short int CAndroidProperties::GetListenerPort() const
{
  return 0u;
}

std::vector<std::string> CAndroidProperties::GetReflectionListenerAddresses() const
{
  std::vector<std::string> addresses;
  addresses.push_back(iReflectionListenerAddress);
  return addresses;
}

bool CAndroidProperties::AcquireJniResources()
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

  const char* getWorkingDirectoryMethodName = "getWorkingDirectory";

  iGetWorkingDirectoryID = env->GetMethodID(proxyClass,getWorkingDirectoryMethodName,"()Ljava/lang/String;");
  if (CJniException::Occured(env) || (iGetWorkingDirectoryID == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, getWorkingDirectoryMethodName);
    return false;
  }

  const char* getPersistentDirectoryMethodName = "getPersistentDirectory";

  iGetPersistentDirectoryID = env->GetMethodID(proxyClass,getPersistentDirectoryMethodName,"()Ljava/lang/String;");
  if (CJniException::Occured(env) || (iGetPersistentDirectoryID == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, getPersistentDirectoryMethodName);
    return false;
  }

  const char* getPrivateContentDirectoryMethodName = "getPrivateContentDirectory";

  iGetPrivateContentDirectoryID = env->GetMethodID(proxyClass,getPrivateContentDirectoryMethodName,"()Ljava/lang/String;");
  if (CJniException::Occured(env) || (iGetPrivateContentDirectoryID == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, getPrivateContentDirectoryMethodName);
    return false;
  }

  const char* getSharedContentDirectoryMethodName = "getSharedContentDirectory";

  iGetSharedContentDirectoryID = env->GetMethodID(proxyClass,getSharedContentDirectoryMethodName,"()Ljava/lang/String;");
  if (CJniException::Occured(env) || (iGetSharedContentDirectoryID == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, getSharedContentDirectoryMethodName);
    return false;
  }

  const char* getTemporaryFilesDirectoryMethodName = "getTemporaryFilesDirectory";

  iGetTemporaryFilesDirectoryID = env->GetMethodID(proxyClass,getTemporaryFilesDirectoryMethodName,"()Ljava/lang/String;");
  if (CJniException::Occured(env) || (iGetTemporaryFilesDirectoryID == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, getTemporaryFilesDirectoryMethodName);
    return false;
  }

  const char* getListenerPortMethodName = "getReflectionListenerAddress";

  iGetReflectionListenerAddressID = env->GetMethodID(proxyClass,getListenerPortMethodName,"()Ljava/lang/String;");
  if (CJniException::Occured(env) || (iGetReflectionListenerAddressID == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s: couldn't find the method %s\n", __PRETTY_FUNCTION__, getListenerPortMethodName);
    return false;
  }

  return true;
}

void CAndroidProperties::ReleaseJniResources()
{
  ReleaseProxy();
  iGetWorkingDirectoryID          = NULL;
  iGetPersistentDirectoryID       = NULL;
  iGetPrivateContentDirectoryID   = NULL;
  iGetSharedContentDirectoryID    = NULL;
  iGetTemporaryFilesDirectoryID   = NULL;
  iGetReflectionListenerAddressID = NULL;
}

bool CAndroidProperties::SetStringValueFromJavaMehod(JNIEnv *aEnv, std::string& aPath, jmethodID aMethod)
{
  CJniStringPtr retVal(
      aEnv,
      static_cast<jstring>(aEnv->CallObjectMethod(mProxy,aMethod)));

  if (CJniException::Occured(aEnv))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: Java exception thrown from iGetMachineUniqueIdMethodId\n", __FUNCTION__);
    return false;
  }
  aPath = retVal.GetStringUTFChars();

  return true;
}

void CAndroidProperties::EnsureDirectoryExists(const std::string& aRelPath)
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

NNavKit::NAdaptation::IProperties* CAndroidProperties::OnAcquire()
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

  ///////////////////////////////////////////////////////////////////////////////////////////

  if (SetStringValueFromJavaMehod(env, iWorkingPath, iGetWorkingDirectoryID))
  {
    EnsureDirectoryExists(iWorkingPath);
  }
  if (SetStringValueFromJavaMehod(env, iPersistentPath, iGetPersistentDirectoryID))
  {
    EnsureDirectoryExists(iPersistentPath);
  }
  if (SetStringValueFromJavaMehod(env, iPrivateContentPath, iGetPrivateContentDirectoryID))
  {
    EnsureDirectoryExists(iPrivateContentPath);
  }
  if (SetStringValueFromJavaMehod(env, iSharedContentPath, iGetSharedContentDirectoryID))
  {
    EnsureDirectoryExists(iSharedContentPath);
  }
  if (SetStringValueFromJavaMehod(env, iTemporaryFilesPath, iGetTemporaryFilesDirectoryID))
  {
    EnsureDirectoryExists(iTemporaryFilesPath);
  }
  SetStringValueFromJavaMehod(env, iReflectionListenerAddress, iGetReflectionListenerAddressID);

  __android_log_print(ANDROID_LOG_DEBUG, TAG, "CAndroidProperties::OnAcquire:");
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   iWorkingPath = %s    ",iWorkingPath.c_str());
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   iPersistentPath = %s    ",iPersistentPath.c_str());
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   iPrivateContentPath = %s    ",iPrivateContentPath.c_str());
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   iSharedContentPath = %s    ",iSharedContentPath.c_str());
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   iTemporaryFilesPath = %s    ",iTemporaryFilesPath.c_str());
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "   iReflectionListenerAddress = %s    ",iReflectionListenerAddress.c_str());

  return this;
}

void CAndroidProperties::OnRelease()
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "CAndroidProperties::OnRelease:");
}

} // NAdaptation 
} // NProcessHost
