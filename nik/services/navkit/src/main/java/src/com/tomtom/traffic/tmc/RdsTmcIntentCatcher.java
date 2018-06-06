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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RdsTmcIntentCatcher extends BroadcastReceiver {
    private static final String TAG = RdsTmcIntentCatcher.class.getSimpleName();
    
    public static final String ACTION_RDSTMC_RECEIVER_EVENT = "tomtom.intent.action.RDSTMC_RECEIVER_EVENT";
    public static final String EXTRA_RDSTMC_RECEIVER_ACTIVE = "tomtom.intent.extra.RDSTMC_RECEIVER_ACTIVE";

    private static final String ACTION_START_TMC = "com.tomtom.traffic.tmc.START";
    public static final String ACTION_STOP_TMC = "com.tomtom.traffic.tmc.STOP";

    public static final String ACTION_CONNECT_TMC = "com.tomtom.traffic.tmc.CONNECT";
    public static final String ACTION_DISCONNECT_TMC = "com.tomtom.traffic.tmc.DISCONNECT";

    public static final String ACTION_FACTORY_RESET_TMC = "com.tomtom.traffic.tmc.FACTORY_RESET";

    /**
     * Intent broadcast when the RDS-TMC connection state changes.
     */
    private static final String ACTION_POZNANLITE_STATE = "tomtom.platform.hardware.ACTION_POZNAN_LITE_STATE"; // deprecated from ESTRELLA2 onwards
    private static final String ACTION_RDSTMC_STATE = "tomtom.platform.hardware.ACTION_RDSTMC_STATE";

    /**
     * Extra in the ACTION_RDSTMC_STATE intent. The value is one of the states listed below.
     */
    private static final String EXTRA_POZNANLITE_STATE = "tomtom.platform.hardware.EXTRA_POZNAN_LITE_STATE"; // deprecated from ESTRELLA2 onwards
    private static final String EXTRA_RDSTMC_STATE = "tomtom.platform.hardware.EXTRA_RDSTMC_STATE";

    /**
     * Extra in the ACTION_RDSTMC_STATE intent. The value is one of the device types listed below.
     */
    private static final String EXTRA_RDSTMC_DEVTYPE = "tomtom.platform.hardware.EXTRA_RDSTMC_DEVTYPE";

    /**
     * Extra in the ACTION_RDSTMC_STATE intent. The value is the device node name.
     */
    private static final String EXTRA_RDSTMC_DEVNAME = "tomtom.platform.hardware.EXTRA_RDSTMC_DEVNAME";

    private static final int STATE_UNDEFINED = -321; // Magic number to detect absense of state extra
    private static final int STATE_DISCONNECTED = 0; // The RDS-TMC receiver is not connected.
    private static final int STATE_WRONG_PORT = 1;   // The RDS-TMC receiver is connected to the wrong port.
    private static final int STATE_CONNECTED = 2;    // The RDS-TMC receiver is connected (to the correct port).

    private static final int DEVTYPE_NONE = 0;        // RDS-TMC receiver type: None
    private static final int DEVTYPE_POZNAN_LITE = 1; // RDS-TMC receiver type: Poznan Lite
    private static final int DEVTYPE_POZNAN = 2;      // RDS-TMC receiver type: Poznan

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received " + intent.toString());

        if (intent.getAction().equals(ACTION_START_TMC)) {
            Log.d(TAG, "start RdsTmcService");
            context.startService(new Intent(context, RdsTmcService.class));
        }
        else if (intent.getAction().equals(ACTION_STOP_TMC)) {
            Log.d(TAG, "stop RdsTmcService");
            context.stopService(new Intent(context, RdsTmcService.class));
        }
        else if (intent.getAction().equals(ACTION_CONNECT_TMC)) {
            Log.d(TAG, "broadcast receiver event active " + true);
            Intent broadcastIntent = new Intent(ACTION_RDSTMC_RECEIVER_EVENT);
            broadcastIntent.putExtra(EXTRA_RDSTMC_RECEIVER_ACTIVE, true);
            context.sendStickyBroadcast(broadcastIntent); // RDSTMC_RECEIVER_EVENT send sticky to RDSTMCServiceProxy

            // tell the user we connected
            Toast.makeText(context, "TMC traffic connecting...", Toast.LENGTH_SHORT).show();
        }
        else if (intent.getAction().equals(ACTION_DISCONNECT_TMC)) {
            Log.d(TAG, "broadcast receiver event active " + false);
            Intent broadcastIntent = new Intent(ACTION_RDSTMC_RECEIVER_EVENT);
            broadcastIntent.putExtra(EXTRA_RDSTMC_RECEIVER_ACTIVE, false);
            context.sendStickyBroadcast(broadcastIntent); // RDSTMC_RECEIVER_EVENT send sticky to RDSTMCServiceProxy

            // tell the user we disconnected
            Toast.makeText(context, "TMC traffic disconnecting...", Toast.LENGTH_SHORT).show();
        }
        else if (intent.getAction().equals(ACTION_POZNANLITE_STATE) ||
                 intent.getAction().equals(ACTION_RDSTMC_STATE)) {
            int state = STATE_UNDEFINED;
            int devtype = DEVTYPE_NONE;
            String devname = "";
            if (intent.getAction().equals(ACTION_POZNANLITE_STATE))  {
                state = intent.getIntExtra(EXTRA_POZNANLITE_STATE, STATE_UNDEFINED);
                Log.d(TAG, "Poznan-Lite state extra: " + stateToString(state));
            } else if (intent.getAction().equals(ACTION_RDSTMC_STATE)) {
                state = intent.getIntExtra(EXTRA_RDSTMC_STATE, STATE_UNDEFINED);
                devtype = intent.getIntExtra(EXTRA_RDSTMC_DEVTYPE, DEVTYPE_NONE);
                devname = intent.getStringExtra(EXTRA_RDSTMC_DEVNAME);
                Log.d(TAG, "TMC receiver extras: state " + stateToString(state) + ", devtype " + devTypeToString(devtype) + ", devname " + devname);
            }
            
            // do not broadcast in case state is other than disconnected or connected
            if ((state == STATE_DISCONNECTED) || (state == STATE_CONNECTED))
            {
                boolean active = (state == STATE_CONNECTED);
                Log.d(TAG, "broadcast TMC receiver event active " + active);
                Intent broadcastIntent = new Intent(ACTION_RDSTMC_RECEIVER_EVENT);
                broadcastIntent.putExtra(EXTRA_RDSTMC_RECEIVER_ACTIVE, active);
                context.sendStickyBroadcast(broadcastIntent); // RDSTMC_RECEIVER_EVENT send sticky to RDSTMCServiceProxy
            }

            String tmcReceiverState = "";

            // tell the user about TMC receiver state change
            switch (state) {
                case STATE_DISCONNECTED:
                    tmcReceiverState = "TMC receiver disconnected";
                    break;
                case STATE_WRONG_PORT:
                    tmcReceiverState = "!!! TMC receiver connected to wrong port !!!";
                    break;
                case STATE_CONNECTED:
                    tmcReceiverState = "TMC receiver connected";
                    break;
                default:
                    break;
            }

            // tell the user the TMC receiver state
            Log.d(TAG, "TMC receiver state: " + tmcReceiverState);
            if (!tmcReceiverState.equals("")) {
                final File commonInstalled = new File("/data/ttcontent/common/installed");
                if (!commonInstalled.isDirectory()) {
                    Toast.makeText(context, tmcReceiverState, Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (intent.getAction().equals(ACTION_FACTORY_RESET_TMC)) {
            // delete all files (and directories) in the files dir
            deleteFiles(context.getFilesDir().getAbsolutePath());

            // delete both example.rep
            deleteFile("/data/ttcontent/common/installed/example.rep");
            deleteFile("/mnt/sdcard/ttndata/files/example.rep");

            // rename both test_team_tmc.log
            renameFile("/data/ttcontent/common/installed/logging/test_team_tmc.log");
            renameFile("/mnt/sdcard/ttndata/files/logging/test_team_tmc.log");
        }
        else {
            Log.e(TAG, "Intent: " + intent.toString() + " was not caught");
        }
    }

    public void deleteFiles(String path) {
        Log.d(TAG, "deleteFiles: " + path);
        final File[] files = new File(path).listFiles();
        if (files == null) {
            Log.e(TAG, "deleteFiles: File list is null");
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // recursion into directory
                deleteFiles(file.getPath());
            }
            if (file.delete()) {
              Log.d(TAG, "deleteFiles: " + file.getName());
            } else {
              Log.e(TAG, "deleteFiles: Failed to delete " + file.getName());
            }
        }
        Log.d(TAG, "deleteFiles: " + files.length + " files");
    }

    public void deleteFile(String filename) {
        final File file = new File(filename);
        if (file.exists()) {
            if (file.delete()) {
              Log.d(TAG, "deleteFile: " + file.getPath());
            } else {
              Log.e(TAG, "deleteFile: Failed to delete " + file.getPath());
            }
        }
    }

    public void renameFile(String filename) {
        final File file = new File(filename);
        if (file.exists()) {
            String format = (new SimpleDateFormat("yyyyMMddhhmmss")).format(new Date());

            final File fileNew = new File(filename + "." + format);
            if (file.renameTo(fileNew)) {
              Log.d(TAG, "renameFile: " + file.getPath() + " -> " + fileNew.getName());
            } else {
              Log.e(TAG, "renameFile: Failed to rename " + file.getPath());
            }
        }
    }

    private String stateToString(int state) {
        String stateString = "Default";
        switch (state) {
            case STATE_UNDEFINED:
                stateString = "Undefined";
                break;
            case STATE_DISCONNECTED:
                stateString = "Disconnected";
                break;
            case STATE_WRONG_PORT:
                stateString = "Wrong Port";
                break;
            case DEVTYPE_POZNAN:
                stateString = "Connected";
                break;
            default:
                stateString = "Unknown";
                break;
        }
        return stateString + "(" + state + ")";
    }

    private String devTypeToString(int devtype) {
        String devTypeString = "Default";
        switch (devtype) {
            case DEVTYPE_NONE:
                devTypeString = "None";
                break;
            case DEVTYPE_POZNAN_LITE:
                devTypeString = "Poznan-Lite";
                break;
            case DEVTYPE_POZNAN:
                devTypeString = "Poznan";
                break;
            default:
                devTypeString = "Unknown";
                break;
        }
        return devTypeString + "(" + devtype + ")";
    }
}
