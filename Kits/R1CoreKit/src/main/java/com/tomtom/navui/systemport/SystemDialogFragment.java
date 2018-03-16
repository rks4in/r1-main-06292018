package com.tomtom.navui.systemport;

import android.os.Bundle;

public interface SystemDialogFragment extends SystemFragment {

    /**
     * This method allows screen to set result of currently running fragment. <p> 
     * Currently this is handled only in dialog fragments.
     * @param resultCode
     * @param result
     */
    public void setResult(int resultCode, Bundle result);

}
