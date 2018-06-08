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

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Environment;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.os.Build;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.net.Uri;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.IllegalStateException;
import java.lang.System;
import com.tomtom.crashhandlerjni.CrashHandlerJni;
import com.tomtom.navkit.NavKitShutdownReceiver;
import com.tomtom.navkit.NavKitShutdownReceiver.PowerOffObserver; 
import com.tomtom.navkit.adaptations.AndroidProperties;
import com.tomtom.navkit.NavKitContextStore;

public class NavKit extends Service implements PowerOffObserver 
{
  static final String TAG = "NavKitService";
  private final Binder navkitBinder = new Binder();
  /**
   * Specify crash location
   */
  private static final String CRASH_SUBDIR          = "navkit_tombstones";
  private static final String CRASH_FILENAME_PREFIX = "ttcrashdmp";
  private static final String BASE_ADDRESS_KEY      = "BASE_ADDRESS";

  private static final String STOP_COMMAND          = "com.tomtom.navkit.NavKit.STOP";
  
  private BroadcastReceiver mStopIntentReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
          Log.v(TAG, "onReceive(" + intent + "): Stopping");
          stopNavKitRunner();
          stopSelf();   
      }
  };

  private class NavKitRunnerThread extends Thread 
  {
    static final String TAG = "NavKitRunnerThread";
    private ContentExtractor mContentExtractor;
    private Context          mContext;

    class Locations 
    {
      private String workingDirectory;
      private String privateContentDirectory;
      private String sharedContentDirectory;
      private String persistentDirectory;

      public Locations(String workingDirectory, String privateContentDirectory, String sharedContentDirectory, String persistentDirectory) 
      {
        this.workingDirectory = workingDirectory;
        this.privateContentDirectory = privateContentDirectory;
        this.sharedContentDirectory = sharedContentDirectory;
        this.persistentDirectory = persistentDirectory;
      }

      public String getWorkingDirectory() 
      {
        return workingDirectory;
      }

      public String getPrivateContentDirectory() 
      {
        return privateContentDirectory;
      }
      
      public String getSharedContentDirectory() 
      {
        return sharedContentDirectory;
      }
      
      public String getPersistentDirectory() 
      {
        return persistentDirectory;
      }
    }

    public NavKitRunnerThread(ContentExtractor ce,Context context)
    {
      mContext = context;
      mContentExtractor = ce;
    }

    private Locations getTargetLocations() {
        
        AndroidProperties androidProperties = new AndroidProperties(mContext);  
        return new Locations(androidProperties.getWorkingDirectory(), 
                             androidProperties.getPrivateContentDirectory(), 
                             androidProperties.getSharedContentDirectory(), 
                             androidProperties.getPersistentDirectory());
    }

    private void closeReader(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                Log.e(TAG, "closeReader: close() failed");
            }
        }
    }

    private void excludeDirectoryFromScanning(String aDirectory)
    {
      File file = new File(aDirectory, ".nomedia");
      try
      {
        Log.d(TAG, "Creating file \"" + file.getPath() + "\" to prevent the android media scanner from scanning...");
        boolean isFileCreated = file.createNewFile();
        if (isFileCreated)
        {
          Log.d(TAG, "File \"" + file.getPath() + "\" successfully created.");
        }
        else
        {
          Log.d(TAG, "File \"" + file.getPath() + "\" already exists.");
        }
      }
      catch (IOException e)
      {
        Log.d(TAG, "I/O error creating file \"" + file.getPath() + "\": " + e.getMessage());
      }
      catch (SecurityException e)
      {
        Log.d(TAG, "Security error creating file \"" + file.getPath() + "\": " + e.getMessage());
      }
    }

    public void navKitThreadStart() 
    {
      if (synchronizedNavKitCreate())
      {
        synchronizedNavKitStart();
        start(); // start Java-thread
      }
    }
  
    public boolean navKitThreadIsRunning() 
    {     	
      return isAlive();
    }
  
    public void navKitThreadStop() 
    {
      synchronizedNavKitStop();

      try 
      {
        // Note: This join operation joins with the Java thread.
        // When this method returns, the java thread is marked as finished,
        // even though the underlying pthread is still busy.
        // This can cause issues when destroying NavKit before this pthread
        // (in which NavKit ran) is properly cleaned up.
        join(); // stop Java-thread
      }
      catch (InterruptedException e) 
      {
        Log.w(TAG, "navKitStop: Thread interrupted");
      }

      synchronizedNavKitDestroy();
    }

    @Override
    public void run() 
    {
      Locations locations = getTargetLocations();

      // Exclude all directories from scanning
      excludeDirectoryFromScanning(locations.getWorkingDirectory());
      excludeDirectoryFromScanning(locations.getPrivateContentDirectory());
      excludeDirectoryFromScanning(locations.getSharedContentDirectory());
      excludeDirectoryFromScanning(locations.getPersistentDirectory());
      
      mContentExtractor.setWorkingDirectory(locations.getWorkingDirectory());
      mContentExtractor.setSharedDirectory(locations.getSharedContentDirectory());

      // Do content extraction in the thread before starting NavKit
      Log.v(TAG, "run(): Extract content started in navkit thread");
      if (mContentExtractor.extractUsingZip()) {
        Log.v(TAG, "run(): Extract content done");
        if (!getNavKitHasToStop()) {
            navKitStart(getNavKitRunnerPtr());
        }
      }
      else {
        Log.e(TAG, "run(): Extract content failed");
      }
    }

    private String getNavKitLibPath() {
        final String libNavKitName = System.mapLibraryName("NavKit");
        final String[] searchPaths = new String[] {
            getApplicationInfo().dataDir + "/lib/" + libNavKitName,
            getApplicationInfo().nativeLibraryDir + "/" + libNavKitName,
            "/system/lib/" + libNavKitName,
            "/system/lib64/" + libNavKitName
        };

        for (final String path : searchPaths) {
            Log.v(TAG, "Checking .so path: " + path);
            if (new File(path).exists()) {
                return path;
            }
        }

        throw new IllegalStateException("NavKit library '" + libNavKitName + "' not found");
    }

    private synchronized boolean synchronizedNavKitCreate() {
        final String libNavKitPath = getNavKitLibPath();
        Log.v(TAG, "navKitCreate: Path to libNavKit.so = '" + libNavKitPath + "'");

        System.loadLibrary("gnustl_shared");

        Log.v(TAG, " ========================= Loading NavKit ============================");
        navKitRunnerPtr = navKitCreate(libNavKitPath);
        boolean isNavKitCreated = (navKitRunnerPtr != 0);

        if (isNavKitCreated) {
            Log.v(TAG, "navKitCreate: NavKit created");
        } else {
            Log.e(TAG, "navKitCreate: NavKit creation failed");
        }

        return isNavKitCreated;
    }

    private synchronized void synchronizedNavKitStart()
    {
      navKitHasToStop = false;
    }
    
    private synchronized boolean synchronizedNavKitIsRunning() 
    {
      return ((navKitRunnerPtr != 0) && navKitIsRunning(navKitRunnerPtr));
    }
  
    private synchronized void synchronizedNavKitStop() 
    {
      navKitHasToStop = true;

      if (navKitRunnerPtr != 0) 
      {
        Log.v(TAG, "navKitStop: stop NavKit");
        navKitStop(navKitRunnerPtr);
        Log.v(TAG, "navKitStop: NavKit stopped");
      } 
      else 
      {
        Log.v(TAG, "navKitStop: NavKit not started");
      }
    }
  
    private synchronized void synchronizedNavKitDestroy() 
    {
      if (navKitRunnerPtr != 0) 
      {
        navKitDestroy(navKitRunnerPtr);
        navKitRunnerPtr = 0;
        Log.v(TAG, "navKitDestroy: NavKit destroyed");
      } 
      else 
      {
        Log.v(TAG, "navKitDestroy: NavKit not created");
      }
    }

    private synchronized long getNavKitRunnerPtr() 
    {
      return navKitRunnerPtr;
    }
    
    private synchronized boolean getNavKitHasToStop()
    {
      return navKitHasToStop;
    }
    
    private boolean navKitHasToStop = false;
  }

  //@Override
  public void onPowerOff() {
    Log.i(TAG, "onPowerOff: Terminate command received.");

    if (!instanceCreated)
    {
      Log.e(TAG, "onPowerOff: Instance of the service has not been created. Skipping request!");
      return;
    }

    stopNavKitRunner();
    stopSelf();   
  }

  @Override
  public IBinder onBind(Intent intent) 
  {
    Log.v(TAG, "NavKit: onBind(Intent): Started");

    if (!instanceCreated)
    {
      Log.e(TAG, "onBind: Instance of the service has not been created. Skipping request!");
    }
    else if (isNavKitThreadRunning())
    {
      Log.v(TAG, "onBind: Service already running, skipping request!");
    }
    else
    {
      init();
      setAndroidProperties(intent);
      startNavKitRunner();
    }

    Log.v(TAG, "NavKit: onBind(Intent): Done");
    return navkitBinder;
  }
  
  @Override
  public boolean onUnbind(Intent intent) 
  {
    Log.v(TAG, "NavKit: onUnbind(Intent): Started");
    Log.v(TAG, "NavKit: onUnbind(Intent): Done");
    return false;
  }

  @Override
  public void onCreate() 
  {
    Log.v(TAG, "onCreate: Started");

    // This should never happen! Android OS should never create a new instance of service if the service is already
    // up and running. But in practice this happens on Parrot platform with unsupported version of Android 2.3.7
    // (SEM project) and theoretically can happen on other platforms. Therefore NavKit should be defensive to these
    // kind of issues.
    if (isNavKitThreadRunning())
    {
      Log.e(TAG, "onCreate: The service is already running! No need to create a new instance of the service!");
      return;
    }

    // TEMPORARY: Set the CrashHandler for NavKit as long as NavKitLifeline is not generally used.
    String crashPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + CRASH_SUBDIR;
    CrashHandlerJni.install(crashPath, CRASH_FILENAME_PREFIX);

    Log.v(TAG, " ========================= Loading Icu4c ========");
    System.loadLibrary("Icu4c");

    Log.v(TAG, " ========================= Loading NavKitJniBind =====================");
    System.loadLibrary("NavKitJniBind");

    shutDownReceiver = NavKitShutdownReceiver.createNavKitShutdownReceiver(this,this);
    registerReceiver(mStopIntentReceiver, new IntentFilter(STOP_COMMAND));
    printAndroidInfo();
    instanceCreated = true;

    Log.v(TAG, "onCreate: Done");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) 
  {
    Log.v(TAG, "onStartCommand: Started");

    if (!instanceCreated)
    {
      Log.e(TAG, "onStartCommand: Instance of the service has not been created. Skipping request!");
    }
    else if (isNavKitThreadRunning())
    {
      Log.v(TAG, "onStartCommand: Service already running, skipping request!");
    }
    else
    {
      init();
      setAndroidProperties(intent);
      startNavKitRunner();
    }

    Log.v(TAG, "onStartCommand: Done");
    return START_STICKY;
  }

  @Override
  public void onDestroy()
  {    
    Log.v(TAG, "OnDestroy: Started");

    if (!instanceCreated)
    {
      Log.e(TAG, "OnDestroy: Instance of the service has not been created. Skipping request!");
      return;
    }

    unregisterReceiver(mStopIntentReceiver);
    shutDownReceiver.stop(this);      
    stopNavKitRunner();
    instanceCreated = false;

    Log.v(TAG, "OnDestroy: Done");
  }

  private void init() 
  {
    stopNavKitRunner();
    
    String packageName = getPackageName();
    try 
    {
      PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);

      String versionCodeText = "Version CL: " + packageInfo.versionCode;
      String versionNameText = "Version number: " + packageInfo.versionName;

      Log.v(TAG, versionNameText);
      Log.v(TAG, versionCodeText);
    }
    catch (PackageManager.NameNotFoundException e)
    {
    }

    NavKitContextStore.setContext(this);
  }
  
  private void startNavKitRunner() 
  {
    Log.v(TAG, "startNavKitRunner: start NavKit");
    try 
    {
      ContentExtractor ce = new ContentExtractor(this);
      navKitRunnerThread = new NavKitRunnerThread(ce,this);
      navKitRunnerThread.navKitThreadStart();
      Log.v(TAG, "startNavKitRunner: NavKit started");
    } 
    catch (Exception e) 
    {
      Log.e(TAG, "startNavKitRunner: starting NavKit failed", e);
    }
  }
  
  private boolean isNavKitThreadRunning()
  {
    return ((navKitRunnerThread != null) && navKitRunnerThread.navKitThreadIsRunning());
  }
  
  private void stopNavKitRunner()
  {
    Log.v(TAG, "stopNavKitRunner(): started");

    if (navKitRunnerThread != null)
    {
      Log.v(TAG, "stopNavKitRunner(): Stopping NavKit ...");
      navKitRunnerThread.navKitThreadStop();
      navKitRunnerThread = null;
      Log.v(TAG, "stopNavKitRunner(): NavKit stopped");
    } 
    else 
    {
      Log.v(TAG, "stopNavKitRunner(): NavKit not started");
    }
    // Reset Android properties
    setAndroidProperties(null);

    Log.v(TAG, "stopNavKitRunner(): done");
  }

  private void setAndroidProperties(Intent navkitIntent)
  {
    if (navkitIntent != null && navkitIntent.hasExtra(BASE_ADDRESS_KEY)) {
      String reflectionBaseAddress = navkitIntent.getStringExtra(BASE_ADDRESS_KEY);
      AndroidProperties.setReflectionBaseAddress(reflectionBaseAddress);
    }
    else {
        AndroidProperties.setReflectionBaseAddress(null);
    }
  }
  /*! Prints OS info to log. */
  private void printAndroidInfo()
  {
    Log.i(TAG, " -- Android version: " + Build.VERSION.RELEASE);
  }

  private NavKitShutdownReceiver shutDownReceiver = null;
  private static NavKitRunnerThread navKitRunnerThread = null;
  private boolean instanceCreated = false;

  private long navKitRunnerPtr = 0;

  static native long navKitCreate(String libNavKitPath);

  static native boolean navKitStart(long navKitRunnerPtr);

  static native boolean navKitIsRunning(long navKitRunnerPtr);

  static native void navKitStop(long navKitRunnerPtr);

  static native void navKitDestroy(long navKitRunnerPtr);
}
