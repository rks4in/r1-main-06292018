package com.tomtom.navui.input.parser.intent;

import android.content.Intent;

import com.tomtom.navui.input.parser.GeoUnitHelper;
import com.tomtom.navui.input.parser.InputParser;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.input.parser.data.location.DataParseResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "./src/main/AndroidManifest.xml")
public class GoogleMapLocationParserTest {

    /**
     * @throws ParseException
     * @throws ParseFailureException
     */
    @Test
    public void testShouldGoogleMapLocationParserParseValidGeoData() throws ParseException, ParseFailureException {
        //GIVEN GoogleMapLocationParser and Intent with data for geo parser
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&daddr=Ferdinand+Bolstraat+180,+1072+LV+Amsterdam,+Holandia+@52.376382,4.8842626&panel=1&f=d&fb=1&dirflg=d&geocode=0,52.376382,4.884262&cid=0,0,14080343995521171618";
        final Intent input = GeoUnitHelper.createIntentfromUrl(geoMapUriString);
        final GoogleMapLocationParser testObject = new GoogleMapLocationParser(); // In unit test we must have only one test object
        final int expectedLatitude = (int) (52.376382 * 10e5);
        final int expectedLongitude = (int) (4.884262 * 10e5);
        final ParseResult expectedResult = DataParseResult.createFromCoordinateWithAction(
                expectedLatitude, expectedLongitude, "action://PlanRoute");
        //WHEN check if GoogleMapLocationParser parsing correctly data 
        final ParseResult result = testObject.parse(input);
        //THEN Parser should parse correctly data.
        final String message = "Data (geoUri " + geoMapUriString + ") parsed by GoogleMapLocationParser to get " + ParseResult.class.getCanonicalName() + " with coordinates (" + expectedLatitude + "," + expectedLongitude + ") ";
        assertThat(message, result, is(equalTo(expectedResult)));
    }


    /**
     * @throws ParseException
     * @throws ParseFailureException
     */
    @Test
    public void testShouldGoogleMapLocationParserParsePolishHostGeoData() throws ParseException, ParseFailureException {
        //GIVEN GoogleMapLocationParser and Intent with polish data for  geo parser
        final String geoMapUriString = "http://maps.google.pl/maps?hl=pl&daddr=Ferdinand+Bolstraat+180,+1072+LV+Amsterdam,+Holandia+@52.376382,4.8842626&panel=1&f=d&fb=1&dirflg=d&geocode=0,52.376382,4.884262&cid=0,0,14080343995521171618";
        final Intent input = GeoUnitHelper.createIntentfromUrl(geoMapUriString);
        final GoogleMapLocationParser testObject = new GoogleMapLocationParser(); // In unit test we must have only one test object
        final int expectedLatitude = (int) (52.376382 * 10e5);
        final int expectedLongitude = (int) (4.884262 * 10e5);
        final ParseResult expectedResult = DataParseResult.createFromCoordinateWithAction(
                expectedLatitude, expectedLongitude, "action://PlanRoute");
        //WHEN check if GoogleMapLocationParser parsing correctly data 
        final ParseResult result = testObject.parse(input);
        //THEN Parser should parse correctly data.
        final String message = "Data (geoUri " + geoMapUriString + ") parsed by GoogleMapLocationParser to get " + ParseResult.class.getCanonicalName() + " with coordinates (" + expectedLatitude + "," + expectedLongitude + ") ";
        assertThat(message, result, is(equalTo(expectedResult)));
    }

    @Test
    public void testShouldGoogleMapLocationParserParseValidGeocodeData() throws ParseException, ParseFailureException {
        //GIVEN GoogleMapLocationParser and Intent with geocode data
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&daddr=Ferdinand+Bolstraat+180,+1072+LV+Amsterdam,+Holandia&f=d&fb=1&dirflg=d&geocode=0,52.376382,4.884262&cid=0,0,14080343995521171618";
        final Intent input = GeoUnitHelper.createIntentfromUrl(geoMapUriString);
        final GoogleMapLocationParser testObject = new GoogleMapLocationParser(); // In unit test we must have only one test object
        final int expectedLatitude = (int) (52.376382 * 10e5);
        final int expectedLongitude = (int) (4.884262 * 10e5);
        final ParseResult expectedResult = DataParseResult.createFromCoordinateWithAction(
                expectedLatitude, expectedLongitude, "action://PlanRoute");
        //WHEN check if GoogleMapLocationParser parsing correctly data 
        final ParseResult result = testObject.parse(input);
        //THEN Parser should parse geocode data from url
        final String message = "Data (geoUri " + geoMapUriString + ") parsed by GoogleMapLocationParser to get " + ParseResult.class.getCanonicalName() + " with coordinates (" + expectedLatitude + "," + expectedLongitude + ") ";
        assertThat(message, result, is(equalTo(expectedResult)));
    }

