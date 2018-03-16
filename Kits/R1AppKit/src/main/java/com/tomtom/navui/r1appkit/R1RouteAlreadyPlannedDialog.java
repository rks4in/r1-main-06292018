package com.tomtom.navui.r1appkit;

import android.content.Context;
import android.os.Bundle;

import com.tomtom.navui.appkit.RouteAlreadyPlannedDialog;
import com.tomtom.navui.sigappkit.SigAppContext;
import com.tomtom.navui.systemport.systemcomponents.DialogSystemComponent;
import com.tomtom.navui.systemport.systemcomponents.dialogs.SystemDialog;
import com.tomtom.navui.systemport.systemcomponents.dialogs.SystemDialog.OnClickListener;
import com.tomtom.navui.systemport.systemcomponents.dialogs.SystemDialogBuilder;
import com.tomtom.navui.taskkit.Location2;
import com.tomtom.navui.taskkit.route.RouteGuidanceTask;
import com.tomtom.navui.taskkit.route.RoutePlanningTask;
import com.tomtom.navui.util.Theme;
import com.tomtom.navui.util.Log;

public class R1RouteAlreadyPlannedDialog extends R1AppDialog implements RouteAlreadyPlannedDialog, OnClickListener {

    private static final String TAG = "R1RouteAlreadyPlannedDialog";

    private Location2 mLocation;
    private RouteGuidanceTask mRouteGuidanceTask;
    private RoutePlanningTask mRoutePlanningTask;

    private static final String ACTION_ADD_WAY_POINT = "action://AddWayPoint";

    public R1RouteAlreadyPlannedDialog(SigAppContext context) {
        super(context);
    }
    @Override
    public SystemDialog onCreateDialog(Context context, Bundle savedInstanceState) {
        mRouteGuidanceTask = getContext().getTaskKit().newTask(RouteGuidanceTask.class);
        mRoutePlanningTask = getContext().getTaskKit().newTask(RoutePlanningTask.class);
        if (savedInstanceState != null && savedInstanceState.containsKey(ARGUMENT_LOCATION)) {
            mLocation = getContext().getTaskKit().retrieveLocation(savedInstanceState.getString(ARGUMENT_LOCATION));
        } else {
            mLocation = getContext().getTaskKit().retrieveLocation(getArguments().getString(ARGUMENT_LOCATION));
        }
        // Just a quick hack. Not translations.
        final SystemDialogBuilder builder = getContext().getSystemPort().getComponent(DialogSystemComponent.class).createBuilder();
        builder.setCancelable(true);
        builder.setTitle("Route already planned");
        builder.setMessage("There is already a route planned. Plan a route to a new location?");
        builder.setPositiveButton("Yes", this);
        builder.setNegativeButton("No",  this);
        return builder.build();
    }
    @Override
    public void onClick(SystemDialog dialog, int which) {
        switch (which) {
        case BUTTON_POSITIVE:
            mRoutePlanningTask.planRouteFromCurrentLocation(mLocation);
            break;
        case BUTTON_NEGATIVE:
            break;
        default:
             if (Log.W) Log.w(TAG, "Button " + which + " is not supported");
            break;
        }
        dialog.cancel();
    }
    @Override
    public void onCancel() {
        super.onCancel();
        mRouteGuidanceTask.release();
        mRouteGuidanceTask = null;
        mRoutePlanningTask.release();
        mRoutePlanningTask = null;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARGUMENT_LOCATION, mLocation.persist());
        mLocation.release();
        mLocation = null;
    }
    @Override
    public void onDestroy() {
        if ((mLocation != null) && !isChangingConfiguration()) {
            mLocation.release();
            mLocation = null;
        }
        super.onDestroy();
    }
}
