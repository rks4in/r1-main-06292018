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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/* 
 * This is a head-less Activity to fire up the process then quit.
 */
public class RdsTmcActivity extends Activity {
    private static final String TAG = RdsTmcActivity.class.getSimpleName();

    private static final String ACTION_RDSTMC_RECEIVER_EVENT = "tomtom.intent.action.RDSTMC_RECEIVER_EVENT";
    private static final String EXTRA_RDSTMC_RECEIVER_ACTIVE = "tomtom.intent.extra.RDSTMC_RECEIVER_ACTIVE";

     /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: entry");
        super.onCreate(savedInstanceState);

        // Restore preferences
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        boolean active = settings.getBoolean(EXTRA_RDSTMC_RECEIVER_ACTIVE, false);
        Log.d(TAG, "onCreate: restored preference active " + active);

        // toggle active flag
        active = !active;

        if (active) {
          // connect TMC traffic
          this.sendBroadcast(new Intent(RdsTmcIntentCatcher.ACTION_CONNECT_TMC));
        } else {
          // disconnect TMC traffic
          this.sendBroadcast(new Intent(RdsTmcIntentCatcher.ACTION_DISCONNECT_TMC));
        }

        // Commit preferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(EXTRA_RDSTMC_RECEIVER_ACTIVE, active);
        Log.d(TAG, "onCreate: commit preference active " + active);
        editor.commit();

        // activity is done and should be closed
        Log.d(TAG, "onCreate: finish");
        finish();
    }
}
