
package com.tomtom.navui.input.parser.data;

import android.net.Uri;

public interface ParseResult {

    Uri getAction();

    ParsedData getParsedData();

    /**
     * @return False if the action should be executed instantly on the current thread, true if it
     * should be posted to be ran on the same thread after the current task has concluded
     */
    boolean isAsynchronous();

}
