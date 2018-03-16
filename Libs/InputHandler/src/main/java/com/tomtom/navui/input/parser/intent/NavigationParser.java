
package com.tomtom.navui.input.parser.intent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Intent;
import android.net.Uri;

import com.tomtom.navui.input.parser.InputParser;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.input.parser.data.location.DataParseResult;
import com.tomtom.navui.util.Log;

/**
 * Parsing intent with google.navigation data type like: google.navigation:///?ll
 * =52.367585%2C4.893381&q=Rokin%20174%2C%201012%20LE%20Amsterdam
 * %2C%20The%20Netherlands&entry=d&opt=4%3A0%2C5%3A0
 * 
 */
public class NavigationParser implements InputParser<Intent> {

    private static final String URL_UTF8_ENCODING = "UTF-8";
    private static final String TAG = "NavigationParser";
    private static final String SCHEME_NAVIGATION = "google.navigation";
    private static final String DESTINATION_LATIT_LANG_PARAMETER = "ll";
    private static final String DESTINATION_QUERY_PARAMETER = "q";
    private static final String DESTINATION_TITLE_PARAMETER = "title";

    @Override
    public boolean accept(final Intent input) {
        return SCHEME_NAVIGATION.equals(input.getScheme());
    }

    @Override
    public ParseResult parse(final Intent intent) throws ParseException, ParseFailureException {
        Uri uriData = intent.getData();
        try {
            final String intentData = URLDecoder.decode(intent.getDataString(), URL_UTF8_ENCODING);
            if (Log.D) {
                Log.d(TAG, "Parsing intent data : " + intentData);
            }

            if (!uriData.isHierarchical()) {
                uriData = makeHierarchical(uriData);
            }

            if (hasCoordinate(uriData)) {
                return extractCoordinates(uriData);
            } else {
                throw new ParseFailureException("Cant find geographical coordinates in the intent: " + intentData);
            }
        } catch (final ParseFailureException ex) {
            if (hasQuery(uriData)) {
                return extractQuery(uriData);
            } else if (hasTitle(uriData)) {
                return extractTitle(uriData);
            } else {
                throw ex;
            }
        } catch (final IndexOutOfBoundsException ex) {
            throw new ParseFailureException("Unable to parse intent. Intent data has improper format. " + ex.getMessage() + " "
                    + intent.getDataString());
        } catch (final UnsupportedEncodingException ex) {
            throw new ParseFailureException("Unable to parse intent. Intent data has improper format. " + ex.getMessage() + " "
                    + intent.getDataString());
        }
    }

    /**
     * Method converts none hierarchical to hierarchical URI. If the uri parameter is hierarchical
     * it will return the same object.
     * 
     * @param uri Uri
     * @return URI
     * @throws ParseFailureException when URI doesn't contain colon character
     */
    private Uri makeHierarchical(final Uri uri) throws ParseFailureException {
        if (uri.isHierarchical()) {
            return uri;
        }
        final String data = uri.toString();
        final int colonIndex = data.indexOf(':');
        if (colonIndex < 0) {
            throw new ParseFailureException("URL cant be converted to hierarchical");
        }
        final String newData = data.substring(0, colonIndex + 1) + "///?" + data.substring(colonIndex + 1);
        return Uri.parse(newData);
    }

    /**
     * Method extracts geographical coordinates from the URI
     * 
     * @param uriData Uri
     * @return ParseResult
     * @throws ParseFailureException when URI doesn't contain geographical parameters
     */
    private ParseResult extractCoordinates(final Uri uriData) throws ParseFailureException {
        final String destination = uriData.getQueryParameter(DESTINATION_LATIT_LANG_PARAMETER);
        return DataParseResult.createFromCoordinatePart(destination).setAction(DataParseResult.PLAN_ROUTE_ACTION);
    }

    /**
     * Method extracts query parameter from the URI
     * 
     * @param uriData Uri
     * @return ParseResult
     * @throws ParseException when URI doesn't meet the requirements
     * @throws ParseFailureException when URI doesn't contain query parameter
     */
    private ParseResult extractQuery(final Uri uriData) throws ParseException, ParseFailureException {
        return extractDataFromUri(uriData, DESTINATION_QUERY_PARAMETER);
    }

    /**
     * Method extracts title parameter from the URI
     * 
     * @param uriData Uri
     * @return ParseResult
     * @throws ParseException when URI doesn't meet the requirements
     * @throws ParseFailureException when URI doesn't contain title parameter
     */
    private ParseResult extractTitle(final Uri uriData) throws ParseException, ParseFailureException {
        return extractDataFromUri(uriData, DESTINATION_TITLE_PARAMETER);
    }

    /**
     * Method extracts from the URI given parameter.
     * 
     * @param uriData Uri
     * @param parameter String
     * @return ParseResult
     * @throws ParseException when URI doesn't meet the requirements
     * @throws ParseFailureException when URI doesn't contain given parameter
     */
    private ParseResult extractDataFromUri(final Uri uriData, final String parameter) throws ParseException, ParseFailureException {
        final String destination = uriData.getQueryParameter(parameter).trim();
        if (destination == null) {
            throw new ParseFailureException("Intent query is not vaild = null");
        }
        return DataParseResult.createFromSearchQuery(destination).setAction(DataParseResult.PLAN_ROUTE_ACTION);
    }

    /**
     * Check if the URI has title parameter
     * 
     * @param intentData Uri
     * @return boolean
     */
    private boolean hasTitle(final Uri intentData) {
        return intentData.getQueryParameter(DESTINATION_TITLE_PARAMETER) != null;
    }

    /**
     * Check if the URI has query parameter
     * 
     * @param intentData Uri
     * @return boolean
     */
    private boolean hasQuery(final Uri intentData) {
        return intentData.getQueryParameter(DESTINATION_QUERY_PARAMETER) != null;
    }

    /**
     * Check if the URI have coordinates parameters
     * 
     * @param intentData Uri
     * @return boolean
     */
    private boolean hasCoordinate(final Uri intentData) {
        return intentData.getQueryParameter(DESTINATION_LATIT_LANG_PARAMETER) != null;
    }

    @Override
    public String toString() {
        return TAG;
    }

}
