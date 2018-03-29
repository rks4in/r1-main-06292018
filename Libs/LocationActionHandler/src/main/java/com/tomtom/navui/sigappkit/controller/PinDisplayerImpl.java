
package com.tomtom.navui.sigappkit.controller;

import android.content.Intent;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.AppScreen;
import com.tomtom.navui.appkit.LocationPreviewScreen;
import com.tomtom.navui.appkit.action.PinMapAction;
import com.tomtom.navui.sigappkit.SigLocationPreviewScreen;
import com.tomtom.navui.sigappkit.maprenderer.MapElement;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.systemport.systemcomponents.ScreenSystemComponent;
import com.tomtom.navui.taskkit.Location2;
import com.tomtom.navui.util.Log;

public class PinDisplayerImpl implements ShowCoordinateController {

    protected static final String TAG = "PinDisplayerImpl";
    private final AppContext mAppContext;
    private final SystemContext mSystemPort;
    private final ScreenSystemComponent mScreenSystemComponent;
    private Location2 mLocation2;

    public PinDisplayerImpl(final AppContext appContext) {
        this.mAppContext = appContext;
        this.mSystemPort = appContext.getSystemPort();
        this.mScreenSystemComponent = mSystemPort.getComponent(ScreenSystemComponent.class);
    }

    public AppContext getAppContext() {
        return mAppContext;
    }

    @Override
    public void displayLocation(final Location2 location2) {
        this.mLocation2 = location2;
        startMapScreenWithPin(this.mLocation2);
    }

    /**
     * Start the home screen with popup
     * 
     * @param location Location to preview - location ownership is passed to the screen - i.e. do
     *            not release
     */
    private void startMapScreenWithPin(final Location2 location) {
        if (Log.ENTRY) {
            Log.entry(TAG, "startLocationHomeScreen(), location: " + location);
        }
        
        // screen we're going to next.
        final Intent intent = new Intent(LocationPreviewScreen.class.getSimpleName());
        // parameter for the location that the home screen will
        // focus on
        intent.putExtra(AppScreen.LOCATION, location.persist());
        intent.putExtra(SigLocationPreviewScreen.LOCATION_TYPE, MapElement.Type.CUSTOM_PUSH_PIN);
        intent.putExtra(AppScreen.LOCATION_LAT_LON, location.getCoordinate());
        intent.putExtra(PinMapAction.EXTERNAL_LOCATTION_KEY, true);
        // start the screen
        mScreenSystemComponent.startScreen(intent);
    }
}
