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

#ifndef ANDROID_SECURE_KEY_VALUE_STORE_H_
#define ANDROID_SECURE_KEY_VALUE_STORE_H_

#include "IConfidentialValueStore.h"
#include "AndroidJniUtil.h"
#include "AndroidJniAdaptation.h"
#include "AdaptationsMutex.h"

namespace NProcessHost
{
  namespace NAdaptation
  {

    class CAndroidSecureKeyValueStore
        : public NNavKit::NAdaptation::IConfidentialValueStore
        , public IJniAdaptation
        , private CAndroidJNIUtil
    {
    public:
      CAndroidSecureKeyValueStore(JavaVM *aVM);
      virtual ~CAndroidSecureKeyValueStore();

      // IJniAdaptation
      virtual bool AcquireJniResources();
      virtual void ReleaseJniResources();

      // IConfidentationValueStore
      virtual NNavKit::NAdaptation::IControlAdaptation* OnAcquire();
      virtual void OnRelease();

      virtual bool StoreValue(const char* aKey, const void* aValue, const int aValueSize);
      virtual int RetrieveValue(const char* aKey, void* aValue, const int aValueSize);

    private:
      jmethodID iStoreValueMethodId;
      jmethodID iRetrieveValueMethodId;

      // Guards OnAcquire/OnRelease and all pure virtuals from  IConfidentialValueStore
      NNavKit::NUtils::CAdaptationsMutex iMutex;
    };

  }
}


#endif
