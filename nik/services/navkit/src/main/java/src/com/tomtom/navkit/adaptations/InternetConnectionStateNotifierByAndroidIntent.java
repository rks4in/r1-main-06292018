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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Listens to connectivity events happening in the system and forwards the
 * information to NavKit.
 * This class is instantiated and used in the C++ part of the adaptation.
 */
public class InternetConnectionStateNotifierByAndroidIntent extends BroadcastReceiver {
    static private final String TAG = InternetConnectionStateNotifierByAndroidIntent.class.getSimpleName();
    static private final int NO_NETWORK = -1;

    private Context context = null;
    private long internetConnectionStateNotifier = 0;

    public InternetConnectionStateNotifierByAndroidIntent(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (
            (context != null)
            && (intent != null)
            && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)
            ) {
            Log.v(TAG, "Connectivity action received '" + intent + "'");
            NetworkInfo network = getActiveNetworkInfo(context);
            boolean connected = isConnected(network);
            boolean roaming = isMobileNetworkRoaming(context);
            int type = getNetworkType(network);
            Log.i(TAG, "InternetConnectionState = " + (connected ? "connected" : "disconnected"));
            if (internetConnectionStateNotifier != 0) {
                Log.v(TAG, "informNavkit " + internetConnectionStateNotifier);
                informNavKit(internetConnectionStateNotifier, connected, type, roaming);
            }
        }
    }

    /**
     * Attaches a notifier adaptation to this object.
     * This is called from the C++ code, the notifier adaptation creates an instance
     * of this class and then registers itself with it so that it can call it back
     * with informNavKit. Note that it's only possible to attach up to one notifier.
     *
     * @param notifier C++ pointer to a CInternetConnectionStateNotifierByAndroidIntent.
     */
    public void AttachNotifier(long notifier) {
        assert (notifier != 0);
        assert (internetConnectionStateNotifier == 0);

        internetConnectionStateNotifier = notifier;
        Log.v(TAG, "AttachNotifier " + internetConnectionStateNotifier);

        // CONNECITIVY_ACTION is sticky, registering with connectivity available triggers a first callback immediately
        // This stickiness is an unofficial Android feature, but blogposts indicate it is widely used
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
        Log.v(TAG, "Receiver registered");
    }

    /**
     * Detach the notifier adaptation.
     * Also called from the C++ code, when releasing resources.
     *
     * @param notifier C++ pointer to a CInternetConnectionStateNotifierByAndroidIntent.
     */
    public void DetachNotifier(long notifier) {
        assert (internetConnectionStateNotifier == notifier);
        Log.v(TAG, "DetachNotifier " + internetConnectionStateNotifier);

        internetConnectionStateNotifier = 0;
        context.unregisterReceiver(this);
        Log.v(TAG, "Receiver unregistered");
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivity =
            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo network = connectivity.getActiveNetworkInfo();
        if (network == null) {
            Log.i(TAG, "No active network");
        }
        return network;
    }

    private static boolean isConnected(NetworkInfo network) {
        if (network == null) {
            return false;
        }
        return network.isConnected();
    }

    private static int getNetworkType(NetworkInfo network) {
        if (network == null) {
            return NO_NETWORK;
        }
        return network.getType();
    }

    private static boolean isMobileNetworkRoaming(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return false;
        }
        return tm.isNetworkRoaming();
    }

    /**
     * Reports connectivity information back to NavKit.
     *
     * @param notifierHandle Pointer to the C++ notifier adaptation.
     * @param connected      True if the device now has a default connection, false otherwise.
     * @param networkType    Type of the network, see ConnectivityManager for more details.
     * @param roaming        True if the phone network is roaming.
     */
    private static native void informNavKit(long notifierHandle, boolean connected, int networkType, boolean roaming);
}
