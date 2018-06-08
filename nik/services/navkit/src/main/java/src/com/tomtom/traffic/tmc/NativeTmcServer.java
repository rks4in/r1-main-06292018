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

package com.tomtom.traffic.tmc;

import android.util.Log;

public class NativeTmcServer {
    private static final String TAG = NativeTmcServer.class.getSimpleName();

    public native void open(String aTmcFilePath);
    public native void close();

    public native boolean process();

    public native String getDiagInfo();

    /* this is used to load the native libraries.
     */
    static {
        try {
            Log.d(TAG, "Loading native libraries");
            System.loadLibrary("gnustl_shared");
            System.loadLibrary("tmcserver");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load library: " + e.getMessage());
        }
    }
}
