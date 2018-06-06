//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef ADAPTATIONS_MUTEX_H
#define ADAPTATIONS_MUTEX_H

namespace NNavKit
{
namespace NUtils
{

  class CAdaptationsMutex
  {
  public:
    //! \brief CMutex ctor.
    CAdaptationsMutex();
    //! \brief CMutex dtor.
    ~CAdaptationsMutex();

    //
    //! \brief Locks the mutex. The call is blocking.
    //!
    //! \returns A C-style success/error code.
    //! \retval 0, if mutex is correctly locked.
    //! \retval any other value, if locking has failed.
    //!
    int Lock();
    //!
    //! \brief Unlocks the previously locked mutex.
    //!
    //! \returns A C-style success/error code.
    //! \retval 0, if mutex is correctly unlocked.
    //! \retval any other value, if unlocking has failed.
    //!
    int Unlock();

  private:
    CAdaptationsMutex(const CAdaptationsMutex&); //no copy-construction
    CAdaptationsMutex& operator=(const CAdaptationsMutex&); // no assignment

    class CImplementation;
    CImplementation* iImplementation;
  };

} // NUtils
} // NNavKit

#endif // ADAPTATIONS_MUTEX_H
