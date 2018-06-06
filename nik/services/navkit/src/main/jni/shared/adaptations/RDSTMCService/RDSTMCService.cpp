//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include "RDSTMCService.h"
#include "AdaptationsMutexLocker.h"

#include <assert.h>
#include <stdio.h>
#include <algorithm>

using namespace NRDSTMC;

//
// CRDSTMCService::CRDSTMCServiceStateNotifierImplementation
//

void CRDSTMCService::CRDSTMCServiceStateNotifierImplementation::NotifyStatus(TRDSTMCServiceStatus aStatus)
{
  switch (aStatus)
  {
    case ERDSTMCServiceAvailable:
      GetState().Available();
      break;
    case ERDSTMCServiceUnavailable:
      GetState().Unavailable();
      break;
    default:
      assert(false);
      break;
  }
  NotifyObserver();
}

//
// CRDSTMCService
//

CRDSTMCService::CRDSTMCService()
: iRDSTMCServiceStateNotifier()
, iStatus(ERDSTMCServiceUnavailable)
, iAttachedReceivers(0)
, iStartRequests(0)
, iAcquiredCounter(0)
{
  printf("CRDSTMCService::CRDSTMCService\n");
}

CRDSTMCService::~CRDSTMCService()
{
  printf("CRDSTMCService::~CRDSTMCService\n");
}

NNavKit::NAdaptation::IControlAdaptation* CRDSTMCService::OnAcquire()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  printf("CRDSTMCService::OnAcquire: iAcquiredCounter %d\n", iAcquiredCounter);
  NNavKit::NAdaptation::IControlAdaptation* result = this;
  if (iAcquiredCounter == 0)
  {
    result = DoOnAcquire();
  }
  if (result != NULL)
  {
    iAcquiredCounter++;
  }
  return result;
}

void CRDSTMCService::OnRelease()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  printf("CRDSTMCService::OnRelease: iAcquiredCounter %d\n", iAcquiredCounter);
  assert(iAcquiredCounter > 0);
  if (iAcquiredCounter > 0)
  {
    if (iAcquiredCounter == 1)
    {
      DoOnRelease();
    }
    iAcquiredCounter--;
  }
  else
  {
    printf("CRDSTMCService::OnRelease: ERROR: OnRelease called without OnAcquire\n");
  }
}

bool CRDSTMCService::TryStartService()
{
  printf("CRDSTMCService::TryStartService: status %d, #attachedReceivers %d, #startRequests %d\n",
             iStatus,
             iAttachedReceivers,
             iStartRequests);

  if (iStatus == ERDSTMCServiceAvailable)
  {
    SetStatus(ERDSTMCServiceAvailable);
    printf("CRDSTMCService::TryStartService: WARNING: status %d, return true\n", iStatus);
    return true;
  }

  if (iAttachedReceivers <= 0)
  {
    printf("CRDSTMCService::TryStartService: #attachedReceivers %d, return false\n", iAttachedReceivers);
    return false;
  }

  if (iStartRequests != 1)
  {
    printf("CRDSTMCService::TryStartService: #startRequests %d, return false\n", iStartRequests);
    return false;
  }

  if (StartRemoteService())
  {
    SetStatus(ERDSTMCServiceAvailable);
    printf("CRDSTMCService::TryStartService: StartRemoteService(), return true\n");
    return true;
  }

  printf("CRDSTMCService::TryStartService: ERROR: StartRemoteService(), return false\n");
  return false;
}

void CRDSTMCService::TryStopService()
{
  printf("CRDSTMCService::TryStopService: status %d, #attachedReceivers %d, #startRequests %d\n",
             iStatus,
             iAttachedReceivers,
             iStartRequests);

  if (iStatus == ERDSTMCServiceAvailable)
  {
    SetStatus(ERDSTMCServiceUnavailable);
    StopRemoteService();
  }
}

bool CRDSTMCService::StartService()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  printf("CRDSTMCService::StartService: status %d, #attachedReceivers %d, #startRequests %d -> %d\n",
             iStatus,
             iAttachedReceivers,
             iStartRequests,
             iStartRequests + 1);

  iStartRequests++;

  // call TryStartService independent from number of start requests
  if (TryStartService())
  {
    printf("CRDSTMCService::StartService: return true\n");
    return true;
  }

  printf("CRDSTMCService::StartService: return false\n");
  return false;
}

void CRDSTMCService::StopService()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  printf("CRDSTMCService::StopService: status %d, #startRequests %d -> %d\n", iStatus, iStartRequests, iStartRequests-1);

  assert(iStartRequests > 0);

  if (iStartRequests > 0)
  {
    // check if it's the last observer
    if (iStartRequests == 1)
    {
      TryStopService();
    }

    iStartRequests--;
  }
  else
  {
    printf("CRDSTMCService::StopService: WARNING: StopService called without StartService\n");
  }

}

void CRDSTMCService::SetStatus(TRDSTMCServiceStatus aStatus)
{
  printf("CRDSTMCService::SetStatus: status %d -> %d, #startRequests %d\n", iStatus, aStatus, iStartRequests);

  iStatus = aStatus;
  iRDSTMCServiceStateNotifier.NotifyStatus(iStatus);
}

void CRDSTMCService::OnReceiverConnected()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  printf("CRDSTMCService::OnReceiverConnected: #attachedReceivers %d -> %d, status %d\n",
             iAttachedReceivers,
             iAttachedReceivers + 1,
	     iStatus);

  iAttachedReceivers++;

  TryStartService(); // if there is anyone observing start the service
}

void CRDSTMCService::OnReceiverDisconnected()
{
  NNavKit::NUtils::CAdaptationsMutexLocker lock(iMutex);

  printf("CRDSTMCService::OnReceiverDisconnected: #attachedReceivers %d -> %d, status %d\n",
    iAttachedReceivers,
    std::max(iAttachedReceivers - 1, 0),
    iStatus);

  assert(iAttachedReceivers > 0);
  if (iAttachedReceivers > 0)
  {
    iAttachedReceivers--;

    TryStopService();
  }
  else
  {
    printf("CRDSTMCService::OnReceiverDisconnected: WARNING: OnReceiverDisconnected called without OnReceiverConnected\n");
  }
}
