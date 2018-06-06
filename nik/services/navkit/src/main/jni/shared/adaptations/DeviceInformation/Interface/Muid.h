//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef MUID_H
#define MUID_H

#include <ctime>
#include <string>

/* \brief Calculates date (year, week, and week-day) according to ISO 8601 standard.
   \param date          Input calendar date to be converted to ISO 8601 standard.
   \param year[out]     Calculated ISO 8601 compilant year.
   \param week[out]     Calculated ISO 8601 week number (1-53).
   \param weekDay[out]  Calculated ISO 8601 week day (1-7, Monday-Sunday).
 */
void CalculateISO8601Date(const std::tm* aDate, int& aYear, int& aWeek, int& aWeekDay);

/* Generates MUID.
   \param buffer  Buffer within which MUID is to be generated.
   \return  Returns TRUE if MUID was successfully generated.
 */
bool GenerateMUID(std::string& aBuffer);

#endif
