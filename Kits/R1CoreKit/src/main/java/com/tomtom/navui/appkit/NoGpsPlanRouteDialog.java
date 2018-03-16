package com.tomtom.navui.appkit;

/**
 * Dialog shown on attempt to plan route without active GPS.
 * <p>
 * Parameters: <br>
 * {@link #ARGUMENT_LOCATION} - Location2 location to plan route to
 *
 */
public interface NoGpsPlanRouteDialog {

    public static final String ARGUMENT_LOCATION = "locationArgument";
}
