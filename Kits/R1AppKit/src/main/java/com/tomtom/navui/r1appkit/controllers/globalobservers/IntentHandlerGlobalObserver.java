package com.tomtom.navui.r1appkit.controllers.globalobservers;

import android.support.annotation.NonNull;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.input.intent.IntentActionHandler;
import com.tomtom.navui.sigappkit.controllers.globalobservers.BaseGlobalObserver;
import com.tomtom.navui.systemport.systemcomponents.IntentHandlerSystemComponent;

/**
 * A global observer that provides the {@link IntentHandlerSystemComponent} with an action handler
 * when the app is in a state where intents may be handled.
 */
public class IntentHandlerGlobalObserver extends BaseGlobalObserver {

    private final IntentHandlerSystemComponent mIntentHandlerSystemComponent;
    private final IntentActionHandler mIntentActionHandler;

    public IntentHandlerGlobalObserver(@NonNull final AppContext appContext) {
        mIntentHandlerSystemComponent = appContext.getSystemPort().getComponent(IntentHandlerSystemComponent.class);
        mIntentActionHandler = new IntentActionHandler(appContext);
    }

    @Override
    public void onAppActive() {
        mIntentHandlerSystemComponent.setIntentHandler(mIntentActionHandler);
    }

    @Override
    public void onAppInactive() {
        mIntentHandlerSystemComponent.setIntentHandler(null);
    }

}
