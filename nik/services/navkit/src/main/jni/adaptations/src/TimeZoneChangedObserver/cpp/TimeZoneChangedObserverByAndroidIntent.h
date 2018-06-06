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

#ifndef TIMEZONE_CHANGED_OBSERVER_ANDROID_BY_ANDROID_INTENT_H
#define TIMEZONE_CHANGED_OBSERVER_ANDROID_BY_ANDROID_INTENT_H

#include "ITimeZoneChangedObserver.h"
#include "AndroidJniUtil.h"
#include "AndroidJniAdaptation.h"
#include "AdaptationsMutex.h"

#include <string>

namespace NProcessHost
{
  namespace NAdaptation
  {
    class CTimeZoneChangedObserverByAndroidIntent
    : public NNavKit::NAdaptation::ITimeZoneChangedObserver
    , public IJniAdaptation
    , private CAndroidJNIUtil
    {
    public:
      CTimeZoneChangedObserverByAndroidIntent(
        JavaVM* aVM,
        const char* aReceiver,
        const char* aIntentName
      );
      virtual ~CTimeZoneChangedObserverByAndroidIntent();

      virtual bool AcquireJniResources();
      virtual void ReleaseJniResources();

      virtual NNavKit::NAdaptation::IControlAdaptation* OnAcquire();
      virtual void OnRelease();

      virtual void NotifyTimeZoneChanged(const char* aTimeZone);
    private:
      jmethodID iTimeZoneMethodID;
      std::string iReceiver;
      std::string iIntentName;

      // Guards OnAcquire/OnRelease and all pure virtuals from  ITimeZoneChangedObserver
      NNavKit::NUtils::CAdaptationsMutex iMutex;
    };
  }
}

#endif // TIMEZONE_CHANGED_OBSERVER_ANDROID_BY_ANDROID_INTENT_H
