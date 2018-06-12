//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include <jni.h>
#include <android/log.h>

#include "AndroidServiceAdaptationProvider.h"
#include "AndroidSecureKeyValueStore.h"
#include "AndroidPropertiesDeviceInformation.h"
#include "InternetConnectionStateNotifierByAndroidIntent.h"
#include "RemovableMediaStateNotifierByAndroidIntent.h"
#include "AndroidProperties.h"
#include "RDSTMCServiceAndroid.h"
#include "TimeZoneChangedObserverByAndroidIntent.h"
#include "KeyStoreMapSecurity.h"
#include "AndroidProxyConfiguration.h"
#include "NavCloudConfiguration.h"
#include "AndroidDnsConfiguration.h"
#include "AndroidOnlineRoutingConfiguration.h"
#include "AndroidOnlineSearchConfiguration.h"

using namespace NProcessHost::NAdaptation;
using namespace NNavKit::NAdaptation;
using namespace NAdaptations::NMapSecurity;

static char const* TAG = "CAndroidServiceAdaptationProvider";

CAndroidServiceAdaptationProvider::CAndroidServiceAdaptationProvider(JavaVM* aVM)
: iVM(aVM)
, iTimeZoneChangedObserverByAndroidIntent(NULL)
, iInternetConnectionStateNotifierByAndroidIntent(NULL)
, iRemovableMediaStateNotifierByAndroidIntent(NULL)
, iAndroidPropertiesDeviceInformation(NULL)
, iRDSTMCService(NULL)
, iAndroidSecureKeyValueStore(NULL)
, iAndroidProperties(NULL)
, iMapSecurity(NULL)
, iAndroidProxyConfiguration(NULL)
, iNavCloudConfiguration(NULL)
, iAndroidDnsConfiguration(NULL)
, iOnlineRoutingConfiguration(NULL)
, iOnlineSearchConfiguration(NULL)
{
}

template <typename taControlAdaptation>
void CAndroidServiceAdaptationProvider::CreateAdaptationAndAcquireJniResources(taControlAdaptation*& aAdaptation, const char* aName)
{
  aAdaptation = new taControlAdaptation(iVM);
  if (aAdaptation->AcquireJniResources())
  {
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "Registered  %s...", aName);
    RegisterControlAdaptation(*aAdaptation);
  }
  else
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to create %s", aName);
    delete aAdaptation;
    aAdaptation = NULL;
  }
}

