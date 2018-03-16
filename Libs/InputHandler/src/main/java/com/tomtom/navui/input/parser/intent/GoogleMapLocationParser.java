
package com.tomtom.navui.input.parser.intent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Intent;
import android.net.Uri;

import com.google.common.base.CharMatcher;
import com.tomtom.navui.input.parser.InputParser;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.input.parser.data.location.DataParseResult;
import com.tomtom.navui.util.Log;

/**
 * Parsing intent with maps.google data type like: http://maps.google.com/maps?hl
 * =pl&daddr=Ferdinand+Bolstraat+180,+1072+LV+Amsterdam ,+Holandia+@52.351335,4.891196
 * &panel=1&f=d&fb=1&dirflg=d&geocode=0,52.351335, 4.891196&cid=0,0,14080343995521171618
 * 
 */
public class GoogleMapLocationParser implements InputParser<Intent> {

    private static final int OFFSET_FOR_FIRST_COORDINATE = 2; //geocode=0, ==>  0, is not need
    private static final String REGEX_FOR_EXTRACTING_COORDINATES_FROM_STRING = "[^(\\-?\\d+(\\.\\d+)?),\\s*(\\-?\\d+(\\.\\d+)?)]";
    private static final String REGEX_FOR_COORDINATES_IN_GEO_LOC_QUERY = "^(loc|geo):(\\-?\\d+(\\.\\d+)?),\\s*(\\-?\\d+(\\.\\d+)?).*$";
    private static final String CHARS_FOR_CLEANUP_COORDINATES_IN_GEO_LOC_QUERY = "()#=";
    private static final String PARAM_SEPARATOR = "&";
    private static final String TAG = "GoogleMapLocationParser";
    private static final String HOST_GOOGLE = "maps.google";
    private static final String GEOCODE_PARAM = "geocode";
    private static final String GEOCODE_PARAM2 = "@";
    private static final String GEOCODE_ADDR_PARAM = "q";
    private static final String GEOCODE_DESTINATION_ADDR_PARAM = "daddr";

    @Override
    public boolean accept(final Intent intent) {
        if ((intent.getData() != null) && (intent.getData().getHost() != null)) {
            final boolean acceptation = (intent.getData().getHost().contains(HOST_GOOGLE));
            if (acceptation && Log.D) {
                Log.d(TAG, TAG + " accept intent schema: " + intent.getScheme());
            }
            return acceptation;
        } else {
            return false;
        }
    }

    @Override
    public ParseResult parse(final Intent intent) throws ParseException, ParseFailureException {
        final String intentData = intent.getDataString();
        final Uri intentUriData = intent.getData();

        if (intentData == null) {
            throw new IllegalArgumentException("Intent has empty data. intentData is null ");
        }
        if (Log.D) {
            Log.d(TAG, "Parsing intent data : " + intentData);
        }

        try {
            if (hasGeoCode(intentUriData)) {
                return extractCoordinate(intentUriData);
            } else if (intentData.contains(GEOCODE_PARAM2)) {
                return extractCoordinate(intentData);
            } else if (containsCoordinateInGeoLocQuery(intentUriData)) {
                return extractCoordinateFromGeoLocQuery(intentUriData);
            } else {
                throw new ParseFailureException("Cant find geographical coordinates in the intent: " + intentData);
            }
        } catch (final ParseFailureException e) {
            return extractAddress(intentUriData);
        }
    }

    private boolean hasGeoCode(final Uri intentUriData) {
        return intentUriData.getQueryParameter(GEOCODE_PARAM) != null;
    }

    private DataParseResult extractAddress(final Uri intentData) throws ParseFailureException, ParseException {
        String address;
        if (intentData.getQueryParameterNames().contains(GEOCODE_DESTINATION_ADDR_PARAM)) {
            address = intentData.getQueryParameter(GEOCODE_DESTINATION_ADDR_PARAM);
        } else {
            address = intentData.getQueryParameter(GEOCODE_ADDR_PARAM);
        }

        // throw an exception in case of nullable address or in case there are coordinates
        if (address == null || address.contains(GEOCODE_PARAM2)) {
            throw new ParseFailureException("Intent query is not vaild = null");
        }

        try {
            address = URLDecoder.decode(address, "UTF-8").trim();
        } catch (final UnsupportedEncodingException e) {
            if (Log.E) {
                Log.e(TAG, "Cant decode address from intent: " + intentData, e);
            }
        }

        return DataParseResult.createFromSearchQuery(address).setAction(DataParseResult.PLAN_ROUTE_ACTION);
    }

    private DataParseResult extractCoordinate(final String intentData) throws ParseFailureException {
        final int coordinatesStart = intentData.indexOf(GEOCODE_PARAM2) + 1;
        String coordinates = intentData.substring(coordinatesStart, intentData.length());
        //trim from first "&" on, if there is such character
        if (coordinates.contains(PARAM_SEPARATOR)) {
            coordinates = coordinates.substring(0, coordinates.indexOf(PARAM_SEPARATOR));
        }
        return DataParseResult.createFromCoordinatePart(coordinates).setAction(DataParseResult.PLAN_ROUTE_ACTION);
    }

    private DataParseResult extractCoordinate(final Uri intentData) throws ParseFailureException {
        String coordinates = intentData.getQueryParameter(GEOCODE_PARAM);
        coordinates = coordinates.substring(OFFSET_FOR_FIRST_COORDINATE);
        return DataParseResult.createFromCoordinatePart(coordinates).setAction(DataParseResult.PLAN_ROUTE_ACTION);
    }

    private DataParseResult extractCoordinateFromGeoLocQuery(final Uri intentData) throws ParseFailureException {
        final String queryParameter = intentData.getQueryParameter(GEOCODE_ADDR_PARAM);
        final String queryWithoutLetters = queryParameter.replaceAll(REGEX_FOR_EXTRACTING_COORDINATES_FROM_STRING, "");
        final String coords = CharMatcher.anyOf(CHARS_FOR_CLEANUP_COORDINATES_IN_GEO_LOC_QUERY).removeFrom(queryWithoutLetters);

        if (Log.D) {
            Log.d(TAG, "Coordinates extracted from Geo/Loc query: " + coords);
        }

        return DataParseResult.createFromCoordinatePart(coords).setAction(DataParseResult.PLAN_ROUTE_ACTION);
    }

    private boolean containsCoordinateInGeoLocQuery(final Uri intentData) throws ParseFailureException {
        final String queryParameter = intentData.getQueryParameter(GEOCODE_ADDR_PARAM);

        return queryParameter != null && queryParameter.matches(REGEX_FOR_COORDINATES_IN_GEO_LOC_QUERY);
    }

}
