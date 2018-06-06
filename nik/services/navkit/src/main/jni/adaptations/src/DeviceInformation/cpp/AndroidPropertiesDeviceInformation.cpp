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

#include "AndroidPropertiesDeviceInformation.h"
#include "MUIDUtils.h"
#include "AndroidJniResourceManagement.h"
#include "StringUtils.h"
#include "AdaptationsMutexLocker.h"

#include <android/log.h>
#include <algorithm>
#include <cassert>
#include <string>
#include <cctype>
#include <functional>

namespace NProcessHost
{
namespace NAdaptation
{
using namespace NJniUtils;
using namespace NNavKit::NUtils;

static char const* TAG = "AndroidPropertiesDeviceInformation";
static char const* CLASS_NAME = "com/tomtom/navkit/adaptations/AndroidPropertiesDeviceInformation";

// OTA name
static char const* SANTORINI_LIVE = "santorini_live";

CAndroidPropertiesDeviceInformation::CAndroidPropertiesDeviceInformation(JavaVM* aVM)
: CAndroidJNIUtil(aVM)
,iGetMachineUniqueIdMethodId(NULL)
,iGetMediaIdsMethodId(NULL)
,iGetIMEICodeMethodId(NULL)
,iGetICCIDCodeMethodId(NULL)
{
  iMachineUniqueId     [0] = 0;
  iLiveServicesUniqueId[0] = 0;
  iOTAName             [0] = 0;
  iGPSFirmwareVersion  [0] = 0;
  iGPSType             [0] = 0;
  iIMEICode            [0] = 0;
  iICCIDCode           [0] = 0;
}

CAndroidPropertiesDeviceInformation::~CAndroidPropertiesDeviceInformation()
{ 
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "destructor (%p)", this);

}

void CAndroidPropertiesDeviceInformation::ReleaseJniResources()
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "ReleaseJniResources (%p)", this);
  ReleaseProxy();
  iGetMachineUniqueIdMethodId = NULL;
  iGetMediaIdsMethodId        = NULL;
  iGetIMEICodeMethodId        = NULL;
  iGetICCIDCodeMethodId       = NULL;
};

bool CAndroidPropertiesDeviceInformation::AcquireJniResources()
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
    __android_log_print(ANDROID_LOG_ERROR, TAG,"RetrieveEnv failed!\n");
    return false;
  }

  CJniJClassPtr proxyClass(env, env->GetObjectClass(mProxy));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: Could not retrieve proxy object\n", __FUNCTION__);
    return false;
  }
