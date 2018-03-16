package com.tomtom.navui.r1systemport;

public final class ErrorReporterConstants {

    /**
     * Key used to report current application fragment/screen stack.
     */
    public static final String FRAGMENT_STACK = "application fragment stack";
    
    /**
     * Key used to report last screen started in app context.
     */
    public static final String LAST_SCREEN_STARTED = "last screen started";

    /**
     * Key used to report current root screen provider set.
     */
    public static final String CURRENT_ROOT_SCREEN_PROVIDER = "current root screen provider";

    /**
     * Key used to report installed maps in mobile navapp.
     */
    public static final String INSTALLED_MAPS = "installed maps";

    /**
     * Key used to report if the external storage that is being used is removable (e.g., an external
     * SD card).
     */
    public static final String EXTERNAL_STORAGE_REMOVABLE = "external storage removable";
    
    /**
     * Value separator for status reports.
     */
    public static final String VALUE_SEPARATOR = "||";

    private ErrorReporterConstants() {

    }
}
