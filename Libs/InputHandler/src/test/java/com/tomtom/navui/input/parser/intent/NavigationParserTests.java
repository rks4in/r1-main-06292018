
package com.tomtom.navui.input.parser.intent;

import android.content.Intent;

import com.tomtom.navui.input.parser.GeoUnitHelper;
import com.tomtom.navui.input.parser.InputParser;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.input.parser.data.location.DataParseResult;
import com.tomtom.navui.input.parser.data.location.GeoLocationData;
import com.tomtom.navui.input.parser.data.location.TextLocationData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.tomtom.navui.utest.matcher.LocalizationMatcher.validLocation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for navigation parser
 * <p>
 * google.navigation:///?ll=52.367585%2C4.893381&q=Rokin%20174%2C%201012%20LE%20
 * Amsterdam%2C%20The%20Netherlands&entry=d&opt=4%3A0%2C5%3A0
 * </p>
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "./src/main/AndroidManifest.xml")
public class NavigationParserTests {

    @Test
    public void testShouldParseNavigationLink() throws ParseException, ParseFailureException {
        //GIVEN Parser and data and valid data to parse
        final String dataToParse = "google.navigation:///?ll=52.367585%2C4.893381&q=Rokin%20174%2C%201012%20LE%20Amsterdam%2C%20The%20Netherlands&entry=d&opt=4%3A0%2C5%3A0";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final NavigationParser tested = new NavigationParser();
        final int expectedLatitude = (int) (52.367585 * 10e5);
        final int expectedLongitude = (int) (4.893381 * 10e5);
        //WHEN parsing
        final ParseResult result = tested.parse(intent);
        //THEN check if parsed properly. 
        assertThat(result, is(instanceOf(DataParseResult.class)));
        final DataParseResult castResult = (DataParseResult) result;
        assertThat(castResult.getParsedData(), is(instanceOf(GeoLocationData.class)));
        final GeoLocationData gld = (GeoLocationData) castResult.getParsedData();
        assertThat(gld.getLatitude(), is(equalTo(expectedLatitude)));
        assertThat(gld.getLongitude(), is(equalTo(expectedLongitude)));
    }

    @Test
    public void testShouldParseSimpleNavigationLinkLodz() throws ParseException, ParseFailureException {
        // GIVEN Parser and data and valid data to parse
        final String dataToParse = "google.navigation:///?ll=51.561376%2C19.478317&q=Niedas%20Le%C5%9Bny%204%2C%2095-080%20Gmina%20Tuszyn&entry=d&opt=4%3A0%2C5%3A0";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final NavigationParser tested = new NavigationParser();
        final int expectedLatitude = (int) (51.561376 * 10e5);
        final int expectedLongitude = (int) (19.478317 * 10e5);
        // WHEN parsing
        final ParseResult result = tested.parse(intent);
        // THEN check if parsed properly.
        assertThat(result, is(instanceOf(DataParseResult.class)));
        final DataParseResult castResult = (DataParseResult) result;
        assertThat(castResult.getParsedData(), is(instanceOf(GeoLocationData.class)));
        final GeoLocationData gld = (GeoLocationData) castResult.getParsedData();
        assertThat(gld.getLatitude(), is(equalTo(expectedLatitude)));
        assertThat(gld.getLongitude(), is(equalTo(expectedLongitude)));
        assertThat(gld, is(validLocation()));
    }

    @Test
    public void testShouldParseSimpleNavigationLink() throws ParseException, ParseFailureException {
        //GIVEN Parser and data and valid data to parse
        final String dataToParse = "google.navigation:///?ll=52%2C4";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final NavigationParser tested = new NavigationParser();
        final int expectedLatitude = (int) (52 * 10e5);
        final int expectedLongitude = (int) (4 * 10e5);
        //WHEN parsing
        final ParseResult result = tested.parse(intent);
        //THEN check if parsed properly. 
        assertThat(result, is(instanceOf(DataParseResult.class)));
        final DataParseResult castResult = (DataParseResult) result;
        assertThat(castResult.getParsedData(), is(instanceOf(GeoLocationData.class)));
        final GeoLocationData gld = (GeoLocationData) castResult.getParsedData();
        assertThat(gld.getLatitude(), is(equalTo(expectedLatitude)));
        assertThat(gld.getLongitude(), is(equalTo(expectedLongitude)));
    }

    @Test
    public void testShouldParseInvalidNavigationLink() throws ParseException {
        //GIVEN Parser and data and valid data to parse
        final String dataToParse = "google.navigation:///?ll=52.3er%2C4ew";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final NavigationParser tested = new NavigationParser();
        Exception ex = null;
        //WHEN parsing
        try {
            tested.parse(intent);
        } catch (final ParseFailureException e) {
            ex = e;
        }
        //THEN check if parsed properly. 
        assertThat(ex, is(notNullValue()));
    }

