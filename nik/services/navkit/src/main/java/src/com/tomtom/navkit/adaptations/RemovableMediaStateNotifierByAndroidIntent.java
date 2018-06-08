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



// Our C++ counterpart will instantiate an object of RemovableMediaStateNotifierByAndroidIntent on which
// it can register and unregister (notifier parameter is the C++'s this pointer). From the register we 
// set an IntentFilter for the broadcasts intents.
// Whenever ACTION_MEDIA_UNMOUNTED or ACTION_MEDIA_MOUNTED is received, Android will instantiate separate
// obejcts from RemovableMediaStateNotifierByAndroidIntent on which it calls OnReceive. That's why 
// RemovableMediaStateNotifier needs to be static

public class RemovableMediaStateNotifierByAndroidIntent extends BroadcastReceiver {
    static private final String TAG = RemovableMediaStateNotifierByAndroidIntent.class.getSimpleName();

    public RemovableMediaStateNotifierByAndroidIntent(Context context) {
        // constructor used when instantiated by our C++ counterpart
    }
    
    public RemovableMediaStateNotifierByAndroidIntent() {
        // constructor used when Android is delivering broadcasts intents
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (
            (context != null)
         && (intent  != null)
         && (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED) ||
             intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)   ||
             intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED))
        ) {
            boolean mounted = intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED);
            String path = intent.getData().getPath();

            Log.v(TAG, "Removable media event received '" + intent + "'");
            if (removableMediaStateNotifier != 0)
            {
                Log.v(TAG, "Informing NavKit");
                informNavKit(removableMediaStateNotifier, mounted, path);
            }
        }
    }

    public void AttachNotifier(long notifier) {
        assert (notifier != 0);
        assert (removableMediaStateNotifier == 0);

        removableMediaStateNotifier = notifier;
        Log.v(TAG, "AttachNotifier " + removableMediaStateNotifier);
    }

    public void DetachNotifier(long notifier)
    {
      assert (removableMediaStateNotifier == notifier);
      Log.v(TAG, "DetachNotifier " + removableMediaStateNotifier);

      removableMediaStateNotifier = 0;
      Log.v(TAG, "Receiver unregistered");
    }

    static private long removableMediaStateNotifier = 0;

    private static native void informNavKit(long removableMediaStateNotifier, boolean mounted, String path);
}
