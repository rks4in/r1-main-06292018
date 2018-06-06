//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include <android/log.h>
#include <unistd.h>
#include <jni.h>
#include <string>

#include "NavKitRunnerAndroid.h"
#include "NavKitJavaVM.h"
#include "StdioConverter.h"
#include "AndroidServiceAdaptationProvider.h"
#include "AndroidPropertiesDeviceInformation.h"
#include "AndroidProperties.h"

#if defined(SUPPORT_ASSERTS)
  #include "osutils.h"
#endif

void LoggingAssertHandler(const char *aCondition, const char *aFile, int aLine)
{
  __android_log_print(
    ANDROID_LOG_ERROR,
    "NavKit",
    "Assertion failed: %s, file %s:%d", 
    (const char*) ((aCondition != NULL) ? aCondition : "Unknown"),
    (const char*) aFile,
    (int) aLine
  );
}

static void CheckprocessID()
{
  static __pid_t sMyProcessID = 0;

  if (sMyProcessID != 0)
  {
  __android_log_print(ANDROID_LOG_ERROR, "NavKit", "***");
  __android_log_print(ANDROID_LOG_ERROR, "NavKit", "NAVKIT IS RESTARTED IN THE SAME PROCESS (%d) !!!", sMyProcessID);
  __android_log_print(ANDROID_LOG_ERROR, "NavKit", "***");
  }
  else
  {
    sMyProcessID = getpid();
  }
}

void CNavKitRunnerAndroid::PDSetup()
{
  CheckprocessID();

  // redirect stdout/stderr before the first printf
  dvmStdioConverterStartup();

  #if defined(SUPPORT_ASSERTS)
    SetAssertHandler(LoggingAssertHandler);
  #endif
}

void CNavKitRunnerAndroid::PDTeardown()
{
  dvmStdioConverterShutdown();
}