    @Test
    public void testShouldParseFreeTextUrl() throws ParseException, ParseFailureException {
        //GIVEN url without coordinate, with text only and parser to parse such data. 
        final String dataToParse = "google.navigation:///?q=Rokin%20174%2C%201012%20LE%20Amsterdam%2C%20The%20Netherlands&entry=d&opt=4%3A0%2C5%3A0";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final String expectedLocationQuery = "Rokin 174, 1012 LE Amsterdam, The Netherlands";
        final NavigationParser tested = new NavigationParser();
        //WHEN parse
        final ParseResult result = tested.parse(intent);
        //THEN Text with address 
        assertThat(result, is(instanceOf(DataParseResult.class)));
        final DataParseResult castResult = (DataParseResult) result;
        assertThat(castResult.getParsedData(), is(instanceOf(TextLocationData.class)));
        final TextLocationData tld = (TextLocationData) castResult.getParsedData();
        assertThat(tld.getTextLocation(), is(notNullValue()));
        assertThat(tld.getTextLocation(), not(isEmptyOrNullString()));
        assertThat(tld.getTextLocation(), is(equalTo(expectedLocationQuery)));
    }

    @Test
    public void testShouldParseFreeTextHertfordshireUrl() throws ParseException, ParseFailureException {
        //GIVEN url without coordinate, with text only and parser to parse such data. 
        final String dataToParse = "google.navigation:///?q=N%20Orbital%20Rd%2C%20Saint%20Albans%2C%20Hertfordshire%20AL4%2C%20UK&entry=d&opt=4%3A0%2C5%3A0";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final String expectedLocationQuery = "N Orbital Rd, Saint Albans, Hertfordshire AL4, UK";
        final NavigationParser tested = new NavigationParser();
        //WHEN parse
        final ParseResult result = tested.parse(intent);
        //THEN Text with address 
        assertThat(result, is(instanceOf(DataParseResult.class)));
        final DataParseResult castResult = (DataParseResult) result;
        assertThat(castResult.getParsedData(), is(instanceOf(TextLocationData.class)));
        final TextLocationData tld = (TextLocationData) castResult.getParsedData();
        assertThat(tld.getTextLocation(), is(notNullValue()));
        assertThat(tld.getTextLocation(), not(isEmptyOrNullString()));
        assertThat(tld.getTextLocation(), is(equalTo(expectedLocationQuery)));
    }

    @Test
    public void testShouldParseLinkThatAreNotHierarhicalFreeText() throws ParseException, ParseFailureException {
        //GIVEN this url doesn't have ///? in front, is not hierarchical
        //i got it tom HTC one device for location in London
        final String dataToParse = "google.navigation:title=Dunheved Close&entry=d&opt=4:0,5:0";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final NavigationParser tested = new NavigationParser();

        //WHEN parse
        final ParseResult result = tested.parse(intent);

        //THEN Text with address 
        final DataParseResult castResult = (DataParseResult) result;
        final TextLocationData tld = (TextLocationData) castResult.getParsedData();
        assertThat(tld.getTextLocation(), is(notNullValue()));
    }

    @Test
    public void testShouldParseLinkThatAreNotHierarhical() throws ParseException, ParseFailureException {
        //GIVEN this url doesn't have ///? in front, is not hierarchical
        //i got it tom HTC one device for location in London 
        final String dataToParse = "google.navigation:ll=51.390634,-0.115447&title=Dunheved Close&entry=d&opt=4:0,5:0";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final NavigationParser tested = new NavigationParser();
        Exception ex = null;

        //WHEN parse
        try {
            tested.parse(intent);
        } catch (final Exception e) {
            ex = e;
        }

        //THEN ex should not be null
        assertThat(ex, is(nullValue()));
    }

    @Test
    public void testOverscaleNavigationCoordinates() {
        //GIVEN NavigationParser and Intent with wrong coordinates over limits
        final String dataToParse = "google.navigation:///?ll=2522.367585%2C2224.893381&q=&entry=d&opt=4%3A0%2C5%3A0";
        final NavigationParser testObject = new NavigationParser();
        Exception ex = null;

        //WHEN check if NavigationParser throw exception
        try {
            parse(testObject, dataToParse);
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
    public void testUnderscaleNavigationCoordinates() {
        //GIVEN NavigationParser and Intent with wrong coordinates with minus values
        final String dataToParse = "google.navigation:///?ll=-2223.367585%2C-24.893381&q=&entry=d&opt=4%3A0%2C5%3A0";
        final NavigationParser testObject = new NavigationParser();
        Exception ex = null;

        //WHEN check if NavigationParser throw exception
        try {
            parse(testObject, dataToParse);
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
