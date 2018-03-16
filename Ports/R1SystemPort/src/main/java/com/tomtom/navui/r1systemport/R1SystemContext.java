package com.tomtom.navui.r1systemport;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tomtom.navui.controlport.ControlContext;
import com.tomtom.navui.r1systemport.systemcomponents.R1IntentHandlerSystemComponent;
import com.tomtom.navui.r1systemport.systemcomponents.R1ScreenSystemComponent;
import com.tomtom.navui.stocksystemport.StockSystemContext;
import com.tomtom.navui.stocksystemport.SystemComponentContainer;
import com.tomtom.navui.systemport.SystemService;
import com.tomtom.navui.systemport.systemcomponents.IntentHandlerSystemComponent;
import com.tomtom.navui.systemport.systemcomponents.ScreenSystemComponent;

public class R1SystemContext extends StockSystemContext {
    public R1SystemContext(final Context context, final Class<? extends SystemService> serviceClz, final PendingIntent activityLaunchIntent,
            final ControlContext controlPort) {
        super(context, serviceClz, activityLaunchIntent, controlPort);

        final SystemComponentContainer systemComponents = getSystemComponentContainer();
        systemComponents.put(IntentHandlerSystemComponent.class, new R1IntentHandlerSystemComponent());
        systemComponents.put(ScreenSystemComponent.class, new R1ScreenSystemComponent(this, context));
    }
}
