//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include "Muid.h"
#include <sstream>
#include <iomanip>
#include <stdlib.h>

#define TM_YEAR_BASE   (1900)
#define DAYSPERLYEAR   (366)
#define DAYSPERNYEAR   (365)
#define DAYSPERWEEK    (7)

/* \brief Returns TRUE if given year is a leap year.
 */
bool IsLeapYear(int aYear)
{
  if ((aYear % 400) == 0)
  {
    return true;
  }
  else if ((aYear % 100) == 0)
  {
    return false;
  }
  else if ((aYear % 4) == 0)
  {
    return true;
  }

  return false;
}

/* \brief Calculates date (year, week, and week-day) according to ISO 8601 standard.
   \param date          Input calendar date to be converted to ISO 8601 standard.
   \param year[out]     Calculated ISO 8601 compilant year.
   \param week[out]     Calculated ISO 8601 week number (1-53).
   \param weekDay[out]  Calculated ISO 8601 week day (1-7, Monday-Sunday).
 */
void CalculateISO8601Date(const std::tm* aDate, int& aYear, int& aWeek, int& aWeekDay)
{
  int yearDay = aDate->tm_yday;                   // days since Jan 1st (0-365)
  aYear       = aDate->tm_year + TM_YEAR_BASE;    // year (ie. 2012)
  aWeekDay    = aDate->tm_wday;                   // week-day index since Sunday (0-6)

  // NOTE: Any given date when converted into ISO 8601 standard will satisfy one and only one of the following criteria:
  //       - it may be a part of the last week of the previous year
  //       - it may be a part of the first week of the next year
  //       - it may be a part of the week belonging to current year

  // the worst case scenario: 2 loops
  while (true)
  {
    // determine number of days in the year
    int daysInYear = IsLeapYear(aYear) ? DAYSPERLYEAR : DAYSPERNYEAR;

    // calculate first valid day of a current year
    // (first three days of the week are negative to indicate they do not count towards first week of a year, according to ISO 8601)
    int firstDay = ((yearDay + 11 - aWeekDay) % DAYSPERWEEK) - 3;

    // calculate last valid day in the current year
    int lastDay = firstDay - (daysInYear % DAYSPERWEEK);

    if (lastDay < -3)
    {
      lastDay += DAYSPERWEEK;
    }

    lastDay += daysInYear;

    // check if the day belongs to next year...
    if (yearDay >= lastDay)
    {
      // yeap, the date belongs to first week of the following year
      ++aYear;
      aWeek = 1;

      // we are done
      break;
    }

    // check if day belongs to current year...
    if (yearDay >= firstDay)
    {
      // yeap, calculate proper week index (1-based)
      aWeek = 1 + ((yearDay - firstDay) / DAYSPERWEEK);

      // we are done
      break;
    }

    // date belongs to previous year. Update data and give it another (final try)
    --aYear;
    yearDay += IsLeapYear(aYear) ? DAYSPERLYEAR : DAYSPERNYEAR;
  }

  // convert day of the week to ISO 8601 compilant value
  if (aWeekDay == 0)
  {
    aWeekDay = 7;
  }
}

// MUID is of the following form: NAxxxxvrrrrr
// where:
//        - xxxx      - ISO 8601 week-day date format (DWWY)
//        - v         - project variation. Currently 0.
//        - r         - random decimal digit

/*! Generates MUID.
    \param buffer  Buffer within which MUID is to be generated.
    \return  Returns TRUE if MUID was successfully generated.
 */
bool GenerateMUID(std::string& aBuffer)
{
  srand(std::time(NULL));

  // get current date...
  std::time_t t = std::time(NULL);
  std::tm* loctime = std::localtime(&t);

  // ...and convert it into ISO 8601
  int year = 0;
  int week = 0;
  int weekDay = 0;
  CalculateISO8601Date(loctime, year, week, weekDay);

  std::ostringstream strStream;
  strStream << "NA"
            << weekDay
            << std::setfill('0') << std::setw(2) << week
            << std::setw(1) << (year % 10)
            << "0"
            << std::setfill('0') << std::setw(5) << (rand() % 100000);

  aBuffer = strStream.str();

  return true;
}