//////////////////////////////////////////////////////////////////////////////////////////////////////
  const char* getMachineUniqueIdMethodName = "getMachineUniqueId";

  iGetMachineUniqueIdMethodId = env->GetMethodID(proxyClass, getMachineUniqueIdMethodName, "()Ljava/lang/String;");
  if (CJniException::Occured(env) || (iGetMachineUniqueIdMethodId == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: couldn't find the method %s\n", __FUNCTION__, getMachineUniqueIdMethodName);
    return 0;
  }

  const char* getSerialNumberMethodName = "getSerialNumber";
  iGetSerialNumberMethodId = env->GetMethodID(proxyClass, getSerialNumberMethodName, "()Ljava/lang/String;");
  if (CJniException::Occured(env) || (iGetSerialNumberMethodId == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: couldn't find the method %s\n", __FUNCTION__, getSerialNumberMethodName);
    return 0;
  }

  const char* getMediaIdsMethodName = "getMediaIds";
  iGetMediaIdsMethodId = env->GetMethodID(proxyClass, getMediaIdsMethodName, "(J)Ljava/lang/String;");
  if (CJniException::Occured(env) || (iGetMediaIdsMethodId == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: couldn't find the method %s\n", __FUNCTION__, getMediaIdsMethodName);
    return 0;
  }

  const char* getIMEICodeMethodName="getIMEICode";
  iGetIMEICodeMethodId = env->GetMethodID(proxyClass, getIMEICodeMethodName, "()Ljava/lang/String;");
  if (NJniUtils::CJniException::Occured(env) || (iGetIMEICodeMethodId == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: couldn't find the method %s\n", __FUNCTION__, getIMEICodeMethodName);
    return 0;
  }

  const char* getICCIDCodeMethodName="getICCIDCode";
  iGetICCIDCodeMethodId = env->GetMethodID(proxyClass, getICCIDCodeMethodName, "()Ljava/lang/String;");
  if (NJniUtils::CJniException::Occured(env) || (iGetICCIDCodeMethodId == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: couldn't find the method %s\n", __FUNCTION__, getICCIDCodeMethodName);
    return 0;
  }
//////////////////////////////////////////////////////////////////////////////////////////////////////
  return true;
}

NNavKit::NAdaptation::IControlAdaptation* CAndroidPropertiesDeviceInformation::OnAcquire()
{
  // No need to guard this with iMutex
  return this;
}

void CAndroidPropertiesDeviceInformation::OnRelease()
{
  // No need to guard this with iMutex
}

uint32_t CAndroidPropertiesDeviceInformation::GetMuidFromHost(char* aBuffer, const uint32_t aLength) const
{
  int status;
  JNIEnv *env;
  status = RetrieveVM()->AttachCurrentThread(&env, NULL);
  if (status != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not attach to thread");
    return -1;
  }

  assert(aLength > 0);
  __android_log_print(ANDROID_LOG_VERBOSE, TAG, "GetMuidFromHost(%p): mProxy == %p", this, mProxy);

  env = RetrieveEnv();
  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,
      "%s: Could not retrieve environment (%s)\n",
      __FUNCTION__,
      (const char*) ((env == NULL) ? "environment == NULL" : "mProxy == NULL")
    );
    return 0;
  }

  CJniStringPtr muid(env, static_cast<jstring>(env->CallObjectMethod(mProxy, iGetMachineUniqueIdMethodId)));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: Java exception thrown from iGetMachineUniqueIdMethodId\n", __FUNCTION__);
    return 0;
  }

  const char* convertedString = muid.GetStringUTFChars();
  if (convertedString == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: GetStringUTFChars returned NULL", __FUNCTION__);
    return 0;
  }

  uint32_t length = std::min(aLength-1, static_cast<uint32_t>(strlen(convertedString))); // -1 reserve space for null-terminator
  memcpy(aBuffer, convertedString, length);
  aBuffer[length] = '\0';
  return length;
}

uint32_t CAndroidPropertiesDeviceInformation::GetSerialNumber(char* aSerialNumber, uint32_t aLength)
{
  assert(aLength > 1);

  JNIEnv *env = NULL;
  int status = RetrieveVM()->AttachCurrentThread(&env, NULL);
  if (status != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not attach to thread");
    return 0;
  }

  __android_log_print(ANDROID_LOG_VERBOSE, TAG, "GetSerialNumber(%p): mProxy == %p", this, mProxy);

  env = RetrieveEnv();
  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,
      "%s: Could not retrieve environment (%s)\n",
      __FUNCTION__,
      (const char*) ((env == NULL) ? "environment == NULL" : "mProxy == NULL")
    );
    return 0;
  }

  CJniStringPtr serialno(env, static_cast<jstring>(env->CallObjectMethod(mProxy, iGetSerialNumberMethodId)));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: Java exception thrown from iGetSerialNumberMethodId\n", __FUNCTION__);
    return 0;
  }

  const char* convertedString = serialno.GetStringUTFChars();
  if (convertedString == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: GetStringUTFChars returned NULL", __FUNCTION__);
    return 0;
  }

  uint32_t length = std::min(aLength-1, static_cast<uint32_t>(strlen(convertedString))); // -1 reserve space for null-terminator
  memcpy(aSerialNumber, convertedString, length);
  aSerialNumber[length] = '\0';
  return length;
}

uint32_t CAndroidPropertiesDeviceInformation::GetMachineUniqueId(char* aBuffer, const uint32_t aBufferSize)
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  // NOTE: No java code involved here, we do not need to attach to the JVM.
  if (iMachineUniqueId[0] == 0)
  {
    char serialno[PROP_VALUE_MAX];

    if (GetSerialNumber(serialno, sizeof(serialno)) > 0 && CMUIDUtils::IsValidMUID(serialno))
    {
      strncpy(iMachineUniqueId, serialno, CMUIDUtils::KMuidLength + CMUIDUtils::KZeroTerminatorLength - 1);
      iMachineUniqueId[CMUIDUtils::KMuidLength + CMUIDUtils::KZeroTerminatorLength - 1] = '\0';
    }
    else
    {
      // If the serial number is at least "KUniquePartOfMuid" characters, we use it to create a valid MUID.
      // Otherwise we get the ANDROID_ID from the host and use that instead.
      if (strlen(serialno) < CMUIDUtils::KUniquePartOfMuid)
      {
        // Use the leftmost "KUniquePartOfMuid" characters of the ANDROID_ID
        GetMuidFromHost(serialno, CMUIDUtils::KUniquePartOfMuid + CMUIDUtils::KZeroTerminatorLength);
      }

      char muid[PROP_VALUE_MAX];
      CMUIDUtils::CreateCustomMUID(muid, serialno);
      strncpy(iMachineUniqueId, muid, CMUIDUtils::KMuidLength + CMUIDUtils::KZeroTerminatorLength - 1);
      iMachineUniqueId[CMUIDUtils::KMuidLength + CMUIDUtils::KZeroTerminatorLength - 1] = '\0';
    }
  }

  // copy either full value or nothing
  CopyPropertyValue(aBuffer, iMachineUniqueId, aBufferSize, __FUNCTION__);

  uint32_t returnLength = strlen(aBuffer);
  if (returnLength == 0)
  {
    returnLength = strlen(iMachineUniqueId);
  }
  return returnLength;
}

