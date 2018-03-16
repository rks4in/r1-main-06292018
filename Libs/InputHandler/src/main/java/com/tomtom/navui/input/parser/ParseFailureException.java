
package com.tomtom.navui.input.parser;

/**
 * This exception we should throw when we can't parse input data. When input data is not supported
 * or format of external data is not recognized by us.
 * 
 * @see {@link ParseException} when format of data is not recognize by us, but we can parse any data
 *      we should throw {@link ParseException}
 */
public class ParseFailureException extends Exception {

    public ParseFailureException(final String debugMessage, final Throwable throwable) {
        super(debugMessage, throwable);
    }

    public ParseFailureException(final String debugMessage) {
        super(debugMessage);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -8961406322016213352L;

}
