
package com.tomtom.navui.input.parser;

/**
 * Exception generated when we have a problem with parsing input data, but we could parse anything,
 * and we provide query to user to search screen in order to improve
 * 
 */
public class ParseException extends Exception {

    private String mClosestQuery = ""; //default 

    public ParseException(final String debugMessage, final String closestQuery, final Throwable throwable) {
        super(debugMessage, throwable);
        mClosestQuery = closestQuery;
    }

    public ParseException(final String debugMessage, final String closestQuery) {
        super(debugMessage);
        mClosestQuery = closestQuery;
    }

    /**
     * Proposition of query parsed by application
     * 
     * @return
     */
    public String getClosestQuery() {
        return mClosestQuery;
    }

    /**
     * 
     */
    private static final long serialVersionUID = -8961406322016213352L;

    @Override
    public String getMessage() {
        return super.getMessage() + " ClosestQuery proposition is " + mClosestQuery;
    }

}