uint32_t CAndroidPropertiesDeviceInformation::GetDRMUniqueIds(const uint32_t aIndex, char* aBuffer, const uint32_t aBufferSize)
{
  // NOTE: No java code involved here, we do not need to attach to the JVM.
  // No additional locking needed - GetMachineUniqueId does the locking for us
  return (aIndex == 0) ? GetMachineUniqueId(aBuffer, aBufferSize) : 0;
}

uint32_t CAndroidPropertiesDeviceInformation::GetLiveServicesUniqueId(char* aBuffer, const uint32_t aBufferSize)
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  // NOTE: No java code involved here, we do not need to attach to the JVM.
  if (iLiveServicesUniqueId[0] == 0)
  {
    // If the LSID is empty then we make it equal to the MUID.
    GetMachineUniqueId(iLiveServicesUniqueId, 13);
  }

  // copy either full value or nothing
  CopyPropertyValue(aBuffer, iLiveServicesUniqueId, aBufferSize, __FUNCTION__);

  uint32_t returnLength = strlen(aBuffer);
  if (returnLength == 0)
  {
    returnLength = strlen(iLiveServicesUniqueId);
  }
  return returnLength;
}

uint32_t CAndroidPropertiesDeviceInformation::GetMediaIds(const uint32_t aIndex, uint8_t* aBuffer, const uint32_t aBufferSize)
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  int status;
  JNIEnv *env;
  status = RetrieveVM()->AttachCurrentThread(&env, NULL);
  if (status != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not attach to thread");
    return -1;
  }

  assert(aBufferSize > 0);
  __android_log_print(ANDROID_LOG_DEBUG, TAG, "GetMediaIds(%p): mProxy == %p", this, mProxy);

  env = RetrieveEnv();
  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,
      "%s: Could not retrieve environment (%s)\n",
      __FUNCTION__,
      (const char*) ((env == NULL) ? "environment == NULL" : "mProxy == NULL")
    );
    return 0;
  }

  CJniStringPtr cid(env, static_cast<jstring>(env->CallObjectMethod(mProxy, iGetMediaIdsMethodId, static_cast<jlong>(aIndex))));
  if (CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: Java exception thrown from getMediaIds\n", __FUNCTION__);
    return 0;
  }

  const char* convertedString = cid.GetStringUTFChars();
  if (convertedString == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: GetStringUTFChars returned NULL", __FUNCTION__);
    return 0;
  }

  const uint32_t length = NNavKit::NUtils::ConvertHexStringToBinary(aBuffer, aBufferSize, convertedString);
  return length;
}

const char* CAndroidPropertiesDeviceInformation::GetOTAName()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  // NOTE: No java code involved here, we do not need to attach to the JVM.
  if (iOTAName[0] == 0)
  {
    __system_property_get("ro.product.ota_name", iOTAName);
    if (iOTAName[0] == 0)
    {
      // default fallback to Santorini
      strncpy(iOTAName, SANTORINI_LIVE, PROP_VALUE_MAX - 1);
      iOTAName[PROP_VALUE_MAX - 1] = '\0';
    }
  }
  return iOTAName;
}

const char* CAndroidPropertiesDeviceInformation::GetGPSFirmwareVersion()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  // NOTE: No java code involved here, we do not need to attach to the JVM.
  if (iGPSFirmwareVersion[0] == 0)
  {
    __system_property_get("ro.gps.firmware.version", iGPSFirmwareVersion);
  }
  return iGPSFirmwareVersion;
}

const char* CAndroidPropertiesDeviceInformation::GetGPSType()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  // NOTE: No java code involved here, we do not need to attach to the JVM.
  if (iGPSType[0] == 0)
  {
    __system_property_get("ro.gps.type", iGPSType);
  }
  return iGPSType;
}

bool CAndroidPropertiesDeviceInformation::HasWideScreen()
{
  // No additional locking needed
  return true;
}

const char* CAndroidPropertiesDeviceInformation::GetIMEICode()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  // NOTE: No java code involved here, we do not need to attach to the JVM.
  // Only send the IMEI for non Santorini devices
  if (strcmp(GetOTAName(), SANTORINI_LIVE) != 0)
  {
    if (iIMEICode[0] == 0)
    {
      char buffer[PROP_VALUE_MAX];
      uint32_t imeiLength = GetIMEIFromHost(buffer, PROP_VALUE_MAX);
      if (imeiLength > 0)
      {
        strncpy(iIMEICode, buffer, PROP_VALUE_MAX - 1);
        iIMEICode[PROP_VALUE_MAX - 1] = '\0';
      }
    }
  }
  return iIMEICode;
}

