package com.tomtom.navui.r1systemport.systemcomponents;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tomtom.navui.systemport.systemcomponents.IntentHandlerSystemComponent;

import com.tomtom.navui.util.ActionHandler;

public class R1IntentHandlerSystemComponent implements IntentHandlerSystemComponent {

    @Nullable
    private ActionHandler<Intent> mIntentHandler;
    @Nullable
    private Intent mLastUnhandledIntent;

    @Override
    public void release() {
        mIntentHandler = null;
        mLastUnhandledIntent = null;
    }

    @Override
    public void setIntentHandler(@Nullable final ActionHandler<Intent> intentHandler) {
        mIntentHandler = intentHandler;

        if (mIntentHandler != null && mLastUnhandledIntent != null) {
            mIntentHandler.handle(mLastUnhandledIntent);
            mLastUnhandledIntent = null;
        }
    }

    @Override
    public void onNewIntent(@NonNull final Intent intent) {
        if (mIntentHandler != null) {
            mIntentHandler.handle(intent);
        } else {
            mLastUnhandledIntent = intent;
        }
    }

}
