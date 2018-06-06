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

package com.tomtom.navkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.util.Log;
import android.app.Service;

public class NavKitShutdownReceiver extends BroadcastReceiver {
    private static final String TAG = "NavKitShutdownReceiver";
    private static final String NAVKIT_SERVICE = "com.tomtom.navkit.NavKit";
    private static final String HTC_SHUTDOWN_ACTION = "android.intent.action.QUICKBOOT_POWEROFF";
    private    PowerOffObserver iObserver = null;
    
    public interface PowerOffObserver {
        void onPowerOff();
    }
    
    public static NavKitShutdownReceiver createNavKitShutdownReceiver(Context context, PowerOffObserver observer) {
         
        NavKitShutdownReceiver navKitShutdownReceiver = new NavKitShutdownReceiver(observer);
        IntentFilter intentFilter= new IntentFilter();
        // Support HTC
        intentFilter.addAction(NavKitShutdownReceiver.HTC_SHUTDOWN_ACTION);
        // Support the rest of the world
        intentFilter.addAction(Intent.ACTION_SHUTDOWN);
        context.registerReceiver(navKitShutdownReceiver, intentFilter);
        Log.v(TAG, "Navkit is registered to Android Shutdown event");
       
        return navKitShutdownReceiver;
    }
    
    public void stop(Context context) {
        iObserver = null;
        context.unregisterReceiver(this);
        Log.v(TAG, "Navkit is unregistered to Android Shutdown event");
    }
    
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)||
            intent.getAction().equals(HTC_SHUTDOWN_ACTION)){
            Log.v(TAG, "======|Received Action: " + intent.getAction() + " |======");
            Log.v(TAG, "======|Propagating event to: " + NAVKIT_SERVICE+ " |======");
            if (iObserver != null)
                iObserver.onPowerOff();
        }
    }

    private NavKitShutdownReceiver(PowerOffObserver observer) {
        iObserver = observer;
    }
}
