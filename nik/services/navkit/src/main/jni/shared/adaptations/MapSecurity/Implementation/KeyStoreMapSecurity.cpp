//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include "KeyStoreMapSecurity.h"

#include "IProperties.h"

#include <boost/filesystem.hpp>

#include <cassert>
#include <cstring>

using namespace NAdaptations::NMapSecurity;

static const boost::filesystem::path KKeyStoreFile = "keystore.sqlite";
static const int KBase64KeyLength = 24;

// Industrial-strength frobnication scheme
static void Defrobnicate(const char* aCipherText, std::size_t aBytes, char* aPlainText)
{
  for(std::size_t i = 0; i < aBytes; i++)
  {
    switch (i % 11) {
#define DEFROBCHAR(__n__, __k__) case (__n__): aPlainText[i] = aCipherText[i] ^ (__k__); break
      DEFROBCHAR(0,  'C'); // So it doesn't appear as a contiguous string in the executable
      DEFROBCHAR(1,  'h');
      DEFROBCHAR(2,  'u');
      DEFROBCHAR(3,  'c');
      DEFROBCHAR(4,  'k');
      DEFROBCHAR(5,  'N');
      DEFROBCHAR(6,  'o');
      DEFROBCHAR(7,  'r');
      DEFROBCHAR(8,  'r');
      DEFROBCHAR(9,  'i');
      DEFROBCHAR(10, 's');
#undef DEFROBCHAR
      default:
        assert(false);
    }
  }
}

/*! Retrieves cipher key.
 *  @param  aBuffer Buffer to store the key.
 *  @param  aIndex  Index (0-based) of the key to retrieve.
 *  @return If aKey is NULL, the returned value represents the size of the buffer necessary
 *          to hold entire key data, including terminating NULL character.
 *          Otherwise, returned value corresponds to number of bytes written to aKey as well.
 *          In case of an error, negative value is returned.
 *  @note aBuffer will be non-empty in case of successful key retrieval.
 */
static int RetrieveCipherKey(NNavKit::NAdaptation::IMapSecurity::SecureKey* aKey, const unsigned int aIndex)
{
  static const char KKeyStore[][KBase64KeyLength] =
  {
    { 0x01, 0x3e, 0x0c, 0x57, 0x26, 0x16, 0x56, 0x40, 0x22, 0x46, 0x1c, 0x0f, 0x3e, 0x03, 0x06, 0x0e, 0x01, 0x56, 0x34, 0x39, 0x18, 0x04, 0x7e, 0x55 },
    { 0x2c, 0x0f, 0x2f, 0x3a, 0x27, 0x36, 0x3f, 0x13, 0x04, 0x31, 0x06, 0x24, 0x27, 0x04, 0x20, 0x3d, 0x37, 0x06, 0x27, 0x27, 0x2d, 0x14, 0x7e, 0x55 },
    { 0x09, 0x5d, 0x34, 0x36, 0x03, 0x37, 0x1a, 0x03, 0x01, 0x0c, 0x23, 0x06, 0x5d, 0x03, 0x1a, 0x2d, 0x1c, 0x5f, 0x25, 0x45, 0x5a, 0x04, 0x7e, 0x55 },
    { 0x0d, 0x05, 0x5e, 0x36, 0x00, 0x08, 0x3b, 0x10, 0x46, 0x42, 0x04, 0x39, 0x5e, 0x1c, 0x1b, 0x1e, 0x0b, 0x0a, 0x35, 0x3d, 0x24, 0x04, 0x7e, 0x55 },
    { 0x73, 0x59, 0x23, 0x39, 0x06, 0x3e, 0x0e, 0x0b, 0x1c, 0x01, 0x3d, 0x17, 0x07, 0x21, 0x0c, 0x32, 0x04, 0x09, 0x03, 0x05, 0x3b, 0x04, 0x7e, 0x55 }
  };

  // check if index in range
  if (aIndex >= (sizeof(KKeyStore) / sizeof(KKeyStore[0])))
  {
    return -1;
  }

  const int KRequiredLength = KBase64KeyLength + 1;

  if (aKey != nullptr)
  {
    Defrobnicate(KKeyStore[aIndex], KBase64KeyLength, aKey->buffer);
    aKey->buffer[KBase64KeyLength] = '\0';
  }

  return KRequiredLength;
}

