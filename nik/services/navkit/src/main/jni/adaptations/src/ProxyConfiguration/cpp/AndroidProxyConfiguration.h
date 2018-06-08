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

#ifndef ANDROIDCONFIGURATION_H
#define ANDROIDCONFIGURATION_H

#include "IProxyConfiguration.h"
#include "AndroidJniUtil.h"
#include "AndroidJniAdaptation.h"
#include "AdaptationsMutex.h"

namespace NProcessHost
{
  namespace NAdaptation
  {
    class CAndroidProxyConfiguration: public NNavKit::NAdaptation::IProxyConfiguration,
                                      public IJniAdaptation, private CAndroidJNIUtil
    {
    public:
      explicit CAndroidProxyConfiguration(JavaVM *aVM);
      ~CAndroidProxyConfiguration();

      virtual NNavKit::NAdaptation::IProxyConfiguration* OnAcquire();
      virtual void OnRelease();

      virtual bool AcquireJniResources();
      virtual void ReleaseJniResources();

      virtual void GetProxyConfigurationForUrl(const char* aUrl, char* aProxyUrl);

    private:
      jmethodID iGetProxyConfigurationForUrl;
      NNavKit::NUtils::CAdaptationsMutex iMutex;
    };
  }
}

#endif
