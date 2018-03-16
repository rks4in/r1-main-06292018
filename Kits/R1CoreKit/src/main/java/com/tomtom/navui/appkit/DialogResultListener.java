package com.tomtom.navui.appkit;

import android.os.Bundle;

/**
 * Interface implemented by screens
 */
public interface DialogResultListener {

    /** The key for the argument containing the dialog result's result code */
    String ARGUMENT_RESULT_CODE = "dialog-result-result-code";
    /** The key for the argument containing the dialog result's data */
    String ARGUMENT_DATA = "dialog-result-data";

    /**
     * Called by the framework if screen has started app dialog that sends some result back.
     */
    void onDialogResult(int resultCode, Bundle data);

}