    @Test
    public void testShouldThrowExceptionWhenParseInvalidGeocodeData() {
        //GIVEN GoogleMapLocationParser and Intent with geocode data with missing longitude 
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&f=d&fb=1&dirflg=d&geocode=0,52.376382&cid=0,0,14080343995521171618";
        final Intent input = GeoUnitHelper.createIntentfromUrl(geoMapUriString);
        final GoogleMapLocationParser testObject = new GoogleMapLocationParser(); // In unit test we must have only one test object
        //WHEN check if GoogleMapLocationParser throw exception
        Exception ex = null;
        try {
            testObject.parse(input);
        } catch (final Exception e) {
            ex = e;
        }
        //THEN Parser should parse geocode data from url
        final String message = "Parser got invalid data and should throw exception. ";
        assertThat(message, ex, is(not(nullValue())));
    }

    @Test
    public void testShouldGoogleMapLocationParserParseValidIntGeocodeData() throws ParseException, ParseFailureException {
        //GIVEN GoogleMapLocationParser and Intent with geocode data
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&daddr=Ferdinand+Bolstraat+180,+1072+LV+Amsterdam,+Holandia&f=d&fb=1&dirflg=d&geocode=0,52,4&cid=0,0,14080343995521171618";
        final Intent input = GeoUnitHelper.createIntentfromUrl(geoMapUriString);
        final GoogleMapLocationParser testObject = new GoogleMapLocationParser(); // In unit test we must have only one test object
        final int expectedLatitude = (int) (52 * 10e5);
        final int expectedLongitude = (int) (4 * 10e5);
        final ParseResult expectedResult = DataParseResult.createFromCoordinateWithAction(
                expectedLatitude, expectedLongitude, DataParseResult.PLAN_ROUTE_ACTION);
        //WHEN check if GoogleMapLocationParser parsing correctly data 
        final ParseResult result = testObject.parse(input);
        //THEN Parser should parse geocode data from url
        final String message = "Data (geoUri " + geoMapUriString + ") parsed by GoogleMapLocationParser to get " + ParseResult.class.getCanonicalName() + " with coordinates (" + expectedLatitude + "," + expectedLongitude + ") ";
        assertThat(message, result, is(equalTo(expectedResult)));
    }

    @Test
    public void testShouldGoogleMapLocationParserParseIntValidGeoData() throws ParseException, ParseFailureException {
        //GIVEN GoogleMapLocationParser and Intent with data for geo parser
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&daddr=Ferdinand+Bolstraat+180,+1072+LV+Amsterdam,+Holandia+@52,4&panel=1&f=d&fb=1&dirflg=d&geocode=0,52,4&cid=0,0,14080343995521171618";
        final Intent input = GeoUnitHelper.createIntentfromUrl(geoMapUriString);
        final GoogleMapLocationParser testObject = new GoogleMapLocationParser(); // In unit test we must have only one test object
        final int expectedLatitude = (int) (52 * 10e5);
        final int expectedLongitude = (int) (4 * 10e5);
        final ParseResult expectedResult = DataParseResult.createFromCoordinateWithAction(expectedLatitude, expectedLongitude, "action://PlanRoute");
        //WHEN check if GoogleMapLocationParser parsing correctly data 
        final ParseResult result = testObject.parse(input);
        //THEN Parser should parse correctly data.
        final String message = "Data (geoUri " + geoMapUriString + ") parsed by GoogleMapLocationParser to get " + ParseResult.class.getCanonicalName() + " with coordinates (" + expectedLatitude + "," + expectedLongitude + ") ";
        assertThat(message, result, is(equalTo(expectedResult)));
    }

