package com.tomtom.navui.systemport.systemcomponents;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tomtom.navui.util.ActionHandler;

/**
 * A system component used for handling external intents that get provided to the activity.
 */
public interface IntentHandlerSystemComponent extends SystemComponent {

    /**
     * Sets the handler for any intents that are coming in, or came in previously and haven't been
     * handled. Can be set to {@code null} to pause handling incoming intents.
     *
     * @param intentHandler The handler for intents
     */
    void setIntentHandler(@Nullable ActionHandler<Intent> intentHandler);

    /**
     * Called when a new intent is provided, either through starting the app or through an update.
     *
     * @param intent The intent to handle
     */
    void onNewIntent(@NonNull Intent intent);

}
