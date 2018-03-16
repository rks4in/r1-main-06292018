package com.tomtom.r1navapp;

import android.content.Intent;
import android.os.Bundle;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.stocksystemport.StockActivity;
import com.tomtom.navui.systemport.systemcomponents.IntentHandlerSystemComponent;
import com.tomtom.navui.util.Log;

public class R1MainActivity extends StockActivity {
    private static final String TAG = "R1MainActivity";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (Log.D) {
            Log.d(TAG, "onCreate " + getIntent() + " " + this);
        }
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }
    @Override
    protected void onNewIntent(final Intent intent) {
        if (Log.D) {
            Log.d(TAG, "onNewIntent " + intent);
        }
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(final Intent intent) {
        final AppContext appKit = getAppKit();
        final IntentHandlerSystemComponent intentHandlerComponent =
                appKit.getSystemPort().getComponent(IntentHandlerSystemComponent.class);
        intentHandlerComponent.onNewIntent(intent);
    }
}
