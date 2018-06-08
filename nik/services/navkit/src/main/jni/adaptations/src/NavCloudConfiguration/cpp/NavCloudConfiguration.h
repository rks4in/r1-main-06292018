//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef DEFAULT_NAVCLOUD_CONFIGURATION_H_
#define DEFAULT_NAVCLOUD_CONFIGURATION_H_

#include <string>

#include "INavCloudConfiguration.h"
#include "IProperties.h"


namespace NProcessHost
{
  namespace NAdaptation
  {
    using NNavKit::NAdaptation::IControlAdaptation;
    using NNavKit::NAdaptation::INavCloudConfiguration;
    using NNavKit::NAdaptation::IProperties;

    /*!
    * This implementation of INavCloudConfiguration obtains the NavCloud
    * configuration parameters from "NavCloudConfiguration.txt" file.
    *
    * Adaptation expects the following format of the configuration file:
    *
    * ApiServerUrl=http://api.navcloud.tomtom.com:8080/
    * StreamingServerUrl=https://streaming.navcloud.tomtom.com:8080/
    */
    class CNavCloudConfiguration : public INavCloudConfiguration
    {
    public:
      CNavCloudConfiguration(IProperties& aPropertiesAdaptation);
      virtual ~CNavCloudConfiguration() {}
      virtual IControlAdaptation* OnAcquire();
      virtual void OnRelease() {}

      virtual const char* GetApiServerUrl() const;
      virtual const char* GetStreamingServerUrl() const;
      virtual const char* GetCaCertificatePath() const;
      virtual const char* GetApplicationId() const;

    private:
      CNavCloudConfiguration(const CNavCloudConfiguration&);
      CNavCloudConfiguration& operator=(const CNavCloudConfiguration&);

      bool LoadConfig();
      std::string iApiServerUrl;
      std::string iStreamingServerUrl;
      std::string iCaCertificatePath;
      std::string iApplicationId;
      IProperties& iPropertiesAdaptation;
    };
  }
}

#endif /* DEFAULT_NAVCLOUD_CONFIGURATION_H_ */
