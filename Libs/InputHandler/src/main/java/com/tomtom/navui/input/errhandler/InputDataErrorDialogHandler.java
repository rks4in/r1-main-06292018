
package com.tomtom.navui.input.errhandler;

import android.content.Intent;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.AppDialog;
import com.tomtom.navui.appkit.ExternalDataNotSupportedDialog;
import com.tomtom.navui.appkit.ExternalLocationNotFoundDialog;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.util.Log;

/**
 * Handle error situation by display dialogs for user
 * 
 */
public class InputDataErrorDialogHandler implements InputDataErrorHandler {

    private static final String TAG = "InputDataErrorHandler";
    private final SystemContext mSystemPort;

    public InputDataErrorDialogHandler(final AppContext appContext) {
        this.mSystemPort = appContext.getSystemPort();
    }

    @Override
    public void handle(final Exception e) {
        if (e instanceof ParseException) {
            final ParseException ex = (ParseException) e;
            if (Log.E) Log.e(TAG, "We  get invalid data but we are able to parse something", e);
            showUnableToFindDialog(ex.getClosestQuery());
        } else {
            if (Log.E) Log.e(TAG, "Unable to parse input data", e);
            showCriticalErrorDialog();
        }
    }

    void showCriticalErrorDialog() {
        final Intent dataNotSupportedDialog = new Intent(ExternalDataNotSupportedDialog.class.getSimpleName());
        dataNotSupportedDialog.addCategory(AppDialog.DIALOG_CATEGORY);
        mSystemPort.startScreen(dataNotSupportedDialog);
    }

    void showUnableToFindDialog(final String query) {
        final Intent externalLocationDialog = new Intent(ExternalLocationNotFoundDialog.class.getSimpleName());
        externalLocationDialog.addCategory(AppDialog.DIALOG_CATEGORY);
        externalLocationDialog.putExtra(ExternalLocationNotFoundDialog.EXTRA_QUERY, query);
        mSystemPort.startScreen(externalLocationDialog);
    }

}
