
package com.tomtom.navui.sigappkit.action;

import java.util.List;

import android.net.Uri;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.input.errhandler.InputDataErrorDialogHandler;
import com.tomtom.navui.input.errhandler.InputDataErrorHandler;
import com.tomtom.navui.input.parser.data.ParsedData;
import com.tomtom.navui.input.parser.data.location.GeoLocationData;
import com.tomtom.navui.sigappkit.controller.FreeTextLocationSearchController;
import com.tomtom.navui.sigappkit.controller.LocationSearchController;
import com.tomtom.navui.sigappkit.controller.ShowCoordinateController;
import com.tomtom.navui.taskkit.Location2;
import com.tomtom.navui.taskkit.location.GeoCoderTask.LocationListener;
import com.tomtom.navui.util.Log;

public abstract class SigBaseLocationAction extends SigAction implements LocationListener {

    private static final String TAG = "SigBaseLocationAction";
    protected LocationSearchController mLocationSearcher;
    protected ShowCoordinateController mDisplayer;

    public SigBaseLocationAction(final AppContext appContext, final Uri uri) {
        super(appContext, uri);
    }

    @Override
    public boolean onAction() {
        if (Log.ENTRY) {
            Log.entry(TAG, "We starting " + TAG + "action ");
        }
        final ParsedData data = getDataParam();
        final InputDataErrorHandler errorHandler = getErrorHandlerParam();
        createSearcher(data, errorHandler);
        mLocationSearcher.search(data);
        return true;
    }

    private void createSearcher(final ParsedData data, final InputDataErrorHandler errorHandler) {
        if (data instanceof GeoLocationData) {
            mLocationSearcher = new CoordinateLocationSearchController(getAppContext(), this, errorHandler);
        } else {
            mLocationSearcher = new FreeTextLocationSearchController(getAppContext(), this, errorHandler);
        }
    }

    private ParsedData getDataParam() {
        if (getParamList() == null || getParamList().isEmpty()) {
            throw new IllegalArgumentException("We can hanldle only not null or empty params");
        }
        return (ParsedData) getParamList().get(0);
    }

    private InputDataErrorHandler getErrorHandlerParam() {
        final List<Object> paramList = getParamList();
        if (paramList != null) {
            for (final Object param : paramList) {
                if (param instanceof InputDataErrorHandler) {
                    return (InputDataErrorHandler) param;
                }
            }
        }
        return new InputDataErrorDialogHandler(getAppContext());
    }

    @Override
    public void onLocationsRetrieved(final List<Location2> locationList) {
        if (Log.ENTRY) {
            Log.entry(TAG, "onLocationsRetrieved " + locationList);
        }
        final Location2 location = locationList.get(0).copy();
        if (Log.D) {
            Log.d(TAG, "display location as a PIN or END point" + location);
        }
        mDisplayer.displayLocation(location);
    }

}
