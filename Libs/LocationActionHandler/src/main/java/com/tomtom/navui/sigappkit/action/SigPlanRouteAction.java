
package com.tomtom.navui.sigappkit.action;

import java.util.List;

import android.net.Uri;

import com.google.common.base.Preconditions;
import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.action.PlanRouteAction;
import com.tomtom.navui.sigappkit.controller.RouteDisplayerImpl;
import com.tomtom.navui.taskkit.Location2;

public class SigPlanRouteAction extends SigBaseLocationAction implements PlanRouteAction {

    public SigPlanRouteAction(final AppContext appContext, final Uri uri) {
        super(appContext, uri);
        mDisplayer = new RouteDisplayerImpl(getAppContext());
    }

    @Override
    public boolean onAction() {
        final ParamType paramType = getParamType(getParamList());
        if (ParamType.PARSED_DATA == paramType) {
            //resolve first parsed data to location
            return super.onAction();
        } else if (ParamType.LOCATION2 == paramType) {
            final Location2 location = (Location2) getParamList().get(0);
            //we already got location so display planned route
            mDisplayer.displayLocation(location);
            return true;
        }
        return false;
    }

    private ParamType getParamType(final List<Object> paramList) {
        Preconditions.checkNotNull(paramList, "param list cannot be null");
        Preconditions.checkArgument(!paramList.isEmpty(), "param list cannot be empty");
        for (final Object param : paramList) {
            if (param instanceof ParamType) {
                return (ParamType) param;
            }
        }
        return ParamType.PARSED_DATA;
    }
}
