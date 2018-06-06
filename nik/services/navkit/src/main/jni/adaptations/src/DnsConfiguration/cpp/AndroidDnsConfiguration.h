/*!
 * \file
 * \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its
 * subsidiaries and may be used for internal evaluation purposes or commercial
 * use strictly subject to separate licensee agreement between you and TomTom.
 * If you are the licensee, you are only permitted to use this Software in
 * accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and
 * should immediately return it to TomTom N.V.
 */

#ifndef ANDROID_DNSCONFIGURATION_H
#define ANDROID_DNSCONFIGURATION_H

#include "AdaptationsMutex.h"
#include "AndroidJniAdaptation.h"
#include "AndroidJniUtil.h"
#include "IDnsConfiguration.h"

namespace NProcessHost {
namespace NAdaptation {
class CAndroidDnsConfiguration : public NNavKit::NAdaptation::IDnsConfiguration,
                                 public IJniAdaptation,
                                 private CAndroidJNIUtil
{
public:
  explicit CAndroidDnsConfiguration(JavaVM* aVM);
  ~CAndroidDnsConfiguration() override;

  NNavKit::NAdaptation::IDnsConfiguration* OnAcquire() override;
  void                                     OnRelease() override;

  bool AcquireJniResources() override;
  void ReleaseJniResources() override;

  std::vector<std::string> GetDnsAddresses() const override;

private:
  jmethodID                                  iGetDnsAddresses;
  mutable NNavKit::NUtils::CAdaptationsMutex iMutex;
};
} // namespace NAdaptation
} // namespace NProcessHost

#endif // ANDROID_DNSCONFIGURATION_H
