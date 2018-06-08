//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include "StringUtils.h"

#include <string>
#include <cassert>

namespace NNavKit
{
namespace NUtils
{

namespace
{
  uint8_t hexValue(char aCharacter)
  {
   if (aCharacter >= '0' && aCharacter <= '9')
   {
     return aCharacter-'0';
   }
   else if (aCharacter >= 'A' && aCharacter <= 'F')
   {
     return aCharacter-'A'+10;
   }
   else if (aCharacter >= 'a' && aCharacter <= 'f')
   {
     return aCharacter-'a'+10;
   }
   else
   {
     assert(false);
     return 0;
   }
  }
}

uint32_t ConvertHexStringToBinary(uint8_t* aBuffer, const uint32_t aBufferSize, const char* aHexString)
{
  if ((aHexString == NULL) || (aHexString[0] == '\0'))
  {
    return 0;
  }
  if ((aBuffer == NULL) || (aBufferSize == 0))
  {
    return 0;
  }

  const char* hexStringPtr = aHexString;
  do
  {
    bool inRange = (
      ((*hexStringPtr >= '0') && (*hexStringPtr <= '9'))
      || ((*hexStringPtr >= 'A') && (*hexStringPtr <= 'F'))
      || ((*hexStringPtr >= 'a') && (*hexStringPtr <= 'f'))
    );
    if (!inRange)
    {
      return 0;
    }

    hexStringPtr++;
  }
  while (*hexStringPtr != 0);

  const uint32_t hexStringLength =
    static_cast<const uint32_t>(hexStringPtr - aHexString);
  if ((hexStringLength & 0x1) != 0) // odd
  {
    return 0;
  }

  if (aBufferSize < (hexStringLength / 2))
  {
    return hexStringLength / 2;
  }

  hexStringPtr = aHexString;
  uint8_t* bufferPtr = aBuffer;
  do
  {
    uint8_t value = hexValue(*hexStringPtr++) * 16;
    assert(*hexStringPtr != 0);
    value += hexValue(*hexStringPtr++);
    *bufferPtr++ = value;
  }
  while (*hexStringPtr != 0);

  assert((uint32_t)(bufferPtr - aBuffer) == (hexStringLength / 2));
  return static_cast<uint32_t>(bufferPtr - aBuffer);
}

char PathSeparator()
{
  return '/';
}

}
}
