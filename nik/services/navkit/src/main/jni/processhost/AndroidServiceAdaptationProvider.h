//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef ANDROID_SERVICE_ADAPTATION_PROVIDER_H
#define ANDROID_SERVICE_ADAPTATION_PROVIDER_H

#include "BaseAdaptationProvider.h"
#include "AndroidJniUtil.h"

namespace NRDSTMC
{
  class CRDSTMCServiceAndroid;
}

namespace NAdaptations
{
  namespace NMapSecurity
  {
    class CKeyStoreMapSecurity;
  }
}

namespace NSystemAndControl
{
  namespace NAdaptation {
    class CDefaultTrustedCAConfiguration;
    class CDefaultResourceCategoryLayout;
  }
}

namespace NProcessHost
{
  namespace NAdaptation
  {
    class CTimeZoneChangedObserverByAndroidIntent;
    class CInternetConnectionStateNotifierByAndroidIntent;
    class CRemovableMediaStateNotifierByAndroidIntent;
    class CAndroidPropertiesDeviceInformation;
    class CAndroidSecureKeyValueStore;
    class CAndroidProperties;
    class CAndroidProxyConfiguration;
    class CNavCloudConfiguration;
    class CAndroidDnsConfiguration;
    class CAndroidOnlineRoutingConfiguration;
    class CAndroidOnlineSearchConfiguration;

    class CAndroidServiceAdaptationProvider : public NNavKit::NAdaptation::CBaseAdaptationProvider
    {
    public:
      CAndroidServiceAdaptationProvider(JavaVM* aVM);
      virtual ~CAndroidServiceAdaptationProvider();

      virtual void RegisterAdaptations();
      CAndroidPropertiesDeviceInformation* GetAndroidPropertiesDeviceInformation() { return iAndroidPropertiesDeviceInformation; }
      CAndroidProperties* GetAndroidProperties() { return iAndroidProperties; }
    private:
      template <typename taControlAdaptation>
      void CreateAdaptationAndAcquireJniResources(taControlAdaptation*& aAdaptation, const char* aName);

      JavaVM* iVM;
      CTimeZoneChangedObserverByAndroidIntent* iTimeZoneChangedObserverByAndroidIntent;
      CInternetConnectionStateNotifierByAndroidIntent* iInternetConnectionStateNotifierByAndroidIntent;
      CRemovableMediaStateNotifierByAndroidIntent* iRemovableMediaStateNotifierByAndroidIntent;
      CAndroidPropertiesDeviceInformation* iAndroidPropertiesDeviceInformation;
      NRDSTMC::CRDSTMCServiceAndroid* iRDSTMCService;
      CAndroidSecureKeyValueStore* iAndroidSecureKeyValueStore;
      CAndroidProperties* iAndroidProperties;
      NAdaptations::NMapSecurity::CKeyStoreMapSecurity* iMapSecurity;
      CAndroidProxyConfiguration* iAndroidProxyConfiguration;
      CNavCloudConfiguration* iNavCloudConfiguration;
      CAndroidDnsConfiguration* iAndroidDnsConfiguration;
      CAndroidOnlineRoutingConfiguration* iOnlineRoutingConfiguration;
      CAndroidOnlineSearchConfiguration* iOnlineSearchConfiguration;
    };
  }
}

#endif
