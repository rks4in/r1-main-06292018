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

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RdsTmcService extends Service {
    private static final String TAG = RdsTmcService.class.getSimpleName();

    private static final String EXAMPLE_REP = "example.rep";

    /**
     * A buffer size to read and write TMC files
     */
    private static final int BUFSIZE = 1024;

    private TmcServerThread mTmcServerThread = new TmcServerThread(this);
    
    private RdsTmcIntentCatcher mRdsTmcIntentCatcher = new RdsTmcIntentCatcher();

    // A thread that handles preparation of TMC files
    private volatile Looper mTMCPreparationLooper = null;
    private volatile Handler mTMCPreparationHandler = null;

    // Main thread handler messages
    private static final int MSG_START_TMC_THREAD = 1;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        final String root = getFilesDir().getAbsolutePath();
        mTmcServerThread.setTmcFilePath(root);

        // Preparing TMC files on a separate thread
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();

        mTMCPreparationLooper = thread.getLooper();
        if (mTMCPreparationLooper != null) {
            mTMCPreparationHandler = new Handler(mTMCPreparationLooper);
        }
        kickPrepareTMCFiles(root);

        Log.d(TAG, "onCreate(): registerReceiver with dynamic IntentFilter");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RdsTmcIntentCatcher.ACTION_STOP_TMC);
        intentFilter.addAction(RdsTmcIntentCatcher.ACTION_DISCONNECT_TMC);
        registerReceiver(mRdsTmcIntentCatcher, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand(): " + ((intent != null) ? intent.toString() : "Intent: null") + ", flags " + flags + ", startId " + startId);
        super.onStartCommand(intent, flags, startId);
        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");

        // Requests to quit the thread that does IO operations
        // This is not a blocking method!
        mTMCPreparationLooper.quit();

        // Even MSG_START_TMC_THREAD messages would be removed from the handler queue here,
        // it's STILL possible that TMCPreparationLooper is still busy and post a MSG_START_TMC_THREAD.
        mMainThreadHandler.removeMessages(MSG_START_TMC_THREAD);

        stopTmcServerThread();
        
        Log.d(TAG, "onDestroy(): unregisterReceiver with dynamic IntentFilter");
        unregisterReceiver(mRdsTmcIntentCatcher);
        
        Log.d(TAG, "onDestroy(): removeStickyBroadcasts");
        removeStickyBroadcasts();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");

        return null;
    }

    private void removeStickyBroadcasts() {
        Log.d(TAG, "removeStickyBroadcasts");

        // remove RDSTMC_RECEIVER_EVENT sent sticky to RDSTMCServiceProxy
        Intent removeStickyIntent = new Intent(RdsTmcIntentCatcher.ACTION_RDSTMC_RECEIVER_EVENT);
        removeStickyIntent.putExtra(RdsTmcIntentCatcher.EXTRA_RDSTMC_RECEIVER_ACTIVE, true);
        this.removeStickyBroadcast(removeStickyIntent);
        Log.d(TAG, "removeStickyBroadcasts: removed " + removeStickyIntent.toString());
    }

    private synchronized void startTmcServerThread() {
        Log.d(TAG, "startTmcServerThread");

        // A null check has been added since it is possible mTMCPreparationLooper posted a MSG_START_TMC_THREAD
        // after stopTmcServerThread() had been called.
        if (mTmcServerThread != null) {
            mTmcServerThread.start();
        } else {
            Log.w(TAG, "cancelled startTmcServerThread, because mTmcServerThread is null");
        }

        // tell the user we started
        final File commonInstalled = new File("/data/ttcontent/common/installed");
        if (!commonInstalled.isDirectory()) {
            Toast.makeText(this, "TMC traffic started", Toast.LENGTH_LONG).show();
        }

        // tell the user we are writing test_team_tmc.log
        final File testTeamTmcLog = new File("/data/ttcontent/common/installed/logging/test_team_tmc.log");
        if (testTeamTmcLog.isFile()) {
            Toast.makeText(this, "Writing test_team_tmc.log", Toast.LENGTH_LONG).show();
        }
    }

    private synchronized void stopTmcServerThread() {
        Log.d(TAG, "stopTmcServerThread");
        mTmcServerThread.halt();
        try {
            mTmcServerThread.join();
            mTmcServerThread = null;
            Log.d(TAG, "tmcserver thread stopped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // tell the user we stopped
        final File commonInstalled = new File("/data/ttcontent/common/installed");
        if (!commonInstalled.isDirectory()) {
            Toast.makeText(this, "TMC traffic stopped", Toast.LENGTH_LONG).show();
        }
    }

    private void extractTmcFiles(String srcPath, String tmcFile, String dstPath) {
        Log.v(TAG, "extractTmcFiles: " + tmcFile);

        AssetManager mgr = getAssets();
        try {
            String[] files = mgr.list(srcPath);
            if ((srcPath.length() != 0) && !srcPath.endsWith(File.separator)) {
                srcPath = srcPath + File.separator;
            }

            if (files != null) {
                for (String filePath: files) {
                    if (filePath.equals(tmcFile)) {
                        copyFileFromAssetManager(mgr, srcPath + filePath, dstPath + filePath);
                        break;
                    } else {
                        Log.d(TAG, "extractTmcFiles: skipping " + filePath);
                    }
                }
            } else {
                Log.w(TAG, "extractTmcFiles: file " + tmcFile + " not found on " + srcPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "extractTmcFiles: done");
    }

    /**
     * Copies a file from the assets to the given destination file
     * @param mgr The AssetManager which contains asset files
     * @param sourceFilePath The full file path of the source file
     * @param destFilePath The full file path of the destination file
     */
    private void copyFileFromAssetManager(AssetManager mgr, String sourceFilePath, String destFilePath) {
        Log.v(TAG, "copyFileFromAssetManager: " + sourceFilePath + " -> " + destFilePath);

        try {
            byte data[] = new byte[BUFSIZE];

            // Buffered input stream to read a file from AssetManager
            InputStream is = mgr.open(sourceFilePath);
            BufferedInputStream bis = null;

            // Buffered output stream for writing the input stream to the destination
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(is, BUFSIZE);
                bos = new BufferedOutputStream(new FileOutputStream(new File(destFilePath)), BUFSIZE);

                // copy input to output
                int count;
                while ((count = bis.read(data)) != -1) {
                    bos.write(data, 0, count);
                }

                bos.flush();
            } finally {
                // nested finally blocks are needed here to make sure all IO streams get closed.
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } finally {
                    if (bos != null) {
                        bos.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prepares TMC files by copying them to the package folder
     * @param rootDirectory the root directory of TMC files
     */
    private void prepareTMCFiles(String rootDirectory) {
        if ((rootDirectory.length() != 0) && !rootDirectory.endsWith(File.separator)) {
            rootDirectory = rootDirectory + File.separator;
        }
        Log.d(TAG, "prepareTMCFiles: root " + rootDirectory);

        final String ttndataFilesDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ttndata" + File.separator + "files" + File.separator;
        Log.d(TAG, "prepareTMCFiles: ttndataFilesDir " + ttndataFilesDir);

        final File example = (new File(ttndataFilesDir + EXAMPLE_REP));
        if (!example.exists()) {
            extractTmcFiles("", EXAMPLE_REP, ttndataFilesDir);
        }

        Log.d(TAG, "replayMode " + example.exists());
    }

    /**
     * Runs {@link RdsTmcService#prepareTMCFiles(String)} on a separate thread.
     * @param rootDirectory the root directory of TMC files
     */
    private void kickPrepareTMCFiles(final String rootDirectory) {
        if (mTMCPreparationHandler == null) {
            return;
        }
        mTMCPreparationHandler.post(new Runnable() {

            public void run() {
                prepareTMCFiles(rootDirectory);
                mMainThreadHandler.sendEmptyMessage(MSG_START_TMC_THREAD);
            }
        });
    }

    /**
     * A handler which is responsible to execute tasks in the main thread
     */
    private final Handler mMainThreadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_START_TMC_THREAD) {
                startTmcServerThread();
            }
        }
    };
}
