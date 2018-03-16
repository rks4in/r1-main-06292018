
package com.tomtom.navui.input.parser.intent;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.tomtom.navui.input.parser.InputParser;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.input.parser.data.location.DataParseResult;
import com.tomtom.navui.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser that handles geo data, such as: {@code geo:52.376382,4.884262}.
 */
public class GeoLocationParser implements InputParser<Intent> {

    private static final String TAG = "GeoLocationParser";

    private static final String SCHEME = "geo";
    private static final String EMPTY_COORDINATES = "0,0";

    private static final String WHITE_SPACE = "(?:\\+|%20|%0A|%0D|%09|\\s)";
    private static final String COORDINATE = "-?\\d+(?:\\.\\d+)?";
    private static final String COORDINATES = COORDINATE + "(?:" + WHITE_SPACE + "*(?:,|%2C)" + WHITE_SPACE + "*|" + WHITE_SPACE + "+)" + COORDINATE;
    private static final Pattern sSchemePattern = Pattern.compile(
            "^geo:" +                                                   // Scheme prefix
            "(?:(" + COORDINATES + ")|[^?]*)" +                         // Host component (either coordinates or junk to be ignored)
            "(?:\\?.*q=(?:" +                                           // Query component with optional leading junk
                "(" + COORDINATES + ")" + WHITE_SPACE + "*(?:[(&]|$)" + // Query coordinates, including closure to ensure it's not part of an address
            "|" +                                                       // Or, if we didn't find query coordinates
                "([^&]*)" +                                             // Any other contents as address
            "))?"                                                       // The query component is optional
    );

    @Override
    public String toString() {
        return TAG;
    }

    @Override
    public boolean accept(final Intent intent) {
        final boolean result = SCHEME.equals(intent.getScheme());
        if (result && Log.D) {
            Log.d(TAG, "Accepted intent with schema: " + intent.getScheme());
        }
        return result;
    }

    @Override
    public ParseResult parse(final Intent intent) throws ParseException, ParseFailureException {
        if (intent.getDataString() == null) {
            throw new IllegalArgumentException("Intent has no data");
        }

        final Matcher matcher = sSchemePattern.matcher(intent.getDataString());
        if (!matcher.find()) {
            throw new ParseFailureException("Data didn't match scheme: " + intent.getDataString());
        }

        final String hostCoordinates = uriDecode(matcher.group(1));
        if (areCoordinatesValid(hostCoordinates)) {
            return createParseResultFromCoordinates(hostCoordinates);
        }

        final String queryCoordinates = uriDecode(matcher.group(2));
        if (areCoordinatesValid(queryCoordinates)) {
            return createParseResultFromCoordinates(queryCoordinates);
        }

        return createParseResultFromQuery(uriDecode(matcher.group(3)));
    }

    private String uriDecode(final String string) {
        if (string == null) {
            return null;
        }
        return Uri.decode(string.replace('+', ' '));
    }

    private boolean areCoordinatesValid(final String coordinates) {
        return (!TextUtils.isEmpty(coordinates) && !EMPTY_COORDINATES.equals(coordinates));
    }

    private ParseResult createParseResultFromCoordinates(final String coordinates) throws ParseFailureException {
        return DataParseResult.createFromCoordinatePart(coordinates).setAction(DataParseResult.PIN_MAP_ACTION);
    }

    private ParseResult createParseResultFromQuery(final String query) throws ParseFailureException, ParseException {
        return DataParseResult.createFromSearchQuery(query).setAction(DataParseResult.PIN_MAP_ACTION);
    }

}
