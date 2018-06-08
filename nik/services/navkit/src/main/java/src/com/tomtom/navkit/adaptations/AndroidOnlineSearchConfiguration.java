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
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.tomtom.navkit.R;

public class AndroidOnlineSearchConfiguration {
    @NonNull
    private final Context mContext;

    public AndroidOnlineSearchConfiguration(@NonNull final Context context) {
        mContext = context;
    }

    public String getApiKey() {
        final String apiKey = mContext.getString(R.string.tomtom_online_services_key);
        if (TextUtils.isEmpty(apiKey)) {
            throw new IllegalStateException("API key has to be set by defining "
                    + mContext.getResources().getResourceEntryName(R.string.tomtom_online_services_key) + " string resource.");
        }
        return apiKey;
    }
}
