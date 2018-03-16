
package com.tomtom.navui.sigappkit.action;

import android.net.Uri;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.action.PlanRouteAction;
import com.tomtom.navui.sigappkit.controller.PinDisplayerImpl;

public class SigPinMapAction extends SigBaseLocationAction implements PlanRouteAction {

    static final String TAG = "SigPinMapAction";

    public SigPinMapAction(final AppContext appContext, final Uri uri) {
        super(appContext, uri);
        mDisplayer = new PinDisplayerImpl(appContext);
    }
}
