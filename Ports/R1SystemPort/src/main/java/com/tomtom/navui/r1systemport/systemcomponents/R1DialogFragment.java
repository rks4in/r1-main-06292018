package com.tomtom.navui.r1systemport;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.AppDialog;
import com.tomtom.navui.appkit.AppScreen;
import com.tomtom.navui.appkit.DialogResultListener;
import com.tomtom.navui.stocksystemport.StockActivity;
import com.tomtom.navui.stocksystemport.StockFragment;
import com.tomtom.navui.stocksystemport.systemcomponents.screens.InternalScreenSystemComponent;
import com.tomtom.navui.stocksystemport.systemcomponents.dialogs.StockSystemDialog;
import com.tomtom.navui.systemport.SystemApplication;
import com.tomtom.navui.systemport.SystemDialogFragment;
import com.tomtom.navui.systemport.systemcomponents.dialogs.SystemDialog;
import com.tomtom.navui.util.Log;

public class R1DialogFragment extends Fragment implements SystemDialogFragment, SystemDialog.OnCancelListener {

    private static final String TAG = "R1DialogFragment";

    protected static final String ARGUMENT_FRAGMENT_SCREEN = "fragment-screen";
    protected static final String ARGUMENT_SCREEN_BUNDLE = "screen-argument-bundle";
    protected static final String ARGUMENT_CALLED_FROM_SCREEN = "called-from-screen";

    private SystemApplication mSystemApplication;
    private InternalScreenSystemComponent mScreenSystemComponent;
    private AppDialog mAppDialog;
    private StockSystemDialog mSystemDialog;

    private StockFragment mResultListenerFragment;

    @SuppressLint("WrongConstant")
    public static R1DialogFragment newInstance(final Intent intent) {
        final R1DialogFragment fragment = new R1DialogFragment();
        final Bundle b = new Bundle();
        b.putBundle(ARGUMENT_SCREEN_BUNDLE, intent.getExtras());
        b.putString(ARGUMENT_FRAGMENT_SCREEN, intent.getAction());
        final boolean calledFromScreen = ((intent.getFlags() & AppDialog.FLAG_DIALOG_CALLED_FROM_SCREEN) == AppDialog.FLAG_DIALOG_CALLED_FROM_SCREEN);
        b.putBoolean(ARGUMENT_CALLED_FROM_SCREEN, calledFromScreen);
        fragment.setArguments(b);
        return fragment;
    }
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        if (Log.ENTRY) Log.entry(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if (args != null) {
            final Bundle screenArgs = args.getBundle(ARGUMENT_SCREEN_BUNDLE);
            final String screenName = args.getString(ARGUMENT_FRAGMENT_SCREEN);
            if (!TextUtils.isEmpty(screenName)) {
                if (Log.D) {
                    Log.d(TAG, "creating fragment screen in: " + screenName);
                }
                // ask appkit for something to use
                final AppContext appKit = mSystemApplication.getAppKit();
                mAppDialog = appKit.newScreen(screenName);
                mSystemApplication.releaseAppKit(appKit);
                mAppDialog.setArguments(screenArgs);
                mAppDialog.setFragment(this);

                mAppDialog.onCreate(savedInstanceState);

                mSystemDialog = (StockSystemDialog) mAppDialog.onCreateDialog(getContext(), savedInstanceState);
                mSystemDialog.setOnCancelListener(this);
                mSystemDialog.show();
            } else {
                throw new IllegalStateException("No screen parameter in fragment arguments");
            }

            if (args.getBoolean(ARGUMENT_CALLED_FROM_SCREEN)) {
                /* TODO: Currently sending data from dialogs is supported only by fragments/screens.
                 * If there are other entities that start a dialog (like actions or mobile hooks)
                 * and we want to get result back, we will have to think about adding an additional
                 * callback. */
                final Fragment parentFragment = getFragmentManager().findFragmentById(R.id.navui_stock_fragment_container);
                if (parentFragment == null) {
                    throw new IllegalStateException("Dialog called from screen but no parent fragment exists");
                }
                mResultListenerFragment = (StockFragment) parentFragment;
            }

        } else {
            throw new IllegalStateException("No arguments passed to fragment");
        }
    }
    
