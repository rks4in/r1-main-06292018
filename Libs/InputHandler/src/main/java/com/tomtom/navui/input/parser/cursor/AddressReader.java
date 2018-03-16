
package com.tomtom.navui.input.parser.cursor;

import android.net.Uri;

import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;

/**
 * AddressReader load address information from ContactProvider.
 * 
 */
public interface AddressReader {

    /**
     * Address to data for ContactProvider
     * 
     * @param uri Uri with address for ContactProvider
     * @return formated address
     * @throws ParseFailureException when address reader have problem to read address from phone
     *             memory.
     * @throws ParseException when we able to load address, but address looks on broken
     */
    String read(Uri uri) throws ParseException, ParseFailureException;

}
