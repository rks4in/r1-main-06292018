package com.tomtom.navui.appkit;

import android.content.Context;
import android.os.Bundle;

import com.tomtom.navui.systemport.systemcomponents.dialogs.SystemDialog;

public interface AppDialog extends AppScreen {

    /**
     * This category is set on intents that are supposed to start dialog fragment screens.
     */
    public static final String DIALOG_CATEGORY = "com.tomtom.navui.appkit.category.DialogScreen";
    
    /**
     * Use this flag if dialog is called from screen and screen is expecting
     * data to be sent back from dialog via
     * {@link AppScreen#onDialogResult(int, Bundle)} api.
     * <p>
     * If this flag is used, screen that was displayed when dialog started will
     * receive notifications. It is expected that active screen does not get
     * dismissed while dialog is displayed when this flag is used.
     */
    public static final int FLAG_DIALOG_CALLED_FROM_SCREEN = 0x01000000;
    
    /**
     * {@link android.app.DialogFragment#onCreateDialog(Bundle)
     * @param context
     * @param savedInstanceState
     * @return
     */
    public SystemDialog onCreateDialog(Context context, Bundle savedInstanceState);
    
    /**
     * Set result of dialog to be passed back to screen that started dialog.
     * This method is not related with {@link #setResults(Bundle)} - thats used
     * to pass results in screen to screen flows. This method does not dismiss
     * dialog. Closing dialog should be handled explicitly in AppDialog
     * implementation code.
     * 
     * @param resultCode
     * @param data
     */
    public void setDialogResult(int resultCode, Bundle data);
    
    /**
     * Callback called when dialog gets cancelled - override this instead of
     * setting dialog cancel listener directly to NotificationDialog.
     */
    public void onCancel();
    
    /**
     * Returns whether AppDialog is cancelable. Override this method as setting
     * this in NotificationDialog directly will have no effect when using
     * AppDialog
     * 
     * @return true by default
     */
    public boolean isCancelable();
}
