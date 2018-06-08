/*!
 * \file
 * \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 * <br>
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

#ifndef __RDSTMCServiceAndroid_h__
#define __RDSTMCServiceAndroid_h__

#include "RDSTMCService.h"
#include "AndroidJniUtil.h"
#include "AndroidJniAdaptation.h"
#include "jni.h"

namespace NRDSTMC
{
  class CRDSTMCServiceAndroid
      : public CRDSTMCService
      , public NProcessHost::NAdaptation::IJniAdaptation
      , private CAndroidJNIUtil
  {
  public:
    CRDSTMCServiceAndroid(JavaVM* aVM);
    virtual ~CRDSTMCServiceAndroid();

    virtual bool AcquireJniResources();
    virtual void ReleaseJniResources();
  private:
    virtual NNavKit::NAdaptation::IControlAdaptation* DoOnAcquire();
    virtual void DoOnRelease();

    virtual bool StartRemoteService(); // True - if remote service (daemon) started
    virtual void StopRemoteService();

    virtual const char* GetRemoteServiceLocation() const { return ""; }
  private:
    jmethodID iStartRemoteServiceMethodID;
    jmethodID iStopRemoteServiceMethodID;
  };
} // namespace NRDSTMC

#endif // __RDSTMCServiceAndroid_h__
