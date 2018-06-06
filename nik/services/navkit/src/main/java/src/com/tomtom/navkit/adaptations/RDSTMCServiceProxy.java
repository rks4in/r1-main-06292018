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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class RDSTMCServiceProxy extends BroadcastReceiver
{
    private static final String TAG = RDSTMCServiceProxy.class.getSimpleName();

    private static final String ACTION_RDSTMC_RECEIVER_EVENT = "tomtom.intent.action.RDSTMC_RECEIVER_EVENT";
    private static final String EXTRA_RDSTMC_RECEIVER_ACTIVE = "tomtom.intent.extra.RDSTMC_RECEIVER_ACTIVE";

    private Context mContext = null;
    private long mRdsTmcServiceObserver = 0;

    public RDSTMCServiceProxy(Context aContext) {
        Log.i(TAG, "RDSTMCServiceProxy created");
        mContext = aContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received intent: " + intent.toString());

        if(intent.getAction().equals(ACTION_RDSTMC_RECEIVER_EVENT)){
            boolean active = intent.getBooleanExtra(EXTRA_RDSTMC_RECEIVER_ACTIVE, false);
            Log.d(TAG, "RDS-TMC receiver active: " + active);
            try {
                if (mRdsTmcServiceObserver != 0) {
                    Log.v(TAG, "RdsTmcReceiverEvent " + mRdsTmcServiceObserver + " before");

                    RdsTmcReceiverEvent(mRdsTmcServiceObserver, active);

                    Log.v(TAG, "RdsTmcReceiverEvent " + mRdsTmcServiceObserver + " after");
                }
            } catch (UnsatisfiedLinkError e) {
                Log.d(TAG, "Native " + e.toString() + " not found");
            }
        }
        else {
            Log.e(TAG, "Intent: " + intent.toString() + " was not caught");
        }
    }

    public void onConstruct(long rdstmcServiceObserver)
    {
        assert (rdstmcServiceObserver != 0);
        assert (mRdsTmcServiceObserver == 0);

        mRdsTmcServiceObserver = rdstmcServiceObserver;
        Log.v(TAG, "onConstruct " + mRdsTmcServiceObserver);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RDSTMC_RECEIVER_EVENT);

        mContext.registerReceiver(this, filter);
        Log.v(TAG, "Receiver registered");
    }

    public void onDestruct(long rdstmcServiceObserver)
    {
        assert (mRdsTmcServiceObserver == rdstmcServiceObserver);
        Log.v(TAG, "onDestruct " + mRdsTmcServiceObserver);

        mRdsTmcServiceObserver = 0;

        mContext.unregisterReceiver(this);
        Log.v(TAG, "Receiver unregistered");
    }

    public void startRemoteService() {
        Log.i(TAG, "startRemoteService: Sending START intent to remote service");
        Intent intent = new Intent("com.tomtom.traffic.tmc.START");
        intent.addFlags(0x00000020); // Intent.FLAG_INCLUDE_STOPPED_PACKAGES, Constant Value: 32 (0x00000020), since API Level 12
        mContext.sendBroadcast(intent);
        Log.i(TAG, "startRemoteService: exit");
    }

    public void stopRemoteService() {
        Log.i(TAG, "stopRemoteService: Sending STOP intent to remote service");
        Intent intent = new Intent("com.tomtom.traffic.tmc.STOP");
        mContext.sendBroadcast(intent);
        Log.i(TAG, "stopRemoteService: exit");
    }

    private static native void RdsTmcReceiverEvent(long rdstmcService, boolean active);
}
