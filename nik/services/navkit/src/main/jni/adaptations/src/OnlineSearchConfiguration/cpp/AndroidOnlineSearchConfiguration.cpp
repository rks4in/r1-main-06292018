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

#include "AndroidOnlineSearchConfiguration.h"
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

static const auto TAG = "CAndroidOnlineSearchConfiguration";
static const auto CLASS_NAME = "com/tomtom/navkit/adaptations/AndroidOnlineSearchConfiguration";

static const auto DEFAULT_TIMEOUT = 5000;
static const char* const KNavKitCertificate = "nkw.crt";
static const auto SEARCH_URL = "https://api.tomtom.com/search/1/search/xml?key=";
static const auto POI_CATEGORY_URL = "https://api.tomtom.com/search/1/poiCategories/xml?key=";

CAndroidOnlineSearchConfiguration::CAndroidOnlineSearchConfiguration(JavaVM* aVM, NNavKit::NAdaptation::IProperties& aPropertiesAdaptation) :
    CAndroidJNIUtil(aVM),
    iPropertiesAdaptation(aPropertiesAdaptation)
{
}

CAndroidOnlineSearchConfiguration::~CAndroidOnlineSearchConfiguration()
{
}

bool CAndroidOnlineSearchConfiguration::AcquireJniResources()
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

void CAndroidOnlineSearchConfiguration::ReleaseJniResources()
{
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "ReleaseJniResources (%p)", this);
    ReleaseProxy();
};

NNavKit::NAdaptation::IControlAdaptation* CAndroidOnlineSearchConfiguration::OnAcquire()
{
    return this;
}

void CAndroidOnlineSearchConfiguration::OnRelease()
{
}

const char* CAndroidOnlineSearchConfiguration::GetCaCertificatePath() const
{
    const std::string workingDir = iPropertiesAdaptation.GetWorkingDirectory();
    const std::string certificatePath = workingDir
                                        + PathSeparator()
                                        + std::string(KNavKitCertificate);

    __android_log_print(ANDROID_LOG_ERROR, TAG, "NavKit Certificate Path is =  %s\n",
                        certificatePath.c_str());
    return certificatePath.c_str();
}

uint32_t CAndroidOnlineSearchConfiguration::GetSearchRequestTimeoutInMs() const
{
    return DEFAULT_TIMEOUT;
}

const char* CAndroidOnlineSearchConfiguration::GetSearchRequestUrl() const
{
    return (SEARCH_URL + iApiKey).c_str();
}

const char* CAndroidOnlineSearchConfiguration::GetPoiCategoriesListRequestUrl() const
{
    return (POI_CATEGORY_URL + iApiKey).c_str();
}

}
}