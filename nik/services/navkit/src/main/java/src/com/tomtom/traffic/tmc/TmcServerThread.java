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
import android.content.Context;
import android.os.Process;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import java.io.File;

public class TmcServerThread extends Thread {
    private static final String TAG = TmcServerThread.class.getSimpleName();
    
    private static final long mMillis = 20;

    private NativeTmcServer mNativeTmcServer = new NativeTmcServer();
        
    private volatile boolean mRunning = true;
    private String mTmcFilePath;
    
    private Context mContext = null;
    
    private Activity mActivity = new Activity();

    private Toast mToast;

    public TmcServerThread(Context aContext) {
        this.setName(TAG);
        mContext = aContext;
    }

    public synchronized void setTmcFilePath(String aPath) {
        Log.d(TAG, "setTmcFilePath " + aPath);
        mTmcFilePath = aPath;
    }

    public void run() {
        Log.d(TAG, "run");
        mNativeTmcServer.open(mTmcFilePath);
        Log.d(TAG, "wait(" + mMillis + ")");
        Log.d(TAG, "thread priority (default=0) " + Process.getThreadPriority(Process.myTid()));
        int loopCount = -1;
        while (mRunning) {
            try {
                synchronized(this) {
                    if ((loopCount >= 0) && ((loopCount++) % (1000/mMillis) == 0)) {
                        Log.d(TAG, "running with wait " + mMillis + " ms");
                    }
                    wait(mMillis);
                    if (!mNativeTmcServer.process()) {
                        Log.e(TAG, "native process call failed, quit running");
                        mRunning = false;
                    }
                    showDiagInfo();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mNativeTmcServer.close();
        Log.d(TAG, "run() Finished");
    }

    public void halt() {
        Log.d(TAG, "halt");
        mRunning = false;
    }

    private void showDiagInfo() {
        final String diagInfo = mNativeTmcServer.getDiagInfo();

        if ((diagInfo == null) ||
            (diagInfo.length() == 0)) {
            // do not show empty line
            return;
        }

        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    mToast.getView().isShown(); // true if visible
                    mToast.setText(diagInfo); // set the new text
                    //Log.d(TAG, "setText[" + diagInfo.length() + "] \"" + diagInfo + "\"");
                } catch (Exception e) {
                    // invisible if exception
                    mToast = Toast.makeText(mContext, diagInfo, Toast.LENGTH_LONG); // 3500 ms
                    //Log.d(TAG, "makeText[" + diagInfo.length() + "] \"" + diagInfo + "\"");
                    mToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                mToast.show();
            }
        });
    }
}
