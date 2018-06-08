//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include "MUIDUtils.h"
#include <string>
#include <string.h>
#include <assert.h>
#include <cctype>
#include <functional>
#include <algorithm>

bool CMUIDUtils::IsValidMUID(const char* aMUID)
{
  // ID format is described at http://vos.intra.local/display/TERMS/MUID
  static const char* KIDFormat = "LANNNNLNNNNN";
  enum
  {
    KDayIndex    = 2,   // Day index in format string
    KWeekIndexHi = 3,   // First digit of week index
    KWeekIndexLo = 4,   // Last digit of week
    KYearIndex   = 5,   // Year index in format string
    KRadix       = 10,  // Radix value for week format
    KBase        = '0', // First number
    KMinDay      = 1,   // Monday
    KMaxDay      = 7,   // Sunday
    KMinWeek     = 1,   // First week
    KMaxWeek     = 53,  // Last week
    KMinYear     = 0,   // i.e. year 2010
    KMaxYear     = 9    // i.e. year 2009
  };

  bool isValid = false;
  uint32_t idLength = static_cast<uint32_t>(strlen(KIDFormat));
  if (strlen(aMUID) == idLength)
  {
    if (IsValidIDFormat(aMUID, KIDFormat))
    {
      int day  = (aMUID[KDayIndex] - KBase);
      int week = ((aMUID[KWeekIndexHi] - KBase) * KRadix) + (aMUID[KWeekIndexLo] - KBase);
      int year = (aMUID[KYearIndex] - KBase);
      isValid =
        ((KMinDay  <= day)  && (day  <= KMaxDay))  &&
        ((KMinWeek <= week) && (week <= KMaxWeek)) &&
        ((KMinYear <= year) && (year <= KMaxYear));
    }
  }
  return isValid;
}

void CMUIDUtils::CreateCustomMUID(char* aMUID, const char* aSerialNumber)
{
  assert(aSerialNumber != NULL);
  assert(aMUID != NULL);
  std::string serial(aSerialNumber);
  std::string muid("NA");
  if (serial.size() < KUniquePartOfMuid)
  {
    // Invalid serial number -- but we need something, so let's put some default
    // value here for now. Note that there's no law saying MUIDs have to
    // be limited to hexadecimal.
    muid += std::string("TTNOMUID11");
  }
  else
  {
    // Use the rightmost "KUniquePartOfMuid" characters of the serial number
    muid += serial.substr(serial.size() - KUniquePartOfMuid);
  }

  std::transform(muid.begin(), muid.end(), aMUID, std::ptr_fun<int, int>(std::toupper));
  aMUID[muid.length()] = '\0'; // More explicit than calling std::transform with [muid.begin(), muid.end() + 1]
}

bool CMUIDUtils::IsValidIDFormat(const char* aID, const char* aFormat)
{
  uint32_t idLength = static_cast<uint32_t>(strlen(aID));
  assert(strlen(aFormat) == idLength);
  bool isValid = true;
  uint32_t index = 0;

  while (isValid && (index < idLength))
  {
    switch (aFormat[index])
    {
      case 'A':
        isValid = std::isalnum(aID[index]);
        break;

      case 'N':
        isValid = std::isdigit(aID[index]);
        break;

      case 'L':
        isValid = std::isupper(aID[index]);
        break;

      default:
        assert(false);
        break;
    }
    index++;
  }

  return isValid;
}
