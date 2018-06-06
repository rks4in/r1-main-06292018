//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef __RDSTMCService_h__
#define __RDSTMCService_h__

#include "IRDSTMC.h"
#include "AdaptationsMutex.h"
#include <stdint.h>

namespace NRDSTMC
{
  class CRDSTMCService : public IRDSTMCService
  {
  public:
    CRDSTMCService();
    virtual ~CRDSTMCService();

    virtual NNavKit::NAdaptation::IControlAdaptation* OnAcquire();
    virtual void OnRelease();

    // called upon hardware events
    void OnReceiverConnected();
    void OnReceiverDisconnected();

    virtual bool StartService();
    virtual void StopService();

    virtual bool HasReceivers() const { return iAttachedReceivers > 0; }

    virtual NNavKit::NAdaptation::CRDSTMCServiceStateNotifier& GetRDSTMCServiceStateNotifier()
    {
      return iRDSTMCServiceStateNotifier;
    }
  protected:
    void SetStatus(TRDSTMCServiceStatus aStatus);

    bool TryStartService();
    void TryStopService();

    // get path to the TMC files
    const char* GetFilesDir() { return "./home"; }

  private:
    class CRDSTMCServiceStateNotifierImplementation : public NNavKit::NAdaptation::CRDSTMCServiceStateNotifier
    {
    public:
      virtual ~CRDSTMCServiceStateNotifierImplementation() {}

      void NotifyStatus(TRDSTMCServiceStatus aStatus);
    };
    CRDSTMCServiceStateNotifierImplementation iRDSTMCServiceStateNotifier;

    TRDSTMCServiceStatus iStatus;
    int32_t iAttachedReceivers;
    int32_t iStartRequests;
    int32_t iAcquiredCounter;

    CRDSTMCService(const CRDSTMCService &);
    const CRDSTMCService& operator=(const CRDSTMCService &);

    virtual NNavKit::NAdaptation::IControlAdaptation* DoOnAcquire() = 0;
    virtual void DoOnRelease() = 0;

    // Platform dependent code
    virtual bool StartRemoteService() = 0; // True - if remote service (daemon) started
    virtual void StopRemoteService() = 0;

    virtual const char* GetRemoteServiceLocation() const = 0;

    // Guards OnAcquire/OnRelease, all pure virtuals from  IRDSTMCServiceController
    // and all methods receiving events from the underlying Platform Software (i.e.
    // OnReceiverConnected and OnReceiverDisconnected)
    NNavKit::NUtils::CAdaptationsMutex iMutex;
  };
}

#endif // __RDSTMCService_h__
