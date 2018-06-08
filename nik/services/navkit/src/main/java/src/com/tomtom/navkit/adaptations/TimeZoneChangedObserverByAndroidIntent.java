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

import android.content.Context;
import android.content.Intent;

/**
 * Notifies Android-applications when NavKit (native) detected a time-zone change
 * 
 * Note that this class depends on NavKit-Adaptations calls to interact with the native
 * Adaptation code base.
 * 
 */
public class TimeZoneChangedObserverByAndroidIntent {
    private Context context = null;
    
    public TimeZoneChangedObserverByAndroidIntent(Context context) {
        this.context = context;
    }

    // ======================================================================
    // C-to-Java callbacks
    // ======================================================================
 
    /**
     * Sends an intent to notify Android-applications that NavKit detected a time-zone change
     *
     * This is called from TimeZoneChangedObserverByAndroidIntent class in NavKit-adaptation-framework
     *
     * \param aTimeZone should have the following format: continent/city
     * complete list can be found in TZ-database-time=zones
     * (http://en.wikipedia.org/wiki/List_of_tz_database_time_zones)
     */
    public void notifyTimeZoneChanged(String aReceiver, String aIntentName, String aTimeZone) {
        if (context != null) {
            Intent intent = new Intent(aReceiver + ".action." + aIntentName);
            intent.putExtra(aReceiver + ".extra." + aIntentName, aTimeZone);
            // It is hard to determine/find if these intents have to be removed
            //   according to this internet-thread
            //   (https://groups.google.com/forum/?fromgroups=#!topic/android-developers/8341SaXhvmY) 
            //   will previously sent intents be replaced by new intents and by that do not need to be removed
            context.sendStickyBroadcast(intent);
        }
    }
}

