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
        public int setTimeZoneOffset(int timeZoneOffset, String timeZoneId) {
            Log.i(TAG, "setTimeZoneOffset - Got timeZoneOffset ( " + timeZoneOffset +
                    " ) timeZoneId ( " + timeZoneId + " )" );
            return Constants.SERVICE_SEND_SUCCESS;
        }

        @Override
        public int setDayLightSavingsOffset(int daylightSavingsOffset) {
            Log.i(TAG, "setDayLightSavingOffset - Got data dstOffset ( " + daylightSavingsOffset +
                    " )" );
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
