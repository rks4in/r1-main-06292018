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

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.tomtom.logcollector.LogCollectorService;
import com.tomtom.crashhandlerjni.CrashHandlerJni;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class NavKitLifeline extends Service {
    private static final String sLifeLineConfigForeground   = "FOREGROUND";
    private static final String sLifeLineConfigTitle        = "TITLE";
    private static final String sLifeLineConfigSubTitle     = "SUBTITLE";
    private static final String sLifeLineNotification       = ".lifelinenotification";

    /**
     * Specify crash location
     */
    private static final String CRASH_SUBDIR                = "navkit_tombstones";
    private static final String CRASH_FILENAME_PREFIX       = "ttcrashlog";

    private static final String NAVKIT_CLASS_NAME           = "com.tomtom.navkit.NavKit";
    private static final String POSITIONING_CLASS_NAME      = "com.tomtom.positioning.Service";

    private static final String STOP_COMMAND                = "com.tomtom.navkit.NavKitLifeline.STOP";
    private static final String NAVKIT_STOP_ACTION          = "com.tomtom.navkit.NavKit.STOP";

    private static final String SERVICECONNECTORS_CONTEXT   = "com.tomtom.extension.services";
    private static final String SERVICECONNECTORS_CLASS     = "com.tomtom.extension.services.ConnectorHost";

    private CharSequence mTickerText = "TTN service"; // ticker-text
    private CharSequence mContentTitle = "TTN service"; // message title
    private CharSequence mContentText = "TomTom"; // message text
    private boolean mRunAsForwardService = true;

    public final static String TAG = NavKitLifeline.class.getSimpleName();

    
    boolean mIsNavKitBound = false;
    boolean mIsPositioningBound = false;
    boolean mIsLogCollectorRunning = false;
    boolean mIsServiceConnectorsBound = false;

    private NavKitPermissionUtil mNavKitPermissionUtil = null; 
    
    private ServiceConnection mConnectionNavKit = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.v(TAG, " onServiceConnected(NavKit)");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected - process crashed.
            Log.v(TAG, " onServiceDisconnected(NavKit): crash?");
        }
    };

    private ServiceConnection mConnectionPositioning = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.v(TAG, " onServiceConnected(Positioning)");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected - process crashed.
            Log.v(TAG, " onServiceDisconnected(Positioning): crash?");
        }
    };

    private ServiceConnection mConnectionServicesConnector = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.v(TAG, " onServiceConnected(ServicesConnector)");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected - process crashed.
            Log.v(TAG, " onServiceDisconnected(ServicesConnector): crash?");
        }
    };

    private BroadcastReceiver mStopIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "onReceive(" + intent + "): Stopping");
            Log.v(TAG, "Broadcast '" + NAVKIT_STOP_ACTION + "' ensuring NavKit saves its configuration");
            context.sendBroadcast(new Intent(NAVKIT_STOP_ACTION));
            stopNavKitServiceGroup();
            stopSelf();
            Log.v(TAG, "onReceive(" + intent + "): Call to stopSelf() completed");
        }
    };

    public NavKitLifeline() {
        Log.v(TAG, " ========================= NavKitLifeline =======================");
    }

    private class RemoteBinder extends NavKitLifelineRemoteBinder.Stub {
        private List<String> mNonGrantedDangerousPermissions = null;
        
        public RemoteBinder(List<String> nonGrantedDangerousPermissions) {
            super();
            mNonGrantedDangerousPermissions = nonGrantedDangerousPermissions;
        }
        
        @Override
        public List<String> getNonGrantedDangerousPermissions() throws android.os.RemoteException {
            return mNonGrantedDangerousPermissions;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, " onBind(Intent): Started");
        List<String> nonGrantedDangerousPermissions = mNavKitPermissionUtil.getNonGrantedDangerousPermissions();
        if (nonGrantedDangerousPermissions.isEmpty()) {
            setLifeLineConfiguration(intent);
            startNavKitServiceGroup(intent);
        }
        Log.v(TAG, " onBind(Intent): Done");
        return new RemoteBinder(nonGrantedDangerousPermissions);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, " onUnbind(Intent): Done");
        return false;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, " onCreate: Register stop intent receiver");
        registerReceiver(mStopIntentReceiver, new IntentFilter(STOP_COMMAND));
        mNavKitPermissionUtil = new NavKitPermissionUtil(getApplicationContext()); 
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, " onStartCommand: Started");
        List<String> nonGrantedDangerousPermissions = mNavKitPermissionUtil.getNonGrantedDangerousPermissions();
        int startMode = START_STICKY;
        if (nonGrantedDangerousPermissions.isEmpty()) {
            setLifeLineConfiguration(intent);
            startNavKitServiceGroup(intent);
            Log.v(TAG, " onStartCommand: Done");
        } else {
            startMode = START_NOT_STICKY;
            for (String permissionMissing : nonGrantedDangerousPermissions) {
                Log.e(TAG, "Missing permission: " + permissionMissing);
            }
            Log.e(TAG, " onStartCommand: Call stopSelf() due to permission lacking");
            stopSelf();
        }
        return startMode;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, " onDestroy: Started");
        stopNavKitServiceGroup();
        unregisterReceiver(mStopIntentReceiver);
        mNavKitPermissionUtil = null;
        Log.v(TAG, " onDestroy: Done");
    }

    private void setLifeLineConfiguration(Intent intent)
    {
      try{
        if(intent.hasExtra(sLifeLineConfigForeground)){
            mRunAsForwardService = intent.getBooleanExtra(sLifeLineConfigForeground, mRunAsForwardService);
            if (!mRunAsForwardService) {
                Log.d(TAG, "running Navkit as a Background service: ");
            }
        }

        if(intent.hasExtra(sLifeLineConfigTitle)){
                mContentTitle = intent.getStringExtra (sLifeLineConfigTitle);
                mTickerText = mContentTitle;
                Log.d(TAG, "Setting title and ticker to be: "+ mContentTitle);
        }

        if(intent.hasExtra(sLifeLineConfigSubTitle)){
            mContentText = intent.getStringExtra(sLifeLineConfigSubTitle);
            Log.d(TAG, "Setting content text to be: "+ mContentText);
        }
      }catch(Exception e)
      {
          Log.e(TAG, "Error trying to process Navkit Lifeline Intent extras.");
      }
    }

    private void configureLifeLineService() {
        if (!mRunAsForwardService) {
            Log.v(TAG, "Navkit LifeLine is configured to run as a background service.");
            return;
        }

        Log.v(TAG, "Starting NavKit LifeLine as a foreground service...");
        Context context = getApplicationContext();
        String actionDescription = context.getPackageName() + sLifeLineNotification;
        Log.v(TAG, " =====| Notification string is:" + actionDescription);

        Intent notificationIntent = new Intent();
        notificationIntent.setAction(actionDescription);

        Notification notification = new Notification.Builder(this)
            .setSmallIcon(R.drawable.icon)
            .setTicker(mTickerText)
            .setContentTitle(mContentTitle)
            .setContentText(mContentText)
            .setContentIntent(PendingIntent.getBroadcast(this, 0, notificationIntent, 0))
            .getNotification();
        startForeground(1, notification);
    }

    private boolean serviceIsAvailable(Intent intent)
    {
      final PackageManager packageManager = this.getPackageManager();
      final List<ResolveInfo> list = packageManager.queryIntentServices(intent, 0);
      return list == null ? false : list.size() > 0;
    }

    private Intent getAvailableIntent(String packageName, String className)
    {
      Intent intent = new Intent();
      intent.setClassName(packageName, className);

      return serviceIsAvailable(intent) ? intent : null;
    }

    private Intent getServicesConnectorIntent()
    {
      Intent connectorIntent;

      // First try to start local package ServicesConnector service
      connectorIntent = getAvailableIntent(this.getPackageName(), SERVICECONNECTORS_CLASS);

      // If not found, use standard package name
      if (connectorIntent == null) {
        connectorIntent = getAvailableIntent(SERVICECONNECTORS_CONTEXT, SERVICECONNECTORS_CLASS);
      }

      return connectorIntent;
    }
    
    private void startNavKitServiceGroup(Intent intent)
    {
      try{
        configureLifeLineService();

        // Set the CrashHandler for NavKit and PositioningEngine
        String crashPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + CRASH_SUBDIR;
        CrashHandlerJni.install(crashPath, CRASH_FILENAME_PREFIX);

        Intent navkitIntent = new Intent(intent);
        navkitIntent.setClassName(this.getPackageName(), NAVKIT_CLASS_NAME);

        Intent positioningIntent = new Intent(this, Class.forName(POSITIONING_CLASS_NAME));

        mIsNavKitBound       = bindService(navkitIntent, mConnectionNavKit, Context.BIND_AUTO_CREATE | Context.BIND_NOT_FOREGROUND);
        mIsPositioningBound  = bindService(positioningIntent, mConnectionPositioning, Context.BIND_AUTO_CREATE | Context.BIND_NOT_FOREGROUND);
        Log.v(TAG, "bindService(Navkit+Positioning) --> " + mIsNavKitBound + ", " + mIsPositioningBound);

        Context context = getApplicationContext(); // application Context
        final Intent logCollectorIntent = new Intent(context, LogCollectorService.class);
        logCollectorIntent.putExtra(LogCollectorService.CONFIG_CRASH_PATH, crashPath);
        if (startService(logCollectorIntent) != null) {
            mIsLogCollectorRunning = true;
        }
        Log.v(TAG, "startService(LogCollector) --> " + mIsLogCollectorRunning);

        Intent serviceConnectorsIntent = getServicesConnectorIntent();

        if (serviceConnectorsIntent != null) {
          mIsServiceConnectorsBound = bindService(serviceConnectorsIntent, mConnectionServicesConnector, Context.BIND_AUTO_CREATE | Context.BIND_NOT_FOREGROUND);
          Log.v(TAG, "bindService(ServiceConnectors) --> " + mIsServiceConnectorsBound);
        }
        else {
          Log.v(TAG, "ServiceConnectors not available");
        }
      }catch(ClassNotFoundException e)
      {
          Log.e(TAG, "Error trying to bind to service: " + e.getMessage());
      }
    }

    private void stopNavKitServiceGroup() {
        Log.v(TAG, " stopNavKitServiceGroup: unbind/stopService(Navkit+LogCollector+Positioning) if bound/running");

        if (mIsNavKitBound) {
            Log.v(TAG, " stopNavKitServiceGroup: unbindService(Navkit)");
            unbindService(mConnectionNavKit);
            mIsNavKitBound = false;
        }

        if (mIsLogCollectorRunning) {
            Log.v(TAG, " stopNavKitServiceGroup: StopService(LogCollector)");
            Context context = getApplicationContext(); // application Context
            final Intent logCollectorIntent = new Intent(context, LogCollectorService.class);
            stopService(logCollectorIntent);
            mIsLogCollectorRunning = false;
        }

        if (mIsPositioningBound) {
            Log.v(TAG, " stopNavKitServiceGroup: unbindService(Positioning)");
            unbindService(mConnectionPositioning);
            mIsPositioningBound = false;
        }

        if (mIsServiceConnectorsBound) {
            Log.v(TAG, " stopNavKitServiceGroup: unbindService(ServiceConnectors)");
            unbindService(mConnectionServicesConnector);
            mIsServiceConnectorsBound = false;
        }
    }
}
