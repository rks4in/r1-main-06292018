package com.tomtom.navui.input.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.input.parser.intent.ContactsLocationParser;
import com.tomtom.navui.input.parser.intent.GeoLocationParser;
import com.tomtom.navui.input.parser.intent.GoogleMapLocationParser;
import com.tomtom.navui.input.parser.intent.NavigationParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test case for parser matcher
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "./src/main/AndroidManifest.xml")
public class ParserChainOfResponsibilityTests {

    @Test
    public void testGeoLocationIntentMatcher() {
        //GIVEN intent to be parse
        final String dataToParse = "geo:0,0?q=1053%20Oud-West%2C%20The%20Netherlands%208.1%20m";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final Class<?> expected = GeoLocationParser.class;

        //WHEN find parser from the available parsers
        final Class<?> actual = find(intent);

        //THEN check if chose proper parser
        assertEquals(expected, actual);
    }

    @Test
    public void testGoogleMapLocationIntentmatcher() {
        //GIVEN intent to be parse
        final String dataToParse = "http://maps.google.com/maps?hl=pl&daddr=Ferdinand+Bolstraat+180,+1072+LV+Amsterdam,+Holandia+@52.376382,4.8842626&panel=1&f=d&fb=1&dirflg=d&geocode=0,52.376382,4.884262&cid=0,0,14080343995521171618";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final Class<?> expected = GoogleMapLocationParser.class;

        //WHEN find parser from the available parsers
        final Class<?> actual = find(intent);

        //THEN check if chose proper parser
        assertEquals(expected, actual);
    }

    @Test
    public void testNavigationIntentMatcher() {
        //GIVEN intent to be parse
        final String dataToParse = "google.navigation:///?ll=52.367585%2C4.893381&q=Rokin%20174%2C%201012%20LE%20Amsterdam%2C%20The%20Netherlands&entry=d&opt=4%3A0%2C5%3A0";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final Class<?> expected = NavigationParser.class;

        //WHEN find parser from the available parsers
        final Class<?> actual = find(intent);

        //THEN check if chose proper parser
        assertEquals(expected, actual);
    }

    @Test
    public void testContactsIntentMatcher() {
        //GIVEN intent to be parse
        final String dataToParse = "content://com.android.contacts/data/000";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);
        final Class<?> expected = ContactsLocationParser.class;

        //WHEN find parser from the available parsers
        final Class<?> actual = find(intent);

        //THEN check if chose proper parser
        assertEquals(expected, actual);
    }

    @Test
    public void testIntentMatcherNoMatch() {
        //GIVEN intent to be parse
        final String dataToParse = "sdgfs:fe34:4343,feewg4,54645,dumy";
        final Intent intent = GeoUnitHelper.createIntentfromUrl(dataToParse);

        //WHEN find parser from the available parsers
        final Class<?> actual = find(intent);

        //THEN check if chose proper parser
        final String message = "This URI should not to be match by any parser";
        assertThat(message, actual, is(nullValue()));
    }

    private static Class<?> find(final Intent intent) {
        final AppContext appContext = mock(AppContext.class);
        final List<InputParser<Intent>> intentParsers = new ArrayList<>();
        intentParsers.add(new GeoLocationParser());
        intentParsers.add(new GoogleMapLocationParser());
        intentParsers.add(new NavigationParser());
        intentParsers.add(new ContactsLocationParser(appContext));

        for (final InputParser<Intent> parser : intentParsers) {
            if (parser.accept(intent)) {
                return parser.getClass();
            }
        }

        return null;
    }
}
