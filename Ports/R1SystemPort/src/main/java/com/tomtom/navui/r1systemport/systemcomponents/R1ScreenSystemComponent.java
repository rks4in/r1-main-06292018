package com.tomtom.navui.r1systemport.systemcomponents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;

import com.google.common.base.Joiner;
import com.tomtom.navui.appkit.AppDialog;
import com.tomtom.navui.appkit.AppScreen;
import com.tomtom.navui.r1systemport.ErrorReporterConstants;
import com.tomtom.navui.r1systemport.R1DialogFragment;
import com.tomtom.navui.stocksystemport.StockSystemContext;
import com.tomtom.navui.stocksystemport.systemcomponents.screens.StockScreenSystemComponent;
import com.tomtom.navui.systemport.ErrorReporter;
import com.tomtom.navui.systemport.RootScreenProvider;
import com.tomtom.navui.util.Log;

import java.util.ArrayList;
import java.util.List;

public class R1ScreenSystemComponent extends StockScreenSystemComponent {

    private static final String TAG = "R1ScreenSystemComponent";
    private static final String FRAGMENT_TAG = "NOTAG";

    private final ErrorReporter mAppStatusReporter;
    private final OnBackStackChangedListener mBackStackListener;

    public R1ScreenSystemComponent(@NonNull final StockSystemContext systemContext,
                                       @NonNull final Context context) {
        super(systemContext, context);
        mAppStatusReporter = systemContext.getApplication().getErrorReporter();
        mBackStackListener = () -> {
            final String screensStack = getScreenStackString(assertAndGetFragmentManager(), getRootAction());
            mAppStatusReporter.setMetaData(ErrorReporterConstants.FRAGMENT_STACK, screensStack);
        };
    }

    @Override
    public void registerFragmentManager(@NonNull final FragmentManager fragmentManager,
                                        final int containerViewId) {
        fragmentManager.addOnBackStackChangedListener(mBackStackListener);
        super.registerFragmentManager(fragmentManager, containerViewId);
    }

    @Override
    public void unregisterFragmentManager(@NonNull final FragmentManager fragmentManager) {
        super.unregisterFragmentManager(fragmentManager);
        fragmentManager.removeOnBackStackChangedListener(mBackStackListener);
    }

    @Override
    public void startScreen(@NonNull final Intent intent) {
        logScreenStart(intent);
        if (intent.getCategories() != null && intent.getCategories().contains(AppDialog.DIALOG_CATEGORY)) {
            if (Log.D) Log.d(TAG, "intent has dialog category, starting dialog screen");
            getHandler().post(new StartDialogRunnable(intent));
        } else {
            super.startScreen(intent);
        }
    }

    private final class StartDialogRunnable implements Runnable {
        private final Intent mIntent;

        private StartDialogRunnable(@NonNull final Intent intent) {
            mIntent = intent;
        }

        @Override
        public void run() {
            if (Log.ENTRY) Log.entry(TAG, "startDialog action = " + mIntent.getAction());
            final FragmentManager fragmentManager = assertAndGetFragmentManager();

            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R1DialogFragment.newInstance(mIntent), FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void finishFragment(@NonNull final Fragment fragment) {
        if (fragment instanceof R1DialogFragment) {
            final FragmentManager fragmentManager = assertAndGetFragmentManager();

            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        } else {
            super.finishFragment(fragment);
        }
    }

    @Override
    public void setRootScreenProvider(@NonNull final RootScreenProvider rootScreenProvider) {
        logSetRootScreenProvider(rootScreenProvider);
        super.setRootScreenProvider(rootScreenProvider);
    }

    private void logSetRootScreenProvider(@NonNull final RootScreenProvider provider) {
        final StringBuilder sb = new StringBuilder();
        sb.append("name=").append(provider.getRootScreenName());
        final Bundle extras = provider.getExtras();
        if (extras != null) {
            sb.append(" extras=").append(extras.toString());
        }
        sb.append(" hasBackVisibility=").append(provider.getDefaultBackButtonVisibility() != null);
        sb.append(" hasMapVisibility=").append(provider.getDefaultMapButtonVisibility() != null);
        mAppStatusReporter.setMetaData(ErrorReporterConstants.CURRENT_ROOT_SCREEN_PROVIDER, sb.toString());
    }

    private void logScreenStart(@NonNull final Intent intent) {
        final StringBuilder sb = new StringBuilder();
        final Bundle extras = intent.getExtras();
        sb.append(intent.toString());
        if (extras != null) {
            sb.append(" extras=").append(extras.toString());
            if (extras.containsKey(AppScreen.FORWARD_TAG)) {
                final Intent forwardIntent = extras.getParcelable(AppScreen.FORWARD_TAG);
                final Bundle forwardExtra = forwardIntent.getExtras();
                if (forwardExtra != null) {
                    sb.append(" forwardsTo extras=").append(forwardExtra.toString());
                }
            }
        }
        mAppStatusReporter.setMetaData(ErrorReporterConstants.LAST_SCREEN_STARTED, sb.toString());
    }

    private static String getScreenStackString(@NonNull final FragmentManager fragmentManager,
                                               @NonNull final String rootAction) {
        final int count = fragmentManager.getBackStackEntryCount();
        final List<String> stackEntries = new ArrayList<>();
        stackEntries.add(rootAction);
        for (int i = 0; i < count; i++) {
            final FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(i);
            stackEntries.add(entry.getName());
        }
        return Joiner.on(ErrorReporterConstants.VALUE_SEPARATOR).join(stackEntries);
    }
}
