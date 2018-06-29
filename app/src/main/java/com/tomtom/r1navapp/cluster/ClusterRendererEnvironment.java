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

package com.tomtom.r1navapp;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.tomtom.navkit.map.Callback;
import com.tomtom.navkit.map.DpiMapping;
import com.tomtom.navkit.map.sdk.Environment;
import com.tomtom.navkit.map.TomTomNavKitMapJNI;
import com.tomtom.navui.util.Log;

/**
 * Android implementation of the Map API environment class.
 */
public class ClusterRendererEnvironment extends Environment {

    private static final String TAG = "ClusterRendererEnvironment";

    private final long mDpi;
    private final long mCpuCoreCount;
    private final Environment mSdkEnvironment;

    /**
     * Constructs an environment that will return the DPI of the cluster display, the private
     * files folder as base asset path, and the number of cpu cores on the device.
     * @param context context used to get to the default display and the files directory.
     */
    public ClusterRendererEnvironment(Context context) {
        super(context);
        mSdkEnvironment = new Environment(context);
        mCpuCoreCount = TomTomNavKitMapJNI.nativeGetCpuCoreCount();

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mDpi = metrics.densityDpi;

        if (Log.D) {
            Log.d(TAG, "Initializing cluster map environemnt with DPI ( " + mDpi + " )");
        }
    }

    @Override
    public long getDpi() {
        return mDpi;
    }

    @Override
    public String getAssetBasePath() {
        return mSdkEnvironment.getAssetBasePath();
    }

    @Override
    public String getAssetExtractionPath() {
        return mSdkEnvironment.getAssetExtractionPath();
    }

    @Override
    public long getCpuCoreCount() {
        return mCpuCoreCount;
    }

    @Override
    public DpiMapping getDpiMapping() {
        return mSdkEnvironment.getDpiMapping();
    }

    @Override
    public void doCallbackOnMainThread(final Callback callback) {
        mSdkEnvironment.doCallbackOnMainThread(callback);
    }

    @Override
    public long getMainThreadSlackTimeMilliseconds() {
        return mSdkEnvironment.getMainThreadSlackTimeMilliseconds();
    }
}