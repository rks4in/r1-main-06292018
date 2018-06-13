/*
 * Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

#include "AndroidOnlineRoutingConfiguration.h"
#include "AndroidJniResourceManagement.h"
#include "IProperties.h"
#include "StringUtils.h"

#include <android/log.h>
#include <boost/filesystem.hpp>
#include <cstring>
#include <assert.h>

namespace NProcessHost
{
  namespace NAdaptation
  {
    using namespace NJniUtils;
    using NNavKit::NAdaptation::IProperties;
    using NNavKit::NUtils::PathSeparator;

    static char const* TAG = "CAndroidOnlineRoutingConfiguration";
    static char const* CLASS_NAME = "com/tomtom/navkit/adaptations/AndroidOnlineRoutingConfiguration";

    static const uint32_t DEFAULT_TIMEOUT = 0;
    static const char* const KNavKitCertificate = "nkw.crt";
    static const std::string ROUTING_URL = "https://api.tomtom.com/routing/1/calculateRoute/<LOCATIONS>/json?key=";

    CAndroidOnlineRoutingConfiguration::CAndroidOnlineRoutingConfiguration(JavaVM* aVM, NNavKit::NAdaptation::IProperties& aPropertiesAdaptation) :
        CAndroidJNIUtil(aVM),
        iPropertiesAdaptation(aPropertiesAdaptation)
    {
    }

    CAndroidOnlineRoutingConfiguration::~CAndroidOnlineRoutingConfiguration()
    {
    }

    bool CAndroidOnlineRoutingConfiguration::AcquireJniResources()
    {
      if (mProxy != nullptr)
      {
        return false;
      }

      if (!CreateProxyWithContext(CLASS_NAME))
      {
        return false;
      }

      auto *env = RetrieveEnv();
      if (env == nullptr)
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

      auto getApiKeyMethodName = "getApiKey";
      auto getApiKeyMethodId = env->GetMethodID(proxyClass, getApiKeyMethodName, "()Ljava/lang/String;");
      if (CJniException::Occured(env) || (getApiKeyMethodId == NULL))
      {
        __android_log_print(ANDROID_LOG_ERROR, TAG,"%s: couldn't find the method %s\n", __FUNCTION__, getApiKeyMethodName);
        return false;
      }

      CJniStringPtr apiKey(env, static_cast<jstring>(env->CallObjectMethod(mProxy, getApiKeyMethodId)));
      if (CJniException::Occured(env))
      {
        return false;
      }
      iApiKey = std::string(apiKey.GetStringUTFChars());
    }

    void CAndroidOnlineRoutingConfiguration::ReleaseJniResources()
    {
      ReleaseProxy();
    }

    NNavKit::NAdaptation::IControlAdaptation* CAndroidOnlineRoutingConfiguration::OnAcquire()
    {
      return this;
    }

    void CAndroidOnlineRoutingConfiguration::OnRelease()
    {
    }

    const char* CAndroidOnlineRoutingConfiguration::GetCaCertificatePath() const
    {
      const std::string workingDir = iPropertiesAdaptation.GetWorkingDirectory();
      const std::string certificatePath = workingDir
                                        + PathSeparator()
                                        + std::string(KNavKitCertificate);

      __android_log_print(ANDROID_LOG_DEBUG, TAG, "NavKit Certificate Path is =  %s\n", certificatePath.c_str());
      return certificatePath.c_str();
    }

    uint32_t CAndroidOnlineRoutingConfiguration::GetRoutingRequestTimeoutInMs() const
    {
      return DEFAULT_TIMEOUT;
    }

    const char* CAndroidOnlineRoutingConfiguration::GetRoutingRequestUrl() const
    {
      __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s: Routing request URL =  %s\n",__FUNCTION__, (ROUTING_URL + iApiKey).c_str());
      return (ROUTING_URL + iApiKey).c_str();
    }
  }
}
