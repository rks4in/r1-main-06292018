package com.harman.fcaclock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class FcaClockService extends Service {
    private static final String TAG = "FcaClockService";
    private final IFcaClockService.Stub mBinder = new IFcaClockService.Stub() {

        @Override
        public int setTimeZone(String timeZone, int dstOffsetMilli) {
            Log.i(TAG, "setTimeZone - Got data ( " + timeZone +
                    " ) dstOffset ( " + dstOffsetMilli + " )" );
            return Constants.SERVICE_SEND_SUCCESS;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service onBind called");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service onCreate called");
        super.onCreate();
    }
}