/*! Retrieves public key.
 *  @param  aBuffer Buffer to store the key.
 *  @note aBuffer will be non-empty in case of successful key retrieval.
 *  @return Whether the key is copied or not, the length of the key is always returned.
 */
static int RetrievePublicKey(NNavKit::NAdaptation::IMapSecurity::SecureKey* aKey)
{
  static const char* key =
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0QNDL1MKuYkMFSALnws8snsUTvmq"
                    "klAAzRlqp3oDK79CyJQ1eNB5E8J3Ss1uXcrKdL08Lcvq4SoqTVDqneuDs0P9USbl7Cbq24bI"
                    "L44G4n3mw53kCLHC1AdnuLXcx24ZwmXz52sqfda+RJMgipLcOl2FNnhlxKQtJXQNuUlOPjck"
                    "XYQ0foc3uepGWDa77xzgtmQo08QXX0fjovITD8qa6hHUbFy8t/BjAYiHFDC5U1hcXZC/vLBr"
                    "IRVYIf9MRmcknQRQ1n+C5nXgCn7GfDtZJ+4Z+3h+jq0yq1Co1nMPIC/Gb/z0ZWubVHjoQptT"
                    "aSVNWdsb/FrPmy64LOhKHhYXlQIDAQAB";

  const int KRequiredLength = static_cast<const int>(std::strlen(key) + 1);

  if (aKey != nullptr)
  {
      std::memcpy(aKey->buffer, key, KRequiredLength);
  }

  return KRequiredLength;
}

NNavKit::NAdaptation::IControlAdaptation* CKeyStoreMapSecurity::OnAcquire()
{
  return this;
}

void CKeyStoreMapSecurity::OnRelease()
{
}

int32_t CKeyStoreMapSecurity::GetPublicKey(SecureKey* aKey) const
{
  return RetrievePublicKey(aKey);
}

int32_t CKeyStoreMapSecurity::GetCipherKey(SecureKey* aKey, const unsigned int aIndex) const
{
  return RetrieveCipherKey(aKey, aIndex);
}

int32_t CKeyStoreMapSecurity::GetKeyStorePath(SecureKey* aPath) const
{
  if (iProperties == nullptr)
  {
    // Fallback - if we don't have access to IProperties, fall back to the old GetCipherKey()
    return 0;
  }

  // Note: we can only access iProperties AFTER it has been 'acquired'.
  // This will always happen before this class is acquired and subsequently used.
  // However, it means that iProperties is not initialized yet at construction time.
  boost::filesystem::path keystorePath = iProperties->GetWorkingDirectory() / KKeyStoreFile;

  if (!boost::filesystem::exists(keystorePath))
  {
    // Fallback - if we don't have a keystore file, fall back to the old GetCipherKey()
    return 0;
  }

  uint32_t requiredBufferSize = static_cast<uint32_t>(keystorePath.size() + 1);
  if (aPath == nullptr)
  {
    return requiredBufferSize;
  }
  if ((aPath->buffer == nullptr) || (aPath->bufferSize < requiredBufferSize))
  {
    return -1;
  }

  std::memcpy(aPath->buffer, keystorePath.c_str(), requiredBufferSize);
  return static_cast<int32_t>(requiredBufferSize);
}

int32_t CKeyStoreMapSecurity::GetKeyStorePassword(SecureKey* aPassword) const
{
  static const char KObfuscatedKey[] = { 0x27, 0x24, 0x4d, 0x2c, 0x0e, 0x60, 0x5a,
                                         0x02, 0x1b, 0x50, 0x17, 0x28, 0x5c, 0x58 };
  static const uint32_t KKeySize = sizeof(KObfuscatedKey);

  if (aPassword == nullptr)
  {
    return KKeySize + 1;
  }

  if ((aPassword->buffer == nullptr) || (aPassword->bufferSize <= KKeySize))
  {
    return -1;
  }

  Defrobnicate(KObfuscatedKey, sizeof(KObfuscatedKey), aPassword->buffer);
  aPassword->buffer[KKeySize] = '\0';
  return KKeySize + 1;
}
