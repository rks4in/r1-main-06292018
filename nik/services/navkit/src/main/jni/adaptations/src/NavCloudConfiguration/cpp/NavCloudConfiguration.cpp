//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include <iostream>
#include <fstream>
#include <string>
#include "StringUtils.h"

#include "NavCloudConfiguration.h"
#include "IProperties.h"

static const char* const KNavCloudConfigurationFile = "NavCloudConfiguration.txt";

namespace NProcessHost
{
  namespace NAdaptation
  {
    using NNavKit::NAdaptation::IProperties;
    using NNavKit::NUtils::PathSeparator;

    CNavCloudConfiguration::CNavCloudConfiguration(NNavKit::NAdaptation::IProperties& aPropertiesAdaptation)
    : iPropertiesAdaptation(aPropertiesAdaptation)
    {
    }

    IControlAdaptation* CNavCloudConfiguration::OnAcquire()
    {
      LoadConfig();
      return this;
    }

    bool CNavCloudConfiguration::LoadConfig()
    {
      const std::string workingDir = iPropertiesAdaptation.GetWorkingDirectory();
      const std::string navCloudConfigurationPath = workingDir
                                                    + PathSeparator()
                                                    + std::string(KNavCloudConfigurationFile);

      std::ifstream configuration (navCloudConfigurationPath.c_str(), std::ifstream::in);

      if (configuration.is_open())
      {
        std::string configLine;
        while (std::getline(configuration, configLine))
        {
          if (configLine.size() > 1 && configLine[0] != '#' &&
              configLine.find("=") != std::string::npos)
          {
            const std::string name = configLine.substr(0, configLine.find("="));
            const std::string value = configLine.substr(configLine.find("=") + 1);

            if (name.find("ApiServerUrl") != std::string::npos)
            {
              iApiServerUrl = value;
            }
            else if (name.find("StreamingServerUrl") != std::string::npos)
            {
              iStreamingServerUrl = value;
            }
            else if (name.find("CaCertificatePath") != std::string::npos)
            {
              iCaCertificatePath = value;
            }
            else if (name.find("ApplicationId") != std::string::npos)
            {
              iApplicationId = value;
            }
            else
            {
              continue;
            }
          }
        }
      }
      return true;
    }

    const char* CNavCloudConfiguration::GetApiServerUrl() const
    {
      return iApiServerUrl.c_str();
    }

    const char* CNavCloudConfiguration::GetStreamingServerUrl() const
    {
      return iStreamingServerUrl.c_str();
    }

    const char* CNavCloudConfiguration::GetCaCertificatePath() const
    {
      return iCaCertificatePath.c_str();
    }

    const char* CNavCloudConfiguration::GetApplicationId() const
    {
      return iApplicationId.c_str();
    }

  }
}
