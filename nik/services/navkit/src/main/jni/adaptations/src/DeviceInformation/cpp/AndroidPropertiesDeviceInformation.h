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

#ifndef ANDROID_PROPERTIES_DEVICE_INFORMATION_H
#define ANDROID_PROPERTIES_DEVICE_INFORMATION_H

#include "IDeviceInformation.h"
#include "AndroidJniUtil.h"
#include "AndroidJniAdaptation.h"
#include "AdaptationsMutex.h"
#include <stdint.h>
#include <sys/system_properties.h>

namespace NProcessHost
{
  namespace NAdaptation
  {

    //!
    //! Device information for Android devices.
    //!
    //! It is assumed that all accesses to retrieve the information
    //! are rather expensive, so the information is cached.
    //!
    class CAndroidPropertiesDeviceInformation
        : public NNavKit::NAdaptation::IDeviceInformation
        , public IJniAdaptation
        , private CAndroidJNIUtil
    {
    public:
      CAndroidPropertiesDeviceInformation(JavaVM *aVM);
      virtual ~CAndroidPropertiesDeviceInformation();

      virtual bool AcquireJniResources();
      virtual void ReleaseJniResources();
      virtual NNavKit::NAdaptation::IControlAdaptation* OnAcquire();
      virtual void OnRelease();
      
      virtual uint32_t GetMachineUniqueId(char* aBuffer, const uint32_t aBufferSize);
      virtual uint32_t GetDRMUniqueIds(const uint32_t aIndex, char* aBuffer, const uint32_t aBufferSize);
      virtual uint32_t GetLiveServicesUniqueId(char* aBuffer, const uint32_t aBufferSize);
      virtual uint32_t GetMediaIds(const uint32_t aIndex, uint8_t* aBuffer, const uint32_t aBufferSize);
      virtual const char* GetOTAName();
      virtual const char* GetGPSFirmwareVersion();
      virtual const char* GetGPSType();
      virtual bool HasWideScreen();
      virtual const char* GetIMEICode();
      virtual const char* GetICCIDCode();

      //! Gets serial number of the device
      //! Requires:
      //!   Buffer pointed to by "aSerialNumber" with minimum size of "PROP_VALUE_MAX".
      //!   Value of aLength should be at least PROP_VALUE_MAX, aSerialNumber
      //!   may contain truncated serial-number for smaller value.
      //! Returns:
      //!   Length of serial number. On return aSerialNumber contains the serial number of the device.
      uint32_t GetSerialNumber(char* aSerialNumber, uint32_t aLength);

    protected:
      //! Call the host app via JNI and ask for the device ID (which, at the time of this writing,
      //! should be the System.Secure.ANDROID_ID value. Note that any postprocessing needed to
      //! turn this string into a TomTom-formatted MUID, is not the responsibility of this method.
      uint32_t GetMuidFromHost(char* aBuffer, const uint32_t aLength) const;
      uint32_t GetIMEIFromHost(char* aBuffer, uint32_t aLength) const;
      uint32_t GetICCIDFromHost(char* aBuffer, uint32_t aLength) const;

      void CopyPropertyValue(
        char*       aDestination,
        const char* aSource,
        uint32_t aMaxSize,
        const char* functionName
      );

      CAndroidPropertiesDeviceInformation(const CAndroidPropertiesDeviceInformation &);
      const CAndroidPropertiesDeviceInformation& operator=(const CAndroidPropertiesDeviceInformation &);

      char iMachineUniqueId     [PROP_VALUE_MAX];
      char iLiveServicesUniqueId[PROP_VALUE_MAX];
      char iOTAName             [PROP_VALUE_MAX];
      char iGPSFirmwareVersion  [PROP_VALUE_MAX];
      char iGPSType             [PROP_VALUE_MAX];
      char iIMEICode            [PROP_VALUE_MAX];
      char iICCIDCode           [PROP_VALUE_MAX];

    private:
      jmethodID iGetMachineUniqueIdMethodId;
      jmethodID iGetSerialNumberMethodId;
      jmethodID iGetMediaIdsMethodId;
      jmethodID iGetIMEICodeMethodId;
      jmethodID iGetICCIDCodeMethodId;

      // Guards OnAcquire/OnRelease and all pure virtuals from IDeviceInformation
      NNavKit::NUtils::CAdaptationsMutex iMutex;
    };
  }
}

#endif
