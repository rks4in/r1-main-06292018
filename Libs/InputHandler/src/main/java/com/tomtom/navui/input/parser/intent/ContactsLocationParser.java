
package com.tomtom.navui.input.parser.intent;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.input.parser.InputParser;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.cursor.AddressReader;
import com.tomtom.navui.input.parser.cursor.CursorAddressReader;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.input.parser.data.location.DataParseResult;

/**
 * Parser to parse address from Contact data
 * 
 */
public class ContactsLocationParser implements InputParser<Intent> {

    private final AppContext mAppKit;
    private AddressReader mAddressReader;

    public ContactsLocationParser(final AppContext appKit) {
        this.mAppKit = appKit;
        mAddressReader = new CursorAddressReader(appKit);
    }

    public AddressReader getAddressReader() {
        return mAddressReader;
    }

    public void setAddressReader(final AddressReader addressReader) {
        mAddressReader = addressReader;
    }

    public Context getContext() {
        final Context context = mAppKit.getSystemPort().getApplicationContext();
        return context;
    }

    @Override
    public ParseResult parse(final Intent intent) throws ParseException, ParseFailureException {

        if (intent == null || intent.getData() == null) {
            throw new IllegalArgumentException("Cannot parse intent data. Intent dont have data");
        }
        final String formatedAddress = mAddressReader.read(intent.getData());
        return DataParseResult.createFromSearchQuery(formatedAddress).setAction(DataParseResult.PIN_MAP_ACTION);
    }

    @Override
    public boolean accept(final Intent input) {
        return ContentResolver.SCHEME_CONTENT.equals(input.getScheme());
    }

}