    @Test
    public void testShouldGoogleMapLocationParserParseValidLocationData() throws ParseException, ParseFailureException {
        //GIVEN GoogleMapLocationParser and Intent with data for localization parser
        final String locMapUriString = "https://maps.google.com/maps?q=loc:51.753592,19.455643 (Ty)";
        final Intent input = GeoUnitHelper.createIntentfromUrl(locMapUriString);
        final GoogleMapLocationParser testObject = new GoogleMapLocationParser();
        final int expectedLatitude = (int) (51.753592 * 10e5);
        final int expectedLongitude = (int) (19.455643 * 10e5);
        final ParseResult expectedResult = DataParseResult.createFromCoordinateWithAction(expectedLatitude, expectedLongitude, "action://PlanRoute");
        //WHEN check if GoogleMapLocationParser parses the data correctly
        final ParseResult result = testObject.parse(input);
        //THEN The parser should parse the data correctly.
        final String message = "Data (locUri " + locMapUriString + ") parsed by GoogleMapLocationParser to get " + ParseResult.class.getCanonicalName() + " with coordinates (" + expectedLatitude + "," + expectedLongitude + ") ";
        assertThat(message, result, is(equalTo(expectedResult)));
    }

    @Test
    public void testShouldGoogleMapLocationParserParseValidDestinationAddressData() throws ParseException, ParseFailureException {
        //GIVEN GoogleMapLocationParser and Intent with data for destination address parser
        final String locMapUriString = "https://maps.google.com/maps?daddr=42%20Kosterijland%0ABunnik%0ANetherlands";
        final Intent input = GeoUnitHelper.createIntentfromUrl(locMapUriString);
        final GoogleMapLocationParser testObject = new GoogleMapLocationParser();
        final ParseResult expectedResult = DataParseResult.createFromSearchQuery("42 Kosterijland Bunnik Netherlands")
                .setAction("action://PlanRoute");
        //WHEN check if GoogleMapLocationParser parses the data correctly
        final ParseResult result = testObject.parse(input);
        //THEN The parser should parse the data correctly.
        final String message = "Data (locUri " + locMapUriString + ") parsed by GoogleMapLocationParser to get " + ParseResult.class.getCanonicalName() + " with address: 42 Kosterijland Bunnik Netherlands";
        assertThat(message, result, is(equalTo(expectedResult)));
    }

    @Test
    public void testOverscaleGoogleMapCoordinates() {
        //GIVEN GoogleMapLocationParser and Intent with wrong coordinates over limits
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&daddr=@2522.376382,2224.8842626&panel=1&f=d&fb=1&dirflg=d&geocode=0,2252.376382,224.884262&cid=0,0,1403380343995521171618";
        final GoogleMapLocationParser testObject = new GoogleMapLocationParser();
        Exception ex = null;

        //WHEN check if GoogleMapLocationParser throw exception
        try {
            parse(testObject, geoMapUriString);
        } catch (final ParseFailureException e) {
            ex = e;
        } catch (final ParseException e) {
            ex = e;
        }

        //THEN Parser should throw exception
        final String message = "Can not parse wrong coordinates";
        assertThat(message, ex, is(not(nullValue())));
    }

    @Test
    public void testUnderscalseGoogleMapCoordinates() {
        //GIVEN GoogleMapLocationParser and Intent with wrong coordinates with minus values
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&daddr=@-2522.376382,-2224.8842626&panel=1&f=d&fb=1&dirflg=d&geocode=-0,2252.376382,-224.884262&cid=0,0,1403380343995521171618";
        final GoogleMapLocationParser testObject = new GoogleMapLocationParser();
        Exception ex = null;

        //WHEN check if GoogleMapLocationParser throw exception
        try {
            parse(testObject, geoMapUriString);
        } catch (final ParseFailureException e) {
            ex = e;
        } catch (final ParseException e) {
            ex = e;
        }

        //THEN Parser should throw exception
        final String message = "Can not parse wrong coordinates";
        assertThat(message, ex, is(not(nullValue())));
    }

    private void parse(final InputParser<Intent> parser, final String uri) throws ParseFailureException, ParseException {
        final Intent input = GeoUnitHelper.createIntentfromUrl(uri);
        parser.parse(input);
    }
}
