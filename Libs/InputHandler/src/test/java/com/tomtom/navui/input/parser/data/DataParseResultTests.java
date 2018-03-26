
package com.tomtom.navui.input.parser.data;

import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.data.location.DataParseResult;
import com.tomtom.navui.input.parser.data.location.GeoLocationData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "./src/main/AndroidManifest.xml")
public class DataParseResultTests {

    public void testShouldParseStringToCoordinate() throws ParseException, ParseFailureException {
        //GIVEN String to parse, 
        final String toParse = "52.2121,4.787";
        //WHEN parsing 
        final DataParseResult tested = DataParseResult.createFromCoordinatePart(toParse);
        //THEN object with coordinates
        assertThat(tested, is(notNullValue()));
        assertThat(tested.getAction(), is(nullValue()));
        assertThat(tested.getParsedData(), is(instanceOf(GeoLocationData.class)));
        final GeoLocationData geoData = (GeoLocationData) tested.getParsedData();
        assertThat(geoData.getLatitude(), is(equalTo((int) (52.2121 * 10e5))));
        assertThat(geoData.getLongitude(), is(equalTo((int) (4.787 * 10e5))));
    }

    @Test
    public void testShouldParseStringToMinusCoordinate() throws ParseException, ParseFailureException {
        //GIVEN String to parse, 
        final String toParse = "-52.2121,-4.787";
        //WHEN parsing 
        final DataParseResult tested = DataParseResult.createFromCoordinatePart(toParse);
        //THEN object with coordinates
        assertThat(tested, is(notNullValue()));
        assertThat(tested.getAction(), is(nullValue()));
        assertThat(tested.getParsedData(), is(instanceOf(GeoLocationData.class)));
        final GeoLocationData geoData = (GeoLocationData) tested.getParsedData();
        assertThat(geoData.getLatitude(), is(equalTo((int) (-52.2121 * 10e5))));
        assertThat(geoData.getLongitude(), is(equalTo((int) (-4.787 * 10e5))));
    }

    @Test
    public void testShouldParseCommaSpaceSeparatedStringToCoordinate() throws ParseException, ParseFailureException {
        //GIVEN String to parse,
        final String toParse = "52.2121,  4.787";
        //WHEN parsing
        final DataParseResult tested = DataParseResult.createFromCoordinatePart(toParse);
        //THEN object with coordinates
        assertThat(tested, is(notNullValue()));
        assertThat(tested.getAction(), is(nullValue()));
        assertThat(tested.getParsedData(), is(instanceOf(GeoLocationData.class)));
        final GeoLocationData geoData = (GeoLocationData) tested.getParsedData();
        assertThat(geoData.getLatitude(), is(equalTo((int) (52.2121 * 10e5))));
        assertThat(geoData.getLongitude(), is(equalTo((int) (4.787 * 10e5))));
    }

    @Test
    public void testShouldParseSpaceSeparatedStringToCoordinate() throws ParseException, ParseFailureException {
        //GIVEN String to parse,
        final String toParse = "52.2121  4.787";
        //WHEN parsing
        final DataParseResult tested = DataParseResult.createFromCoordinatePart(toParse);
        //THEN object with coordinates
        assertThat(tested, is(notNullValue()));
        assertThat(tested.getAction(), is(nullValue()));
        assertThat(tested.getParsedData(), is(instanceOf(GeoLocationData.class)));
        final GeoLocationData geoData = (GeoLocationData) tested.getParsedData();
        assertThat(geoData.getLatitude(), is(equalTo((int) (52.2121 * 10e5))));
        assertThat(geoData.getLongitude(), is(equalTo((int) (4.787 * 10e5))));
    }

    @Test
    public void testShouldParseStringToSimpleCoordinate() throws ParseException, ParseFailureException {
        //GIVEN String to parse, 
        final String toParse = "52,4";
        //WHEN parsing 
        final DataParseResult tested = DataParseResult.createFromCoordinatePart(toParse);
        //THEN object with coordinates
        assertThat(tested, is(notNullValue()));
        assertThat(tested.getAction(), is(nullValue()));
        assertThat(tested.getParsedData(), is(instanceOf(GeoLocationData.class)));
        final GeoLocationData geoData = (GeoLocationData) tested.getParsedData();
        assertThat(geoData.getLatitude(), is(equalTo((int) (52 * 10e5))));
        assertThat(geoData.getLongitude(), is(equalTo((int) (4 * 10e5))));
    }

    @Test
    public void testShouldMacherFindInvalidData() throws ParseException, ParseFailureException {
        //GIVEN String to parse, latitude and longitude are in the wrong places
        final String toParse = "178,60";
        Exception ex = null;

        //WHEN parsing 
        try {
            DataParseResult.createFromCoordinatePart(toParse);
        } catch (final Exception e) {
            ex = e;
        }
        //THEN check if the exception is null
        assertThat(ex, is(notNullValue()));
    }

    @Test
    public void testShouldThrowExceptionWhenInvalidData() {
        //GIVEN String to parse, 
        final String toParse = "52,2121,4,787";
        //WHEN parsing 
        Exception ex = null;
        try {
            DataParseResult.createFromCoordinatePart(toParse);
        } catch (final ParseFailureException e) {
            ex = e;
        }
        //THEN object with coordinates
        assertThat(ex, is(notNullValue()));
    }

    @Test
    public void testShouldThrowExceptionWhenInvalidData2() {
        //GIVEN String to parse,
        final String toParse = "52.21214.787,";
        //WHEN parsing
        Exception ex = null;
        try {
            DataParseResult.createFromCoordinatePart(toParse);
        } catch (final ParseFailureException e) {
            ex = e;
        }
        //THEN object with coordinates
        assertThat(ex, is(notNullValue()));
    }

    @Test
    public void testShouldThrowExceptionWhenDoubleComma() {
        //GIVEN String to parse,
        final String toParse = "52.2121,,4.787";
        //WHEN parsing
        Exception ex = null;
        try {
            DataParseResult.createFromCoordinatePart(toParse);
        } catch (final ParseFailureException e) {
            ex = e;
        }
        //THEN object with coordinates
        assertThat(ex, is(notNullValue()));
    }

    @Test
    public void testShouldThrowExceptionWhenInvalidEmptyData() {
        //GIVEN String to parse, 
        final String toParse = "";
        //WHEN parsing 
        Exception ex = null;
        try {
            DataParseResult.createFromCoordinatePart(toParse);
        } catch (final ParseFailureException e) {
            ex = e;
        }
        //THEN object with coordinates
        assertThat(ex, is(notNullValue()));
    }

    @Test
    public void testShouldThrowExceptionWhenInvalidNullData() {
        //GIVEN String to parse, 
        final String toParse = null;
        //WHEN parsing 
        Exception ex = null;
        try {
            DataParseResult.createFromCoordinatePart(toParse);
        } catch (final ParseFailureException e) {
            ex = e;
        }
        //THEN object with coordinates
        assertThat(ex, is(notNullValue()));
    }

}
