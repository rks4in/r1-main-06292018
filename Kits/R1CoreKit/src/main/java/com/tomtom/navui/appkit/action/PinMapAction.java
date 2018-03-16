package com.tomtom.navui.appkit.action;

/**
 * An action that as a result show Pin on Map URI to trigger this action: action://PlanRoute
 */

public interface PinMapAction extends Action {
    /**Boolean argument whether pin location coming from external application */
    public static final String EXTERNAL_LOCATTION_KEY = "external_location_key";
}
