
package com.tomtom.navui.sigappkit.controller;

import android.content.Intent;
import android.net.Uri;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.AppDialog;
import com.tomtom.navui.appkit.AppScreen;
import com.tomtom.navui.appkit.HomeScreen;
import com.tomtom.navui.appkit.NoGpsPlanRouteDialog;
import com.tomtom.navui.appkit.RouteAlreadyPlannedDialog;
import com.tomtom.navui.appkit.action.Action;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.systemport.SystemGpsObservable;
import com.tomtom.navui.taskkit.Location2;
import com.tomtom.navui.taskkit.route.Route;
import com.tomtom.navui.taskkit.route.RouteGuidanceTask;
import com.tomtom.navui.taskkit.route.RoutePlanningTask;

public class RouteDisplayerImpl implements ShowCoordinateController {
    private static final String ACTION_ADD_WAY_POINT = "action://AddWayPoint";
    
    private final AppContext mAppContext;
    private SystemContext mSystemPort;
    private RouteGuidanceTask mRouteGuidance;
    private RoutePlanningTask mRoutePlanning;

    public RouteDisplayerImpl(final AppContext appContext) {
        this.mAppContext = appContext;
        mSystemPort = appContext.getSystemPort();
    }

    public AppContext getAppContext() {
        return mAppContext;
    }

    @Override
    public void displayLocation(final Location2 location2) {
        mRouteGuidance = getAppContext().getTaskKit().newTask(RouteGuidanceTask.class);
        mRoutePlanning = getAppContext().getTaskKit().newTask(RoutePlanningTask.class);

        final SystemGpsObservable systemGpsObservable = mSystemPort
            .getSystemObservable(SystemGpsObservable.class);

        if (!isGpsOn(systemGpsObservable)) {
            showNoGpsDialog(location2);
        } else {
            startPlanRoute(location2);
        }
        systemGpsObservable.release();
    }

    private boolean isGpsOn(final SystemGpsObservable mSystemGpsObservable) {
        final SystemGpsObservable.LocationServiceState gpsStatus = mSystemGpsObservable.getModel()
                .getEnum(SystemGpsObservable.Attributes.GPS_STATUS);
        return gpsStatus == SystemGpsObservable.LocationServiceState.STATE_GPS_ON;
    }

    protected void startPlanRoute(final Location2 location2) {
        //make sure we are showing map 
        final Intent intent = new Intent(HomeScreen.class.getSimpleName());
        intent.addFlags(AppScreen.FLAG_SCREEN_CLEAR_HISTORY);
        mSystemPort.startScreen(intent);
        
        final Route activeRoute = mRouteGuidance.getActiveRoute();
        if (activeRoute != null) {
            showAddWayPointDialog(location2);
        } else {
            planRoute(location2);
        }
    }

    protected Action addLocationToRoute(final Location2 location2) {
        final Action a = getAppContext().newAction(Uri.parse(ACTION_ADD_WAY_POINT));
        a.addParameter(location2);
        a.dispatchAction();
        return a;
    }

    protected void planRoute(final Location2 location2) {
        mRoutePlanning.planRouteFromCurrentLocation(location2);
    }

    protected void showNoGpsDialog(final Location2 location2) {
        final Intent noGpsPlanRouteIntent = new Intent(NoGpsPlanRouteDialog.class.getSimpleName());
        noGpsPlanRouteIntent.addCategory(AppDialog.DIALOG_CATEGORY);
        
        //location2 is already copied - dialog will handle releasing, dont release here
        noGpsPlanRouteIntent.putExtra(NoGpsPlanRouteDialog.ARGUMENT_LOCATION, location2.persist());
        getAppContext().getSystemPort().startScreen(noGpsPlanRouteIntent);
      
    }

    protected void showAddWayPointDialog(final Location2 location2) {
        
        final Intent routeAlreadyPlannedIntent = new Intent(RouteAlreadyPlannedDialog.class.getSimpleName());
        routeAlreadyPlannedIntent.addCategory(AppDialog.DIALOG_CATEGORY);
        
        //location2 is already copied - dialog will handle releasing, dont release here
        routeAlreadyPlannedIntent.putExtra(RouteAlreadyPlannedDialog.ARGUMENT_LOCATION, location2.persist());
        
        getAppContext().getSystemPort().startScreen(routeAlreadyPlannedIntent);
    }
}
