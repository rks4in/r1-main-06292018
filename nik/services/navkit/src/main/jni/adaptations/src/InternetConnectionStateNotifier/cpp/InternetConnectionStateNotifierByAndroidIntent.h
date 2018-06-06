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

#ifndef INTERNET_CONNECTION_STATE_NOTIFIER_BY_ANDROID_INTENT_H
#define INTERNET_CONNECTION_STATE_NOTIFIER_BY_ANDROID_INTENT_H

#include "InternetConnectionStateNotifier.h"
#include "AndroidJniUtil.h"
#include "AndroidJniAdaptation.h"

namespace NProcessHost
{
  namespace NAdaptation
  {
    class CInternetConnectionStateNotifierByAndroidIntent
    : public NNavKit::NAdaptation::CInternetConnectionStateNotifier
    , public IJniAdaptation
    , private CAndroidJNIUtil
    {
    public:
      CInternetConnectionStateNotifierByAndroidIntent(JavaVM* aVM);
      virtual ~CInternetConnectionStateNotifierByAndroidIntent();

      virtual bool AcquireJniResources();
      virtual void ReleaseJniResources();

      void SetState(bool aConnected, NNavKit::NAdaptation::CInternetConnectionState::TNetworkType aNetworkType);
    private:
      bool AttachToJavaClass();
      void DetachFromJavaClass();
    };
  }
}

#endif
