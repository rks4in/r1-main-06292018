/*
 * Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 * 
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.navkit.adaptations;

import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.provider.Settings.Secure;
import android.util.Log;
import java.util.List;
import java.io.File;

/**
 * Provides access to device specific information 
 * 
 */
public class AndroidPropertiesDeviceInformation {
    private static final String TAG = AndroidPropertiesDeviceInformation.class.getSimpleName();

    private Context context = null;

    public AndroidPropertiesDeviceInformation(Context context) {
        Log.i(TAG, "AndroidPropertiesDeviceInformation created");
        this.context = context;
    }

    /**
     * Return the machine unique identifier (MUID). This is called from the
     * DeviceInformation class in the Adaptation layer in order to inject the MUID into
     * NavKit.
     * 
     * @return the MUID. Can return null if context is not yet
     *         initialized. Take care to check for this
     */
    public String getMachineUniqueId() {
        if (context == null) {
            return null;
        }
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * Return the serial number of device.
     * 
     * @return the serialno.
     */
    public String getSerialNumber() {
      return android.os.Build.SERIAL;
    }
    
    /**
     * Return the expansion card unique identifier (CID). This is called from the
     * DeviceInformation class in the Adaptation layer in order to inject the CID into
     * NavKit.
     *
     * @param aIndex is index of which CID has to be returned
     * @return the CID[aIndex]. Can return null if context is not yet initialized
     *    Take care to check for this null return value
     */
    public String getMediaIds(long index) {
        return "";
    }     

    /**
     * Returns the IMEI for this device.
     * 
     * @return the IMEI; Be aware: will return null in case error occurs
     */
    public String getIMEICode() {
        TelephonyManager telephonyManager = getTelephonyManager();
        // TelephonyManager may be null in that case return null
        if (telephonyManager == null) {
            Log.e(TAG, "doGetIMEICode telephonyManager == null");
            return null;
        }

        String imei = telephonyManager.getDeviceId();
        return imei;
    }
	
    /**
     * Returns the ICCID for this device.
     * 
     * @return the ICCID; Be aware: will return null in case error occurs
     */
    public String getICCIDCode() {
        TelephonyManager telephonyManager = getTelephonyManager();
        // TelephonyManager may be null in that case return null
        if (telephonyManager == null) {
            Log.e(TAG, "doGetICCIDCode telephonyManager == null");
            return null;
        }

        String iccid = telephonyManager.getSimSerialNumber();
        return iccid;
    }

    //! @return TelephonyManager for this device or null in case there is no TelephonyManager
    private TelephonyManager getTelephonyManager() {
        // Context may be null if the PAL native code is not yet initialized.
        if (context == null) {
            Log.e(TAG, "getTelephonyManager context == null");
            return null;
        }

        // In case this device does not have TELEPHONY support return null
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            Log.e(TAG, "getTelephonyManager no FEATURE_TELEPHONY");
            return null;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager;
    }
}
