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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.harman.fcaclock.Constants;
import com.harman.fcaclock.IFcaClockService;
import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.taskkit.TaskContext;
import com.tomtom.navui.taskkit.currentposition.CurrentPositionTask;
import com.tomtom.navui.taskkit.route.Position;
import com.tomtom.navui.taskkit.route.RouteGuidanceTask;
import com.tomtom.navui.util.Log;

import java.util.TimeZone;

public class R1TimeZoneHandler implements RouteGuidanceTask.CurrentPositionListener,
                                          TaskContext.ContextStateListener {

    private static final String TAG = "R1TimeZoneHandler";

    private AppContext mAppContext;

    private CurrentPositionTask mCurrentPositionTask;

    private TimeZone mPreviousTimeZone;

    private IFcaClockService mFcaClockService;

    private final Object mFcaClockServiceLock = new Object();

    private Handler mHandler;

    private final ServiceConnection mFcaClockServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (Log.D) {
                Log.d(TAG, IFcaClockService.class.getCanonicalName() + " connected");
            }

            synchronized (mFcaClockServiceLock) {
                mFcaClockService = IFcaClockService.Stub.asInterface(iBinder);
            }

            // Send previous TimeZone if Service is Reconnected
            if (mPreviousTimeZone != null) {
                sendTimeZone(mPreviousTimeZone);
            } else {
                initTaskContext();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if (Log.D) {
                Log.d(TAG, IFcaClockService.class.getCanonicalName()  + " disconnected");
            }

            synchronized (mFcaClockServiceLock) {
                mFcaClockService = null;
            }

            // Don't release CurrentPositionTask and TaskContext
            // because service will reconnect automatically and onServiceConnected will be called
        }
    };

    public R1TimeZoneHandler(AppContext appContext) {
        mAppContext = appContext;

        HandlerThread handlerThread = new HandlerThread(TAG + "Thread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        mHandler = new Handler(looper);

        bindFcaClockService();
    }

    @Override
    public void onTaskContextReady() {
        initCurrentPositionTask();
    }

    @Override
    public void onTaskContextMapStateChange(TaskContext.MapState mapState) {
        // Not needed
    }

    @Override
    public void onTaskContextLost(Boolean recoverable, ErrorCode error) {
        releaseCurrentPositionTask();
    }

    @Override
    public void onCurrentPositionResult(Position position) {
        if (mCurrentPositionTask != null) {
            if (mCurrentPositionTask.getCurrentPositionStatus() ==
                    RouteGuidanceTask.PositionStatusChangedListener.PositionStatus.GPS) {

                TimeZone timeZone = mCurrentPositionTask.getTimeZone();
                if (!timeZone.equals(mPreviousTimeZone)) {
                    if (Log.D) {
                        Log.d(TAG, "onCurrentPositionResult: new TimeZone received - " +
                                timeZone.getDisplayName());
                    }

                    sendTimeZone(timeZone);

                    mPreviousTimeZone = timeZone;
                }
            }
        }
    }

    private void sendTimeZone(TimeZone timeZone) {
        mHandler.post(() -> {
            synchronized (mFcaClockServiceLock) {
                if (mFcaClockService != null) {
                    try {
                        int errorCode = mFcaClockService.setTimeZone(timeZone.getID(),
                                timeZone.getDSTSavings());

                        if (errorCode == Constants.SERVICE_SEND_SUCCESS) {
                            if (Log.D) {
                                Log.d(TAG, "TimeZone was successfully sent to " +
                                                IFcaClockService.class.getCanonicalName() +
                                        ", id = " + timeZone.getID() +
                                        " dstOffsetMilli = " + timeZone.getDSTSavings());
                            }
                        } else {
                            if (Log.W) {
                                Log.w(TAG, "Can not send TimeZone to F" +
                                        IFcaClockService.class.getCanonicalName() +
                                        ", Error Code = " + errorCode);
                            }
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void bindFcaClockService() {
        Intent intent = new Intent();
        intent.setPackage(Constants.FCA_CLOCK_PACKAGE);
        if (!mAppContext.getSystemPort().getApplicationContext().bindService(
                intent, mFcaClockServiceConnection, Context.BIND_AUTO_CREATE)) {
            if (Log.E) {
                Log.e(TAG, "Cannot bind to " + IFcaClockService.class.getCanonicalName());
            }
        }
    }

    private void initTaskContext() {
        if (mAppContext.getTaskKit().isReady()) {
            initCurrentPositionTask();
        } else {
            mAppContext.getTaskKit().addContextStateListener(this);
        }
    }

    private void initCurrentPositionTask() {
        if (mCurrentPositionTask == null) {
            mCurrentPositionTask = mAppContext.getTaskKit().newTask(CurrentPositionTask.class);
            mCurrentPositionTask.addCurrentPositionListener(this);
        }
    }

    private void releaseCurrentPositionTask() {
        if (mCurrentPositionTask != null) {
            mCurrentPositionTask.release();
            mCurrentPositionTask = null;
        }
    }
}