void CAndroidServiceAdaptationProvider::RegisterAdaptations()
{
  __android_log_print(ANDROID_LOG_DEBUG, TAG,"CAndroidServiceAdaptationProvider::RegisterAdaptations Started....\n");

  iTimeZoneChangedObserverByAndroidIntent =
    new CTimeZoneChangedObserverByAndroidIntent(iVM, "com.tomtom.pnd.systemapp", "SET_TIME_ZONE");
  if (iTimeZoneChangedObserverByAndroidIntent->AcquireJniResources())
  {
    __android_log_print(ANDROID_LOG_DEBUG, TAG,"Registered  iTimeZoneChangedObserverByAndroidIntent....\n");
    RegisterControlAdaptation(*iTimeZoneChangedObserverByAndroidIntent);
  }
  else
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"Failed to create iTimeZoneChangedObserverByAndroidIntent!\n");
    delete iTimeZoneChangedObserverByAndroidIntent;
    iTimeZoneChangedObserverByAndroidIntent = NULL;
  }

  iInternetConnectionStateNotifierByAndroidIntent = new CInternetConnectionStateNotifierByAndroidIntent(iVM);
  if (
    (iInternetConnectionStateNotifierByAndroidIntent != NULL)
    && iInternetConnectionStateNotifierByAndroidIntent->AcquireJniResources()
  )
  {
    __android_log_print(ANDROID_LOG_DEBUG, TAG,"Registered  iInternetConnectionStateNotifierByAndroidIntent....\n");
    RegisterNotifyAdaptation(*iInternetConnectionStateNotifierByAndroidIntent);
  }
  else
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"Failed to create iInternetConnectionStateNotifierByAndroidIntent!\n");
    delete iInternetConnectionStateNotifierByAndroidIntent;
    iInternetConnectionStateNotifierByAndroidIntent = NULL;
  }

  iRemovableMediaStateNotifierByAndroidIntent = new CRemovableMediaStateNotifierByAndroidIntent(iVM);
  if (
    (iRemovableMediaStateNotifierByAndroidIntent != NULL)
    && iRemovableMediaStateNotifierByAndroidIntent->AcquireJniResources()
  )
  {
    __android_log_print(ANDROID_LOG_DEBUG, TAG,"Registered  iRemovableMediaStateNotifierByAndroidIntent....\n");
    RegisterNotifyAdaptation(*iRemovableMediaStateNotifierByAndroidIntent);
  }
  else
  {
    __android_log_print(ANDROID_LOG_ERROR, TAG,"Failed to create iRemovableMediaStateNotifierByAndroidIntent!\n");
    delete iRemovableMediaStateNotifierByAndroidIntent;
    iRemovableMediaStateNotifierByAndroidIntent = NULL;
  }

  CreateAdaptationAndAcquireJniResources(iAndroidPropertiesDeviceInformation, "CAndroidPropertiesDeviceInformation");
  CreateAdaptationAndAcquireJniResources(iRDSTMCService,                      "NRDSTMC::CRDSTMCServiceAndroid");
  if (iRDSTMCService)
  {
    RegisterNotifyAdaptation(iRDSTMCService->GetRDSTMCServiceStateNotifier());
  }

  CreateAdaptationAndAcquireJniResources(iAndroidSecureKeyValueStore, "CAndroidSecureKeyValueStore");
  CreateAdaptationAndAcquireJniResources(iAndroidProperties,          "CAndroidProperties");
  CreateAdaptationAndAcquireJniResources(iAndroidProxyConfiguration,  "CAndroidProxyConfiguration");

  iMapSecurity = new CKeyStoreMapSecurity(*iAndroidProperties);
  RegisterControlAdaptation(*iMapSecurity);

  iNavCloudConfiguration = new CNavCloudConfiguration(*iAndroidProperties);
  RegisterControlAdaptation(*iNavCloudConfiguration);

  iOnlineRoutingConfiguration = new CAndroidOnlineRoutingConfiguration(iVM, *iAndroidProperties);
  if (iOnlineRoutingConfiguration->AcquireJniResources())
  {
    RegisterControlAdaptation(*iOnlineRoutingConfiguration);
  }

  CreateAdaptationAndAcquireJniResources(iAndroidDnsConfiguration,  "CAndroidDnsConfiguration");

  iOnlineSearchConfiguration = new CAndroidOnlineSearchConfiguration(iVM, *iAndroidProperties);
  if (iOnlineSearchConfiguration->AcquireJniResources())
  {
    RegisterControlAdaptation(*iOnlineSearchConfiguration);
  }

  __android_log_print(ANDROID_LOG_DEBUG, TAG,"CAndroidServiceAdaptationProvider::RegisterAdaptations Done!\n");
}

CAndroidServiceAdaptationProvider::~CAndroidServiceAdaptationProvider()
{
  if(iTimeZoneChangedObserverByAndroidIntent != NULL)
  {
    iTimeZoneChangedObserverByAndroidIntent->ReleaseJniResources();
  }
  if(iInternetConnectionStateNotifierByAndroidIntent != NULL)
  {
    iInternetConnectionStateNotifierByAndroidIntent->ReleaseJniResources();
  }
  if(iRemovableMediaStateNotifierByAndroidIntent != NULL)
  {
    iRemovableMediaStateNotifierByAndroidIntent->ReleaseJniResources();
  }
  if(iAndroidPropertiesDeviceInformation != NULL)
  {
    iAndroidPropertiesDeviceInformation->ReleaseJniResources();
  }
  if(iRDSTMCService != NULL)
  {
    iRDSTMCService->ReleaseJniResources();
  }
  if(iAndroidSecureKeyValueStore != NULL)
  {
    iAndroidSecureKeyValueStore->ReleaseJniResources();
  }
  if(iAndroidProperties != NULL)
  {
    iAndroidProperties->ReleaseJniResources();
  }
  if(iAndroidProxyConfiguration != NULL)
  {
    iAndroidProxyConfiguration->ReleaseJniResources();
  }
  if(iAndroidDnsConfiguration != NULL)
  {
    iAndroidDnsConfiguration->ReleaseJniResources();
  }
  if (iOnlineRoutingConfiguration != NULL)
  {
    iOnlineRoutingConfiguration->ReleaseJniResources();
  }
  if (iOnlineSearchConfiguration != NULL)
  {
    iOnlineSearchConfiguration->ReleaseJniResources();
  }

  // Ensure that allocated adaptations are destroyed
  delete iTimeZoneChangedObserverByAndroidIntent;
  delete iInternetConnectionStateNotifierByAndroidIntent;
  delete iRemovableMediaStateNotifierByAndroidIntent;
  delete iAndroidPropertiesDeviceInformation;
  delete iRDSTMCService;
  delete iAndroidSecureKeyValueStore;
  delete iAndroidProperties;
  delete iMapSecurity;
  delete iAndroidProxyConfiguration;
  delete iNavCloudConfiguration;
  delete iAndroidDnsConfiguration;
  delete iOnlineRoutingConfiguration;
  delete iOnlineSearchConfiguration;
}
