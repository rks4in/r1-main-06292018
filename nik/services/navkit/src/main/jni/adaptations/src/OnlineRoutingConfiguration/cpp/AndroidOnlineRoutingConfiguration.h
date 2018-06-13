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

#ifndef NAVKITINTEGRATIONKIT_ANDROIDONLINEROUTINGCONFIGURATION_H
#define NAVKITINTEGRATIONKIT_ANDROIDONLINEROUTINGCONFIGURATION_H

#include "IOnlineRoutingConfiguration.h"
#include "IProperties.h"
#include "AndroidJniUtil.h"
#include "AndroidJniAdaptation.h"

#include <string>

namespace NProcessHost
{
  namespace NAdaptation
  {
    using NNavKit::NAdaptation::IProperties;

    class CAndroidOnlineRoutingConfiguration:
    public NNavKit::NAdaptation::IOnlineRoutingConfiguration,
    public NProcessHost::NAdaptation::IJniAdaptation,
    private CAndroidJNIUtil
    {
      public:
        CAndroidOnlineRoutingConfiguration(JavaVM* aVM, IProperties& aPropertiesAdaptation);
        virtual ~CAndroidOnlineRoutingConfiguration();

        bool AcquireJniResources() override;
        void ReleaseJniResources() override;
        NNavKit::NAdaptation::IControlAdaptation* OnAcquire() override;
        void OnRelease() override;

        const char* GetRoutingRequestUrl() const override;
        uint32_t GetRoutingRequestTimeoutInMs() const override;
        const char* GetCaCertificatePath() const override;

      private:
        std::string iApiKey;
        IProperties& iPropertiesAdaptation;
    };
  }
}
#endif //NAVKITINTEGRATIONKIT_ANDROIDONLINEROUTINGCONFIGURATION_H
