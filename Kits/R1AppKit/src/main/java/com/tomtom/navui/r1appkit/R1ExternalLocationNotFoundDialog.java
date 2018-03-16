package com.tomtom.navui.r1appkit;

import android.content.Context;
import android.os.Bundle;

import com.tomtom.navui.appkit.ExternalLocationNotFoundDialog;
import com.tomtom.navui.sigappkit.SigAppContext;
import com.tomtom.navui.systemport.systemcomponents.DialogSystemComponent;
import com.tomtom.navui.systemport.systemcomponents.dialogs.SystemDialog;
import com.tomtom.navui.systemport.systemcomponents.dialogs.SystemDialog.OnClickListener;
import com.tomtom.navui.systemport.systemcomponents.dialogs.SystemDialogBuilder;
import com.tomtom.navui.util.Theme;

public class R1ExternalLocationNotFoundDialog extends R1AppDialog implements ExternalLocationNotFoundDialog, OnClickListener {

    public R1ExternalLocationNotFoundDialog(SigAppContext context) {
        super(context);
    }
    @Override
    public SystemDialog onCreateDialog(Context context, Bundle savedInstanceState) {
        // Just a quick hack. Not translations.
        final SystemDialogBuilder builder = getContext().getSystemPort().getComponent(DialogSystemComponent.class).createBuilder();
        builder.setTitle("Error");
        builder.setMessage("The location can not be found!");
        builder.setNeutralButton("OK", this);
        return builder.build();
    }
    @Override
    public void onClick(SystemDialog dialog, int which) {
        dialog.cancel();
    }
    @Override
    public boolean isCancelable() {
        return false;
    }

}
