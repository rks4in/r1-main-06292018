//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef FRAMEWORK_DRM_MUID_UTILS_H
#define FRAMEWORK_DRM_MUID_UTILS_H

#include <stdint.h>

class CMUIDUtils
{
public:
  //! Checks whether a MUID matches rules described at http://vos.intra.local/display/TERMS/MUID.
  //! Requires:
  //!   null-terminated string "aMUID"
  //! Returns:
  //!   "true" if the MUID matches the rules, "false" otherwise
  static bool IsValidMUID(const char* aMUID);

  //! Creates custom MUID which does not match rules described at http://vos.intra.local/display/TERMS/MUID
  //! but matches server-side requirements specified at http://vos/display/NAVKIT/Device+identification+and+authentication+within+NavKit
  //! ODS: verifies only the prefix (first two characters) of the MUID.
  //! Requires:
  //!   buffer pointed to by "aMUID" with minimum size of (KMuidLength + KZeroTerminatorLength) (see .cpp file for constant values)
  //!   null-terminated serial number in "aSerialNumber"
  //! Returns:
  //!   null-terminated MUID value in "aMUID" buffer
  static void CreateCustomMUID(char* aMUID, const char* aSerialNumber);

  static const uint32_t KZeroTerminatorLength = 1;
  static const uint32_t KStaticPartOfMuid     = 2;
  static const uint32_t KUniquePartOfMuid     = 10; // MUIDs on 3rd-party hardware are 'NA' plus 10 chars
  static const uint32_t KMuidLength           = KStaticPartOfMuid + KUniquePartOfMuid;

protected:
  static bool IsValidIDFormat(const char* aID, const char* aFormat);
};

#endif // FRAMEWORK_DRM_MUID_UTILS_H
