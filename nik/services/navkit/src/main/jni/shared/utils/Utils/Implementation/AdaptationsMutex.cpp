//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include "AdaptationsMutex.h"

#include <pthread.h>

namespace NNavKit
{
namespace NUtils
{
class CAdaptationsMutex::CImplementation {
public:
  CImplementation()
  {
    pthread_mutexattr_init(&iMutexAttr);
    pthread_mutexattr_settype(&iMutexAttr, PTHREAD_MUTEX_RECURSIVE);
    pthread_mutex_init(&iMutex, &iMutexAttr);
  }
  ~CImplementation()
  {
    pthread_mutex_destroy(&iMutex);
  }

  int Lock()
  {
    return pthread_mutex_lock(&iMutex);
  }

  int Unlock()
  {
    return pthread_mutex_unlock(&iMutex);
  }

private:
  CImplementation(const CImplementation&); // no construction-copy
  CImplementation& operator=(const CImplementation&); // no assignment

  pthread_mutex_t iMutex;
  pthread_mutexattr_t iMutexAttr;
};


CAdaptationsMutex::CAdaptationsMutex() : iImplementation(new CImplementation())
{
}

CAdaptationsMutex::~CAdaptationsMutex()
{
  delete iImplementation;
}

int CAdaptationsMutex::Lock()
{
  return iImplementation->Lock();
}

int CAdaptationsMutex::Unlock()
{
  return iImplementation->Unlock();
}

} // NUtils
} // NNavKit
