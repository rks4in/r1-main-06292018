
package com.tomtom.navui.utest.matcher;

import com.tomtom.navui.input.parser.data.location.GeoLocationData;
import com.tomtom.navui.taskkit.route.Wgs84Coordinate;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class LocalizationMatcher extends BaseMatcher<GeoLocationData> {

    @Override
    public boolean matches(final Object obj) {
        if (obj instanceof GeoLocationData) {
            final GeoLocationData data = (GeoLocationData) obj;
            if (data.getLatitude() > Wgs84Coordinate.LATITUDE_MIN && data.getLatitude() < Wgs84Coordinate.LATITUDE_MAX
                    && data.getLongitude() > Wgs84Coordinate.LONGITUDE_MIN && data.getLongitude() < Wgs84Coordinate.LONGITUDE_MAX) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(" is a valid Location");
    }

    public static LocalizationMatcher validLocation() {
        return new LocalizationMatcher();
    }
}
