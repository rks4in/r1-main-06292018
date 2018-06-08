//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef __IRDSTMC_h__
#define __IRDSTMC_h__

#include "IRDSTMCServiceController.h"
#include "RDSTMCServiceStateNotifier.h"

namespace NRDSTMC
{
  class IRDSTMCReceiverManager
  {
  public:
    virtual ~IRDSTMCReceiverManager() {}

    virtual void OnReceiverConnected() = 0;
    virtual void OnReceiverDisconnected() = 0;
  };

  class IRDSTMCService
  : public NNavKit::NAdaptation::IRDSTMCServiceController
  , public IRDSTMCReceiverManager
  {
  public:
    enum TRDSTMCServiceStatus
    {
      ERDSTMCServiceAvailable,
      ERDSTMCServiceUnavailable
    };

    virtual ~IRDSTMCService() {}

    virtual NNavKit::NAdaptation::CRDSTMCServiceStateNotifier& GetRDSTMCServiceStateNotifier() = 0;

    // The implementation of this interface will track the connectivity of radio antenna.
    // Once antenna is connected and there is >=1 observer that's registered the "Start RDSTMC service" intent will be
    // sent to the system and there will be an application that will serve this intent by starting up
    // an appropriate RDSTMC daemon (or by passing the antenna type as details)
    // If the "Start RDSTMC service" has been sent, and antenna disconnects or last observer unregisters, an opposite
    // intent of "Stop RDSTMC service" would be sent to the system, that would end up in stopping RDSTMC daemon.
  };
}

#endif // __IRDSTMC_h__
