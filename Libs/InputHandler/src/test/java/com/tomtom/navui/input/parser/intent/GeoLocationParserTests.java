
package com.tomtom.navui.input.parser.intent;

import android.content.Intent;

import com.tomtom.navui.input.parser.GeoUnitHelper;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.input.parser.data.location.DataParseResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "./src/main/AndroidManifest.xml")
public class GeoLocationParserTests {

    private static final List<String> sUnacceptableInput = new ArrayList<>();
    private static final List<String> sCorruptInput = new ArrayList<>();
    private static final Map<String, ParseResult> sParseableInput = new HashMap<>();

    static {
        // Collect inputs that should not be accepted
        sUnacceptableInput.add("google.navigation:///?ll=52.367585%2C4.893381&q=Rokin%20174%2C%201012%20LE%20Amsterdam%2C%20The%20Netherlands&entry=d&opt=4%3A0%2C5%3A0;");
        sUnacceptableInput.add("http://maps.google.com/maps?hl=pl&daddr=Ferdinand+Bolstraat+180,+1072+LV+Amsterdam,+Holandia+@52.376382,4.8842626&panel=1&f=d&fb=1&dirflg=d&geocode=0,52.376382,4.884262&cid=0,0,14080343995521171618");

        // Collect inputs that should be accepted but can't be parsed
        sCorruptInput.add("geo:");
        sCorruptInput.add("geo:?q=");
        sCorruptInput.add("geo:sddf,aafd");
        sCorruptInput.add("geo:5200.376382,4.884262");
        sCorruptInput.add("geo:52.376382,4000.884262");
        sCorruptInput.add("geo:-5200.376382,4.884262");
        sCorruptInput.add("geo:52.376382,-4000.884262");
        sCorruptInput.add("geo:52.376382,,4.884262");

        // Collect inputs that should be parsed, and their expected results
        sParseableInput.put("geo:52.376382,4.884262", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:-52.376382,-4.884262", createParseResult(-52.376382, -4.884262));
        sParseableInput.put("geo:52.376382,4.884262?z=11", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:0,0?q=52.376382,4.884262", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:0,0?q=52.376382,4.884262(Title)", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:0,0?q=52.376382,4.884262%20(Title)", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:0,0?q=52.376382,  4.884262", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:?q=52.376382,4.884262", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:0,?q=52.376382,4.884262", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:0,0?z=11&q=52.376382,4.884262", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:0,0?q=52.376382,4.884262&z=11", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:0,0?q=Oosterdoksstraat+114,+Amsterdam", createParseResult("Oosterdoksstraat 114, Amsterdam"));
        sParseableInput.put("geo:0,0?q=Oosterdoksstraat%20114,%20Amsterdam", createParseResult("Oosterdoksstraat 114, Amsterdam"));
        sParseableInput.put("geo:0,0?q=q%3DOosterdoksstraat%20114,%20Amsterdam", createParseResult("q=Oosterdoksstraat 114, Amsterdam"));
        sParseableInput.put("geo:52.376382%204.884262", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:52.376382%20%204.884262", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:0,0?q=52.376382  4.884262", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:0,0?q=52.376382 4.884262 (Title)", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:52,4", createParseResult(52, 4));
        sParseableInput.put("geo:0,0?q=52 4", createParseResult(52, 4));
        sParseableInput.put("geo:0,0?q=52 4(Title)", createParseResult(52, 4));
        sParseableInput.put("geo:0,0?q=52 4 (Title)", createParseResult(52, 4));
        sParseableInput.put("geo:0,0?q=52 4%20(Title)", createParseResult(52, 4));
        sParseableInput.put("geo:0,0?q=52 4 67 12", createParseResult("52 4 67 12"));
        sParseableInput.put("geo:52.376382%2C4.884262", createParseResult(52.376382, 4.884262));
        sParseableInput.put("geo:?z=1&q=52.376382%2C4.884262", createParseResult(52.376382, 4.884262));
    }

    private static ParseResult createParseResult(final double latitude, final double longitude) {
        try {
            final DataParseResult result = DataParseResult.createFromCoordinate((int) (latitude * 10e5), (int) (longitude * 10e5));
            result.setAction(DataParseResult.PIN_MAP_ACTION);
            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ParseResult createParseResult(final String query) {
        try {
            final DataParseResult result = DataParseResult.createFromSearchQuery(query);
            result.setAction(DataParseResult.PIN_MAP_ACTION);
            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testShouldNotAcceptInvalidInput() {
        // GIVEN a geo location parser
        final GeoLocationParser tested = new GeoLocationParser();

        // WHEN checking if the parser accepts the data
        for (final String unacceptableInput : sUnacceptableInput) {
            final Intent unacceptableIntent = GeoUnitHelper.createIntentfromUrl(unacceptableInput);
            final boolean accepted = tested.accept(unacceptableIntent);

            // THEN the parser should not accept
            assertFalse("The parser should not accept input: " + unacceptableInput, accepted);
        }
    }

    @Test
    public void testShouldThrowExceptionForCorruptInput() {
        // GIVEN a geo location parser
        final GeoLocationParser tested = new GeoLocationParser();

        // WHEN trying to parse the data
        for (final String corruptInput : sCorruptInput) {
            final Intent corruptIntent = GeoUnitHelper.createIntentfromUrl(corruptInput);
            final boolean accepted = tested.accept(corruptIntent);
            try {
                tested.parse(corruptIntent);

            } catch (final Exception e) {
                // THEN the parser should accept but throw an exception
                assertTrue("An exception was correctly thrown, but the parser didn't accept the data", accepted);
                continue;
            }

            fail("An exception should be thrown for input: " + corruptInput);
        }
    }

    @Test
    public void testShouldAcceptValidInput() {
        // GIVEN a geo location parser
        final GeoLocationParser tested = new GeoLocationParser();

        // WHEN checking if the parser accepts the data
        for (final String acceptableInput : sParseableInput.keySet()) {
            final Intent unacceptableIntent = GeoUnitHelper.createIntentfromUrl(acceptableInput);
            final boolean accepted = tested.accept(unacceptableIntent);

            // THEN the parser should accept
            assertTrue("The parser should accept input: " + acceptableInput, accepted);
        }
    }

    @Test
    public void testShouldParseValidInput() {
        // GIVEN a geo location parser
        final GeoLocationParser tested = new GeoLocationParser();

        // WHEN trying to parse the data
        for (final Map.Entry<String, ParseResult> parseableInput : sParseableInput.entrySet()) {
            final Intent parseableIntent = GeoUnitHelper.createIntentfromUrl(parseableInput.getKey());
            final ParseResult result;
            try {
                 result = tested.parse(parseableIntent);
            } catch (final Exception e) {
                fail("No exception should occur, but got: " + e);
                return;
            }

            // THEN the parser should correctly parse the input
            assertEquals("Should correctly parse input: " + parseableInput.getKey(), parseableInput.getValue(), result);
        }
    }
    
}
