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

#ifndef ANDROID_PROPERTIES_H
#define ANDROID_PROPERTIES_H

#include "IProperties.h"
#include "AndroidJniUtil.h"
#include "AndroidJniAdaptation.h"
#include "AdaptationsMutex.h"

#include <string>
#include <vector>

namespace NProcessHost
{
  namespace NAdaptation
  {
    class CAndroidProperties: public NNavKit::NAdaptation::IProperties, public IJniAdaptation, private CAndroidJNIUtil
    {
    public:
      CAndroidProperties(JavaVM *aVM);

      virtual ~CAndroidProperties();

      //Consider recalling on sdcard event changes...
      virtual NNavKit::NAdaptation::IProperties* OnAcquire();
      virtual void OnRelease();

      virtual bool AcquireJniResources();
      virtual void ReleaseJniResources();

      virtual const char* GetWorkingDirectory() const
      {
        return iWorkingPath.c_str();
      }

      virtual const char* GetPersistentDirectory() const
      {
        return iPersistentPath.c_str();
      }

      virtual const char* GetPrivateContentDirectory() const
      {
        return iPrivateContentPath.c_str();
      }

      virtual const char* GetSharedContentDirectory() const
      {
        return iSharedContentPath.c_str();
      }

      virtual const char* GetTemporaryFilesDirectory() const
      {
        return iTemporaryFilesPath.c_str();
      }

      virtual unsigned short int GetListenerPort() const;

      virtual std::vector<std::string> GetReflectionListenerAddresses() const;

      virtual int GetContentProducerQuotaLimitInBytes() const
      {
        const int KQuotaLimitInBytes = (7 * 1024 * 1024);
        return KQuotaLimitInBytes;
      }

    private:
      bool SetStringValueFromJavaMehod(JNIEnv *aEnv, std::string& aPath, jmethodID aMethod);

      //! Ensures that directory "aPath" exists. Currently only one level deep is supported.
      void EnsureDirectoryExists(const std::string& aPath);

      jmethodID iGetWorkingDirectoryID;
      jmethodID iGetPersistentDirectoryID;
      jmethodID iGetPrivateContentDirectoryID;
      jmethodID iGetSharedContentDirectoryID;
      jmethodID iGetTemporaryFilesDirectoryID;
      jmethodID iGetReflectionListenerAddressID;

      std::string iWorkingPath;
      std::string iPersistentPath;
      std::string iPrivateContentPath;
      std::string iSharedContentPath;
      std::string iTemporaryFilesPath;
      std::string iReflectionListenerAddress;

      // Guards OnAcquire/OnRelease and all pure virtuals from  ITimeZoneChangedObserver
      NNavKit::NUtils::CAdaptationsMutex iMutex;
    };
  }
}

#endif /* ANDROID_PROPERTIES_H */
