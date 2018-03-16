
package com.tomtom.navui.input.parser.cursor;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.util.Log;

public class CursorAddressReader implements AddressReader {

    private static final String NEW_LINE_CHARACTER_R = "\r";
    private static final String NEW_LINE_CHARACTER = "\n";
    private static final String TAG = "CursorAdressReader";
    private final AppContext mAppKit;

    public CursorAddressReader(final AppContext appKit) {
        this.mAppKit = appKit;
    }

    public Context getContext() {
        final Context context = mAppKit.getSystemPort().getApplicationContext();
        return context;
    }

    @Override
    public String read(final Uri data) throws ParseException, ParseFailureException {
        final Cursor cursor = getContext().getContentResolver().query(data, null, null, null, null);
        try {
            if ((cursor == null) || (cursor.getCount() == 0)) {
                throw new ParseFailureException("Cannot parse intent data. Query has returned null or empty cursor");
            }
            // we get cursor Uri to specific address, we expect that we get only
            // 1 record.
            cursor.moveToFirst();
            final String address = cursor
                .getString(cursor
                    .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
            if (Log.D) Log.d(TAG, "formatted address " + address);
            if (TextUtils.isEmpty(address)) {
                throw new ParseException("Address loaded from Contact Provider is empty", "");
            }
            return removeEnters(address);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String removeEnters(final String address) {
        return address.replace(NEW_LINE_CHARACTER, " ").replace(NEW_LINE_CHARACTER_R, " ");
    }

}
