package com.tomtom.navui.r1appkit;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.AppDialog;
import com.tomtom.navui.appkit.action.Action;
import com.tomtom.navui.appkit.controllers.utils.ChromeState;
import com.tomtom.navui.sigappkit.SigAppContext;
import com.tomtom.navui.systemport.SystemDialogFragment;
import com.tomtom.navui.systemport.SystemFragment;
import com.tomtom.navui.util.Log;

public abstract class R1AppDialog implements AppDialog {

    private static final String TAG = "R1AppDialog";
    private final SigAppContext mContext;
    private SystemDialogFragment mFragment;
    private Bundle mArguments;
    
    public R1AppDialog(final SigAppContext context) {
        if (Log.ENTRY) {
            Log.entry(TAG, "Constructor - " + this);
        }
        mContext = context;
    }
    
    @Override
    public AppContext getContext() {
        return mContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onDestroyView() {
        //not used
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void setArguments(Bundle args) {
        mArguments = args;
    }

    @Override
    public Bundle getArguments() {
        if (null != mArguments) {
            return (Bundle)mArguments.clone();
        }
        return null;
    }

    @Override
    public String getBackToken() {
        // not used
        return null;
    }

    @Override
    public void setBackToken(String backToken) {
        // not used
    }

    @Override
    public void finish() {
        mFragment.finish();
    }

    @Override
    public void setResultsAction(Uri actionUri, AutoActionParameters parameters) {  
        throw new UnsupportedOperationException("setting result action is not supported");
        //not used
    }

    @Override
    public void setResults(Bundle bundle) {
        throw new UnsupportedOperationException("use setDialogResult");
        //use setDialogResult
    }

    @Override
    public final boolean onBackPressed() {
        return false;
    }

    @Override
    public final boolean onLeftPressed() {
        return false;
    }

    @Override
    public final boolean onRightPressed() {
        //not used
        return false;
    }

    @Override
    public final boolean onUpPressed() {
        //not used
        return false;
    }

    @Override
    public final boolean onDownPressed() {
        //not used
        return false;
    }

    @Override
    public final void triggerAutoAction() {
        //not used
        
    }

    @Override
    public final void onAutoAction(Action action) {
        //not used
        
    }

    @Override
    public final void onPrepareNewScreen() {
        //not used
    }

    @Override
    public void setFragment(SystemFragment fragment) {
        mFragment = (SystemDialogFragment) fragment;
        
    }

    @Override
    public final void updateArguments(Bundle arguments) {
        //not used yet ?
    }

    @Override
    public String getName() {
        //not used yet ?
        return null;
    }

    @Override
    public void setFlags(int flags) {
        //not used
    }

    @Override
    public int getFlags() {
        return 0;
    }

    @Override
    public final void onChromeState(ChromeState state) {
        //not used - dialogs are shown on top of chrome.
        throw new UnsupportedOperationException("setting chrome state is not supported from dialog");
    }

    @Override
    public void setDialogResult(int resultCode, Bundle data) {
        mFragment.setResult(resultCode, data);
    }

    @Override
    public void onCancel() {
    }
    
    @Override
    public boolean isCancelable() {
        return true;
    }
    
    /**
     * Call on screen tear down to check whether or not the tear down is due to the configuration changing.
     * @return true if the configuration is changing, otherwise false
     */
    protected boolean isChangingConfiguration() {
        return mFragment.isChangingConfiguration();
    }
    
    @Override
    public void onFullyDisplayed() { 
    }
}
