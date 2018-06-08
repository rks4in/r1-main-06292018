//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef ADAPTATIONS_MUTEX_LOCKER_H
#define ADAPTATIONS_MUTEX_LOCKER_H

namespace NNavKit
{
namespace NUtils
{

  class CAdaptationsMutex;

  //! \brief Implements the mutex RAII locker idiom. Locks the mutex in contructor, unlocks in destructor.
  class CAdaptationsMutexLocker
  {
  public:
    //! \brief Ctor.
    //! \param[in] aMutex A mutex to lock.
    explicit CAdaptationsMutexLocker(NNavKit::NUtils::CAdaptationsMutex& aMutex);
    //! \brief Dtor.
    ~CAdaptationsMutexLocker();
  private:
    CAdaptationsMutexLocker(const CAdaptationsMutexLocker&); // no construction-copy
    CAdaptationsMutexLocker& operator=(const CAdaptationsMutexLocker&); // no assignment

    CAdaptationsMutex& iMutex;
  };


} // NUtils
} // NNavKit

#endif // ADAPTATIONS_MUTEX_LOCKER_H
