//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef NAVKIT_RUNNER_ANDROID_H
#define NAVKIT_RUNNER_ANDROID_H

#include "NavKitMain.h"
#include <sys/system_properties.h>
#include <cassert>

class CNavKitRunnerAndroid
{
public:
  CNavKitRunnerAndroid(NNavKit::IProductContext* aProductContext)
    : iProductContext(aProductContext)
    , iIsRunning(false)
    , iNavKitMainCode(ENavKitMainOk)
  {
    assert(iProductContext != NULL);
  }

  ~CNavKitRunnerAndroid()
  {
    delete iProductContext;
    iProductContext = NULL;
  }
  
  void Setup()
  {
    PDSetup();
  }

  bool StartNavKit()
  {
    Run();
    return (iNavKitMainCode == ENavKitMainOk);
  }

  bool IsRunning()
  {
    return iIsRunning;
  }
  
  bool StopNavKit()
  {
    bool success  = false;
    bool tearDown = false;

    TNavKitTerminateCode navKitTerminateCode = NavKitTerminate();
    switch (navKitTerminateCode)
    {
      case ENavKitTerminateTimeOut:
        success  = false;
        tearDown = true;
        break;

      case ENavKitTerminateOk:
        success  = true;
        tearDown = true;
        break;

      case ENavKitTerminateAlreadyCalled:
        success  = true;
        tearDown = false;
        break;

      default:
        assert(0 && "Unknown TNavKitTerminateCode");
        break;
    }

    if (tearDown)
    {
      PDTeardown();
    }

    return success;
  }

  void Teardown()
  {
    PDTeardown();
  }

  TNavKitMainCode GetNavKitMainCode() const { return iNavKitMainCode; }

protected:
  void Run()
  {
    assert(iProductContext != NULL);

    iIsRunning = false;
    iNavKitMainCode = NavKitMain(*iProductContext);
    iIsRunning = false;
  }

private:
  NNavKit::IProductContext* iProductContext;
  bool iIsRunning;
  TNavKitMainCode iNavKitMainCode;
  
  // Platform dependent methods
  void PDSetup();
  void PDTeardown();
};

#endif // NAVKIT_RUNNER_ANDROID_H
