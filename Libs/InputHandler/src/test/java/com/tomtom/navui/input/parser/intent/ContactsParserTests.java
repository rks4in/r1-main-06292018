
package com.tomtom.navui.input.parser.intent;

import android.content.ContentProvider;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.input.parser.GeoUnitHelper;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.cursor.CursorAddressReader;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.systemport.SystemContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test case for ContactParser
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "./src/main/AndroidManifest.xml")
public class ContactsParserTests {

    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule();
    @Mock
    private ContentProvider mContentProvider;
    @Mock
    private AppContext mAppContext;
    @Mock
    private SystemContext mSystemContext;

    @Before
    public void setUp() throws OperationApplicationException {

        when(mAppContext.getSystemPort()).thenReturn(mSystemContext);
        when(mSystemContext.getApplicationContext()).thenReturn(RuntimeEnvironment.application);

        ShadowContentResolver.registerProviderInternal(ContactsContract.AUTHORITY, mContentProvider);
    }

    @Test
    public void shouldParseDataCorrectly() throws ParseFailureException, ParseException {
        //GIVEN existing contact to be sure
        final Uri testContentUri = Uri.parse("content://com.android.contacts/data/8806");
        final Intent intent = GeoUnitHelper.createIntentfromUrl(testContentUri.toString());
        final Cursor cursor = mock(Cursor.class);
        when(mContentProvider.query(eq(testContentUri), nullable(String[].class),
                nullable(String.class), nullable(String[].class), nullable(String.class)))
                .thenReturn(cursor);
        when(cursor.getCount()).thenReturn(1);
        when(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)).thenReturn(5);
        when(cursor.getString(5)).thenReturn("Gee Street\\nLondon\\nUnited Kingdom");

        final ContactsLocationParser testObject = new ContactsLocationParser(mAppContext);
        testObject.setAddressReader(new CursorAddressReader(mAppContext));

        //WHEN try to parse intent
        final ParseResult result = testObject.parse(intent);

        //THEN
        assertNotNull(result);
    }
}
