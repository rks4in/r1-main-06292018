package com.tomtom.r1navapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.tomtom.navkit.NavKitLifeline;
import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.stocksystemport.StockService;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.systemport.SystemSettings;
import com.tomtom.navui.systemport.SystemSettingsConstants;
import com.tomtom.navui.util.Log;

public class R1NavKitLifecycleManagementService extends StockService {
    /** Logging tag for debugging purposes */
    private static final String TAG = "R1NavkitLifecycleManagementService";

    private static final ComponentName EXTERNAL_NAVKIT_COMPONENT_NAME =
            new ComponentName("com.tomtom.navkit", "com.tomtom.navkit.NavKitLifeline");

    private ComponentName mNavKitLifelineComponentName;

    @Override
    public void bindService(final Intent service) {
        if (service.getAction().equalsIgnoreCase(NAVKIT_LIFELINE_SERVICE)) {
            mNavKitLifelineComponentName = new ComponentName(this, NavKitLifeline.class);

            service.setComponent(mNavKitLifelineComponentName);
        }

        super.bindService(service);
    }

    @Override
    public void unbindService(final Intent service) {
        if (service.getAction().equalsIgnoreCase(NAVKIT_LIFELINE_SERVICE)) {
            service.setComponent(mNavKitLifelineComponentName);
            mNavKitLifelineComponentName = null;
        }

        super.unbindService(service);
    }
}
