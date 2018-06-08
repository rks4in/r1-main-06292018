//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef KEY_STORE_MAP_SECURITY_H
#define KEY_STORE_MAP_SECURITY_H

#include "IMapSecurity.h"

namespace NNavKit
{
  namespace NAdaptation
  {
    class IProperties;
  }
}

namespace NAdaptations
{
  namespace NMapSecurity
  {

    class CKeyStoreMapSecurity : public NNavKit::NAdaptation::IMapSecurity
    {
    public:
      explicit CKeyStoreMapSecurity(const NNavKit::NAdaptation::IProperties& aProperties)
      : iProperties(&aProperties)
      {
      }
      // Deprecated constructor that effectively disables the GetKeyStorePath/GetKeyStorePassword
      // feature.
      CKeyStoreMapSecurity()
      : iProperties(nullptr)
      {
      }

      virtual NNavKit::NAdaptation::IControlAdaptation* OnAcquire();
      virtual void OnRelease();

      //! @see NNavKit::NAdaptation::IMapSecurity::GetPublicKey.
      virtual int32_t GetPublicKey(SecureKey* aKey) const;
      //! @see NNavKit::NAdaptation::IMapSecurity::GetCipherKey.
      virtual int32_t GetCipherKey(SecureKey* aKey, const uint32_t aIndex) const;
      //! @see NNavKit::NAdaptation::IMapSecurity::GetKeyStorePath.
      virtual int32_t GetKeyStorePath(SecureKey* aPath) const;
      //! @see NNavKit::NAdaptation::IMapSecurity::GetKeyStorePassword.
      virtual int32_t GetKeyStorePassword(SecureKey* aPassword) const;

    private:
      const NNavKit::NAdaptation::IProperties* iProperties;
    };

  }
}

#endif
