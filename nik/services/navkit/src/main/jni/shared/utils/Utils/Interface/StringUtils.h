//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include <stdint.h>

namespace NNavKit
{
namespace NUtils
{
  //! \brief      Convert a string with hexadecimal characters to binary data, i.e. "A3b409" to <163><180><009>
  //!
  //! In case aHexString contains characters that are not in range '0'..'9', 'A'..'F' or 'a'..'f', 0 is returned
  //! In case aHexString does not contain an even amount of characters, 0 is returned
  //! In case the buffer is too small, the return value will indicate the length of the binary-data when it would
  //!   be written (aBufferSize should be this length); this will be treated as an error so aBuffer will be untouched
  //!
  //! In case of an error aBuffer will be untouched
  //!
  //! \param[out] aBuffer that will contain binary data
  //! \param[in]  aBufferSize size of output buffer
  //! \param[in]  aHexString zero-terminated string that contains hexadecimal numbers that have to be
  //!             converted to binary data;
  //! \retval     The length of the binary-data if successful, otherwise 0.
  uint32_t ConvertHexStringToBinary(uint8_t* aBuffer, const uint32_t aBufferSize, const char* aHexString);

  //! \brief      Returns the path separator used on the compiled platform.
  //! \retval     A backslash on Windows systems, a slash otherwise.
  char PathSeparator();
}
}