    /**
     * Use this method to send result to target fragment - call before dismissing dialog
     */
    public void setResult(final int resultCode, final Bundle data) {
        if (mResultListenerFragment == null) {
            if (Log.D) Log.d(TAG, "Dialog does not have a result listener - ignoring result");
            return;
        }

        final AppScreen parentScreen = mResultListenerFragment.getAppScreen();
        if (parentScreen != null) {
            if (!(parentScreen instanceof DialogResultListener)) {
                throw new IllegalStateException("Dialog called from screen but parent fragment's screen is not a DialogResultListener"
                        + " (Screen: " + parentScreen.getName() + ")");
            }

            ((DialogResultListener) parentScreen).onDialogResult(resultCode, data);

        } else {
            Bundle arguments = mResultListenerFragment.getScreenArguments();
            if (arguments == null) {
                arguments = new Bundle();
            }
            arguments.putInt(DialogResultListener.ARGUMENT_RESULT_CODE, resultCode);
            arguments.putBundle(DialogResultListener.ARGUMENT_DATA, data);
            mResultListenerFragment.onArgumentsUpdated(arguments);
        }
    }

    @Override
    public void onAttach(final Context context) {
        if (Log.ENTRY) Log.entry(TAG, "onAttach");
        super.onAttach(context);
        attachContext(context);
    }

    private void attachContext(final Context context) {
        if (mSystemApplication == null) {
            mSystemApplication = ((SystemApplication) context.getApplicationContext());
            final AppContext appKit = mSystemApplication.getAppKit();
            mScreenSystemComponent = appKit.getSystemPort().getComponent(InternalScreenSystemComponent.class);
            mSystemApplication.releaseAppKit(appKit);
        }
    }

    @Override
    public void onDetach() {
        if (Log.ENTRY) Log.entry(TAG, "onDetach");

        super.onDetach();

        mSystemApplication = null;
    }

    @Override
    public void finish() {
        mSystemDialog.cancel();
    }
    
    @Override
    public void onCancel(@NonNull final SystemDialog dialog) {
        if (Log.ENTRY) Log.entry(TAG, "onCancel");

        mSystemDialog.setOnCancelListener(null);
        mSystemDialog = null;

        if (mAppDialog != null) {
            mAppDialog.onCancel();
        }

        mScreenSystemComponent.finishFragment(this);
    }
    
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        if (Log.ENTRY) Log.entry(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (mAppDialog != null) {
            mAppDialog.onSaveInstanceState(outState);
        }
    }
    
    @Override
    public void onDestroy() {
        if (Log.ENTRY) Log.entry(TAG, "onDestroy");
        super.onDestroy();
        if (mAppDialog != null) {
            mAppDialog.onDestroy();
        }
        mResultListenerFragment = null;

        if (mSystemDialog != null) {
            mSystemDialog.setOnCancelListener(null);
            mSystemDialog.cancel();
        }
    }

    @Override
    public boolean isChangingConfiguration() {
        final StockActivity activity = (StockActivity) getActivity();
        return (activity != null && activity.isChangingConfiguration());
    }

    @Override
    public void onArgumentsUpdated(final Bundle newArguments) {}
    
    @Override
    public boolean onBackPressed() {
        if (Log.ENTRY) Log.entry(TAG, "onBackPressed");
        return false;
    }
    
    @Override
    public void onPause() {
        if (Log.ENTRY) Log.entry(TAG, "onPause");
        super.onPause();
        
        if (mAppDialog != null) {
            mAppDialog.onPause();
        }
    }
    
    @Override
    public void onResume() {
        if (Log.ENTRY) Log.entry(TAG, "onResume");
        
        super.onResume();
        if (mAppDialog != null) {
            mAppDialog.onResume();
        }
    }

}
