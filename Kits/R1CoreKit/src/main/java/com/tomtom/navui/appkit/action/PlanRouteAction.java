package com.tomtom.navui.appkit.action;

/**
 * An action that as a result plan route to localization URI to trigger this action:
 * action://PlanRoute
 */

public interface PlanRouteAction extends Action {
    
    /**
     * Type of given destination to plan route to.
     * Type should be added always as second parameter of action after destination
     * 
     */
    enum ParamType {
        LOCATION2,
        PARSED_DATA
    }
}