const char* CAndroidPropertiesDeviceInformation::GetICCIDCode()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  // NOTE: No java code involved here, we do not need to attach to the JVM.
  // Only send the ICCID for non Santorini devices
  if (strcmp(GetOTAName(), SANTORINI_LIVE) != 0)
  {
    if (iICCIDCode[0] == 0)
    {
      char buffer[PROP_VALUE_MAX];
      uint32_t iccidLength = GetICCIDFromHost(buffer, PROP_VALUE_MAX);
      if (iccidLength > 0)
      {
        strncpy(iICCIDCode, buffer, PROP_VALUE_MAX);
        iICCIDCode[PROP_VALUE_MAX - 1] = '\0';
      }
    }
  }
  return iICCIDCode;
}

void CAndroidPropertiesDeviceInformation::CopyPropertyValue(
  char* aDestination, const char* aSource, uint32_t aMaxSize, const char* aFunctionName
)
{
  // don't want to return partial results; either full value or nothing
  uint32_t length = strlen(aSource);
  uint32_t size = length + 1; // take into account termination character ('\0')
  if (size <= aMaxSize)
  {
    strncpy(aDestination, aSource, size - 1);
    aDestination[size - 1] = '\0';
  }
  else
  {
    memset(aDestination, 0, aMaxSize);
    fprintf(stderr, "property value is too long to fit in the supplied buffer");
    fflush(stderr);
  }
}

uint32_t CAndroidPropertiesDeviceInformation::GetIMEIFromHost(char* aBuffer, uint32_t aLength) const
{
  int status;
  JNIEnv *env;
  status = RetrieveVM()->AttachCurrentThread(&env, NULL);
  if (status != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not attach to thread");
    return -1;
  }
  __android_log_print(ANDROID_LOG_VERBOSE, TAG, "GetIMEIFromHost: this == %p, mProxy == %p", this, mProxy);

  env = RetrieveEnv();
  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,
      "%s: Could not retrieve environment (%d %d)\n",
      __FUNCTION__,
      static_cast<int>(env == NULL),
      static_cast<int>(mProxy == NULL)
    );
    return 0;
  }

  NJniUtils::CJniStringPtr imeiCode(env, static_cast<jstring>(env->CallObjectMethod(mProxy, iGetIMEICodeMethodId)));
  if (NJniUtils::CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: Java exception thrown from getIMEICode\n", __FUNCTION__);
    return 0;
  }

  const char* convertedString = imeiCode.GetStringUTFChars();
  if (convertedString == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: GetStringUTFChars returned NULL", __FUNCTION__);
    return 0;
  }

  uint32_t length = std::min(aLength, static_cast<uint32_t>(strlen(convertedString) + 1));
  memcpy(aBuffer, convertedString, length);
  aBuffer[length] = '\0';
  return length;
}

uint32_t CAndroidPropertiesDeviceInformation::GetICCIDFromHost(char* aBuffer, uint32_t aLength) const
{
  int status;
  JNIEnv *env;
  status = RetrieveVM()->AttachCurrentThread(&env, NULL);
  if (status != JNI_OK)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not attach to thread");
    return -1;
  }
  __android_log_print(ANDROID_LOG_VERBOSE, TAG, "GetICCIDFromHost: this == %p, mProxy == %p", this, mProxy);

  env = RetrieveEnv();
  if ((env == NULL) || (mProxy == NULL))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,
      "%s: Could not retrieve environment (%d %d)\n",
      __FUNCTION__,
      static_cast<int>(env == NULL),
      static_cast<int>(mProxy == NULL)
    );
    return 0;
  }

  NJniUtils::CJniStringPtr iccidCode(env, static_cast<jstring>(env->CallObjectMethod(mProxy, iGetICCIDCodeMethodId)));
  if (NJniUtils::CJniException::Occured(env))
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: Java exception thrown from getICCIDCode\n", __FUNCTION__);
    return 0;
  }

  const char* convertedString = iccidCode.GetStringUTFChars();
  if (convertedString == NULL)
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: GetStringUTFChars returned NULL", __FUNCTION__);
    return 0;
  }

  uint32_t length = std::min(aLength, static_cast<uint32_t>(strlen(convertedString) + 1));
  memcpy(aBuffer, convertedString, length);
  aBuffer[length] = '\0';
  return length;
}
} // NAdaptation
} // NProcessHost
