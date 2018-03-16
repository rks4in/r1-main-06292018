
package com.tomtom.navui.input.parser.data.location;

import android.net.Uri;

import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.input.parser.data.ParsedData;
import com.tomtom.navui.taskkit.route.Wgs84Coordinate;
import com.tomtom.navui.util.Log;

public class DataParseResult implements ParseResult {

    // default actions
    public static final String PLAN_ROUTE_ACTION = "action://PlanRoute";
    public static final String PIN_MAP_ACTION = "action://PinMap";

    private static final String COORDINATE_SEPARATOR = "(?:\\s*,\\s*|\\s+)";
    private static final String TAG = "DataParseResult";

    private static final int MIN_QUERY_LENGHT = 3;
    private static final int MAX_QUERY_LENGTH = 1000;

    private Uri mAction;
    private ParsedData mData;
    private boolean mAsynchronous = false;

    @Override
    public Uri getAction() {
        return mAction;
    }

    @Override
    public ParsedData getParsedData() {
        return mData;
    }

    @Override
    public boolean isAsynchronous() {
        return mAsynchronous;
    }

    /**
     * Method creates DataParseResult from latitude and longitude coordinates.
     * 
     * @param latitude int
     * @param longitude int
     * @see {@link Wgs84Coordinate}
     * @return DataParseResult
     * @throws ParseFailureException when coordinates are over the limits
     */
    public static DataParseResult createFromCoordinate(final int latitude, final int longitude) throws ParseFailureException {
        checkCoordinatesRanges(latitude, longitude);
        final DataParseResult result = new DataParseResult();
        result.setData(new GeoLocationData(latitude, longitude));
        return result;
    }

    /**
     * Method creates DataParseResult from latitude and longitude coordinates. "action" parameter
     * define an action to be performed by the TaskKit fe. <b>action://PlanRoute</b>
     * 
     * @param latitude int
     * @param longitude int
     * @param action String
     * @see {@link Wgs84Coordinate}
     * @return DataParseResult
     * @throws ParseFailureException when coordinates are over the limits
     */
    public static DataParseResult createFromCoordinateWithAction(final int latitude, final int longitude, final String action)
        throws ParseFailureException {
        checkCoordinatesRanges(latitude, longitude);
        final DataParseResult result = createFromCoordinate(latitude, longitude);
        result.setAction(action);
        return result;
    }

    /**
     * Method creates DataParseResult with geographical coordinates from stringPart
     * <p>
     * Example: 52.2121,4.787
     * </p>
     * 
     * @param coordinatesPart 52.2121,4.787
     * @return The parse result for the given coordinates
     * @throws ParseFailureException when there are not geographical coordinates in the
     *             coordinatesPart parameter or if the coordinates are over the limits
     */
    public static DataParseResult createFromCoordinatePart(final String coordinatesPart) throws ParseFailureException {
        if (Log.D) {
            Log.d(TAG, "coordinates: " + coordinatesPart);
        }
        if (coordinatesPart == null) {
            throw new ParseFailureException("coordinatesPart not supplied");
        }
        final String[] textCoordinate = coordinatesPart.split(COORDINATE_SEPARATOR);
        if (textCoordinate.length != 2) {
            throw new ParseFailureException("Invalid data in intent data, wrong coordinate format. Format should be XX.XXXX,YY.YYYY but was: " + coordinatesPart);
        }
        int latitude;
        int longitude;
        try {
            latitude = (int) (Double.valueOf(textCoordinate[0]) * 10e5);
            longitude = (int) (Double.valueOf(textCoordinate[1]) * 10e5);

            checkCoordinatesRanges(latitude, longitude);

        } catch (final NumberFormatException e) {
            throw new ParseFailureException("Invalid data in intent data", e);
        }
        return DataParseResult.createFromCoordinate(latitude, longitude);
    }

    /**
     * Check if the coordinates are in the defined limits
     * 
     * @see {@link Wgs84Coordinate}
     * @param latitude int
     * @param longitude int
     * @throws ParseFailureException when latitude or longitude is outside the limits
     */
    private static void checkCoordinatesRanges(final int latitude, final int longitude) throws ParseFailureException {
        if (latitude > Wgs84Coordinate.LATITUDE_MAX || latitude < Wgs84Coordinate.LATITUDE_MIN) {
            throw new ParseFailureException("Latitude coordinate is over the limits");
        }

        if (longitude > Wgs84Coordinate.LONGITUDE_MAX || longitude < Wgs84Coordinate.LONGITUDE_MIN) {
            throw new ParseFailureException("Longitude coordinate is over the limits");
        }
    }

    /**
     * Set action that can be performed by the TaskKit fe. <b>action://PlanRoute</b>
     * 
     * @param action String
     * @return this DataParseResult object with added action
     */
    public DataParseResult setAction(final String action) {
        mAction = Uri.parse(action);
        return this;
    }

    /**
     * Sets whether or not the action should be executed instantly on the current thread, or posted
     * to be ran on the same thread after the current task has completed. This value is false by
     * default.
     *
     * @param asynchronous True for asynchronous execution, false for synchronous
     */
    public DataParseResult setAsynchronous(final boolean asynchronous) {
        mAsynchronous = asynchronous;
        return this;
    }

    private void setData(final ParsedData data) {
        mData = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mAction == null) ? 0 : mAction.hashCode());
        result = prime * result + ((mData == null) ? 0 : mData.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataParseResult other = (DataParseResult) obj;
        if (mAction == null) {
            if (other.mAction != null) {
                return false;
            }
        } else if (!mAction.equals(other.mAction)) {
            return false;
        }
        if (mData == null) {
            if (other.mData != null) {
                return false;
            }
        } else if (!mData.equals(other.mData)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Action: " + mAction + " Data: " + mData;
    }

    /**
     * Method creates DataParseResult from textQuery. DataParseResult doesn't contain geographical
     * data, only text query (address) that can be used by the TaskKit to find location on the map.
     * 
     * @param textQuery String
     * @return DataParseResult
     * @throws ParseFailureException then can't create DataParseResult from the textQuery
     * @throws ParseException when textQuery doesn't meet the requirements
     */
    public static DataParseResult createFromSearchQuery(final String textQuery) throws ParseFailureException, ParseException {
        if (textQuery == null) {
            throw new ParseFailureException("Intent query is not valid: null");
        } else if (textQuery.length() < MIN_QUERY_LENGHT) {
            throw new ParseException("Intent query is not valid: " + textQuery, textQuery);
        } else if (textQuery.length() > MAX_QUERY_LENGTH) {
            throw new ParseException("Intent query is not valid: " + textQuery, textQuery.substring(0, MAX_QUERY_LENGTH));
        }
        final DataParseResult result = new DataParseResult();
        result.setData(TextLocationData.create(textQuery));
        return result;
    }

}
