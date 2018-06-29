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

package com.tomtom.r1navapp.presentation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import com.tomtom.navui.util.Log;

public abstract class PresentationService extends Service implements PresentationHelper.Listener {

    private static final String TAG = "PresentationService";

    private static final String DISPLAY_SERVICE_PARAM = "CLUSTER_DISPLAY_ID";

    private WindowManager mWindowManager;
    private View mPresentationView;
    private PresentationHelper mPresentationHelper;
    private boolean mIsClusterServiceStarted;

    protected abstract int getThemeId();

    protected abstract View buildPresentationView(Context context, LayoutInflater inflater);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPresentationHelper = new PresentationHelper(this, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String displayIdString = bundle.getString(DISPLAY_SERVICE_PARAM);
                if (displayIdString != null) {
                    if (Log.D) {
                        Log.d(TAG, "onStartCommand got display ID ( " + displayIdString + " )");
                    }
                    mPresentationHelper.registerAndHandleDisplayEventForId(Integer.parseInt(displayIdString));
                } else {
                    if (Log.E) {
                        Log.e(TAG, "onStartCommand display ID parameter not mentioned");
                    }
                }
            } else {
                if (Log.E) {
                    Log.e(TAG, "onStartCommand bundle is empty");
                }
            }
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        if (mIsClusterServiceStarted) {
            mPresentationHelper.stopPresentationAndUnregister();
        } else {
            mPresentationHelper.unregisterDisplayListener();
        }
        super.onDestroy();
    }

    @Override
    public void handleStartPresentation(Display display) {

        if (Log.D) {
            Log.d(TAG, "handleStartPresentation()");
        }
        Context displayContext = createDisplayContext(display);
        displayContext.setTheme(getThemeId());

        mWindowManager = (WindowManager)displayContext.getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = LayoutInflater.from(displayContext);

        mPresentationView = buildPresentationView(displayContext, inflater);

        mWindowManager.addView(mPresentationView, new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.OPAQUE));

        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mIsClusterServiceStarted = true;
    }

    @Override
    public void handleStopPresentation() {
        if (Log.D) {
            Log.d(TAG, "handleStopPresentation()");
        }
        mWindowManager.removeView(mPresentationView);
        mPresentationView = null;
        mIsClusterServiceStarted = false;
    }

    @Override
    public void handleNoDisplayAndStopService() {
        if (Log.I) {
            Log.i(TAG, "No cluster display detected, stopping cluster service");
        }
        stopSelf();
    }
}