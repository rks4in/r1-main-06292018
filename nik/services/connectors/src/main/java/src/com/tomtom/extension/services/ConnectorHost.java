/**
 * Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used
 * for internal evaluation purposes or commercial use strictly subject to separate licensee
 * agreement between you and TomTom. If you are the licensee, you are only permitted to use
 * this Software in accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.extension.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectorHost extends Service {

    private static final String TAG = "ConnectorHost";
    private final Binder connectorBinder = new Binder();
    private ExecutorService mServiceExecutor;

    /**
     * Member used on service thread only
     */
    private boolean mConnectorStarted = false;

    static {
        System.loadLibrary("Icu4c");
        System.loadLibrary("SQLite");
        System.loadLibrary("Connector");
        System.loadLibrary("ServicesConnectorJNI");
    }

    @Override
    public void onCreate() {
        mServiceExecutor = Executors.newSingleThreadExecutor();
        mServiceExecutor.submit(mStartConnector);
    }

    // Called by the ServicesConnector
    public void callback(int aId, String aName, int aStateId, String aStateName, int aErrorCode) {
        Log.d(TAG, "ServicesConnector Status [" + aId + " " + aName + "] in [" + aStateId + " " + aStateName
                + "] (code " + aErrorCode + ")");
    }

    private final Runnable mStartConnector = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Service thread started");
            final String loggerSettings = getLoggerSettingsContent();
            final String settings = getSettingsContent();
            if (settings != null) {
                startConnector(loggerSettings, settings);
                setConnectivityState(true);
                mConnectorStarted = true;
            }
        }
    };

    private final Runnable mStopConnector = new Runnable() {
        @Override
        public void run() {
            if (mConnectorStarted) {
                mConnectorStarted = false;
                stopConnector();
                Log.d(TAG, "Service thread terminated");
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind(Intent): Done");
        return connectorBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind(Intent)");
        return false;
    }

    @Override
    public void onDestroy() {
        mServiceExecutor.submit(mStopConnector);
        mServiceExecutor.shutdown();
        mServiceExecutor = null;
    }

    protected String getLoggerSettingsContent() {
        final String file = "/mnt/sdcard/ttndata/files/ServiceConnectors/connectorLogCfg.xml";
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String loggerSettings = readEntireStream(reader);
            Log.d(TAG, "Logger settings file successfully read");
            return loggerSettings;
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "No logger settings found at '" + file + "'");
            return null;
        }
        catch (IOException e) {
            Log.e(TAG, "Error reading logger settings from '" + file + "': " + e.getMessage());
            return null;
        }
        finally { close(reader); }
    }

    protected String getSettingsContent() {
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = getAssets().open("connector.xml");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String settings = readEntireStream(reader);
            Log.d(TAG, "Settings file successfully read");
            return settings;
        }
        catch (IOException e) {
            Log.e(TAG, "Error reading settings from 'connector.xml': " + e.getMessage());
            return null;
        }
        finally {
            close(reader);
            close(inputStream);
        }
    }

    protected static String readEntireStream(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = reader.readLine();
        }

        return sb.toString();
    }

    protected static void close(Closeable aCloseable) {
        if (aCloseable != null) {
            try {
                aCloseable.close();
            }
            catch (IOException e) {
                Log.e(TAG, "Error closing settings file:" + e.getMessage());
            }
        }
    }

    /**
     * Start the Connector in its own thread.
     *
     * Creates a new thread the Connector is running in and returns
     * right after the thread has been started.
     *
     * After starting the Connector setConnectivityState(boolean) must be
     * called to enable network connections.
     *
     * @param loggerSettings Logger settings in XML format.
     * @param settings Configuration settings in XML format.
     */
    public native void startConnector(String loggerSettings, String settings);

    /**
     * Stop the Connector.
     *
     * Waits until the Connector has shut down and its thread has stopped.
     */
    public native void stopConnector();

    /**
     * Enable network connections.
     *
     * Use this method to allow the Connector to connect to other hosts
     * via network. After start this is not enabled and must be called
     * before the first connecton is made.
     *
     * @param isUp Set to true if network connectivity is available and
     * thus to allow network connections. Set to false to
     * shutdown all network connections.
     */
    public native void setConnectivityState(boolean aIsUp);
}
