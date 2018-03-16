package com.tomtom.navui.appkit;

/**
 * Dialog shown when we try to plan a route from external intent but app has
 * already some route planned.
 * <p>
 * Parameters: <br>
 * {@link #ARGUMENT_LOCATION} - Location2 location to plan route to
 */
public interface RouteAlreadyPlannedDialog extends AppDialog {

    public static final String ARGUMENT_LOCATION = "locationArgument";
}
