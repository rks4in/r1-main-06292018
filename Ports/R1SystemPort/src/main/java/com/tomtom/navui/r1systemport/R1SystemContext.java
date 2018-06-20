package com.tomtom.navui.r1systemport;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tomtom.navui.controlport.ControlContext;
import com.tomtom.navui.r1systemport.permissions.RequestPermissionOnReadyListenerWrapper;
import com.tomtom.navui.r1systemport.systemcomponents.R1IntentHandlerSystemComponent;
import com.tomtom.navui.r1systemport.systemcomponents.R1ScreenSystemComponent;
import com.tomtom.navui.stocksystemport.StockSystemContext;
import com.tomtom.navui.stocksystemport.SystemComponentContainer;
import com.tomtom.navui.systemport.SystemService;
import com.tomtom.navui.systemport.systemcomponents.IntentHandlerSystemComponent;
import com.tomtom.navui.systemport.systemcomponents.ScreenSystemComponent;
import com.tomtom.navui.util.Log;

public class R1SystemContext extends StockSystemContext {
    private static final String TAG = "R1SystemContext";
    private final int R1_MICHI_DPI = 240;

    private RequestPermissionOnReadyListenerWrapper mOnReadyListenerWrapper;

    public R1SystemContext(final Context context, final Class<? extends SystemService> serviceClz, final PendingIntent activityLaunchIntent,
            final ControlContext controlPort) {
        super(context, serviceClz, activityLaunchIntent, controlPort);

        final SystemComponentContainer systemComponents = getSystemComponentContainer();
        systemComponents.put(IntentHandlerSystemComponent.class, new R1IntentHandlerSystemComponent());
        systemComponents.put(ScreenSystemComponent.class, new R1ScreenSystemComponent(this, context));
    }

    @Override
    public int getRendererDensity() {
        if (Log.D) {
            Log.d(TAG, "Current DPI for R1 Michi " + R1_MICHI_DPI);
        }
        return R1_MICHI_DPI;
    }

    @Override
    public void setOnReadyListener(final OnReadyListener listener) {
        // Dismiss the previous wrapper if necessary
        if (mOnReadyListenerWrapper != null) {
            if (mOnReadyListenerWrapper.getWrappedListener() != listener) {
                mOnReadyListenerWrapper.dismiss();
                mOnReadyListenerWrapper = null;
            } else {
                return;
            }
        }

        // Wrap the listener, so we can request permissions before invoking it
        if (listener != null) {
            mOnReadyListenerWrapper = new RequestPermissionOnReadyListenerWrapper(listener);
            super.setOnReadyListener(mOnReadyListenerWrapper);
        } else {
            super.setOnReadyListener(null);
        }
    }
}
