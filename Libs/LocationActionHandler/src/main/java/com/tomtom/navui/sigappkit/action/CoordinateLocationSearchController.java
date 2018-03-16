
package com.tomtom.navui.sigappkit.action;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.input.errhandler.InputDataErrorHandler;
import com.tomtom.navui.input.parser.data.ParsedData;
import com.tomtom.navui.input.parser.data.location.GeoLocationData;
import com.tomtom.navui.sigappkit.controller.LocationSearchController;
import com.tomtom.navui.taskkit.TaskNotReadyException;
import com.tomtom.navui.taskkit.location.GeoCoderTask;
import com.tomtom.navui.taskkit.location.GeoCoderTask.LocationListener;
import com.tomtom.navui.taskkit.location.Wgs84CoordinateImpl;
import com.tomtom.navui.taskkit.route.Wgs84Coordinate;
import com.tomtom.navui.util.Log;

public class CoordinateLocationSearchController implements LocationSearchController {

    private static final String TAG = "CoordinateLocationSearchController";
    private final AppContext mAppContext;
    private final LocationListener mListener;
    private final InputDataErrorHandler mDataErrorHandler;

    public CoordinateLocationSearchController(final AppContext appContext, final LocationListener listener, final InputDataErrorHandler errorHandler) {
        this.mAppContext = appContext;
        this.mListener = listener;
        this.mDataErrorHandler = errorHandler;
    }

    public AppContext getAppContext() {
        return mAppContext;
    }

    @Override
    public void search(final ParsedData data) {
        final GeoLocationData geoData = (GeoLocationData) data;
        try {
            final GeoCoderTask lst = getAppContext().getTaskKit().newTask(GeoCoderTask.class);
            final Wgs84Coordinate coordinate = creatCoordinate(geoData);
            lst.getLocations(coordinate, 1, mListener);
        } catch (final TaskNotReadyException e) {
            if (Log.E) {
                Log.e(TAG, e.getMessage(), e);
            }
            mDataErrorHandler.handle(e);
        }
    }

    private Wgs84Coordinate creatCoordinate(final GeoLocationData geoData) {
        final Wgs84Coordinate coordinate = new Wgs84CoordinateImpl(geoData.getLatitude(), geoData.getLongitude());
        return coordinate;
    }

}
