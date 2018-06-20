package com.tomtom.navui.r1systemport.permissions;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.systemport.systemcomponents.PermissionSystemComponent;
import com.tomtom.navui.systemport.systemcomponents.permissions.PermissionGrantedListener;
import com.tomtom.navui.systemport.systemcomponents.permissions.PermissionRequest;
import com.tomtom.navui.systemport.systemcomponents.permissions.PermissionResultListener;
import com.tomtom.navui.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Wrapper class for a {@link com.tomtom.navui.systemport.SystemContext.OnReadyListener},
 * which requests the required permissions before calling the wrapped listener.
 */
public class RequestPermissionOnReadyListenerWrapper implements SystemContext.OnReadyListener {
    private static final String TAG = "RequestPermissionOnReadyListenerWrapper";

    static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    private final List<CustomPermissionGrantedListener> mPermissionGrantedListeners = new CopyOnWriteArrayList<>();
    private final SystemContext.OnReadyListener mListener;

    private SystemContext mSystemContext;
    private PermissionSystemComponent mPermissionSystemComponent;

    private boolean mPermissionsRequested;
    private boolean mDismissed;
    private boolean mListenerCalled;

    private PermissionRequest mCurrentPermissionRequest;

    public RequestPermissionOnReadyListenerWrapper(final SystemContext.OnReadyListener listener) {
        mListener = listener;
    }

    public SystemContext.OnReadyListener getWrappedListener() {
        return mListener;
    }

    /**
     * Dismisses the wrapper. This will ignore any callbacks and unregister all listeners
     */
    public void dismiss() {
        if (Log.D) Log.d(TAG, "dismiss()");

        mDismissed = true;

        if (mCurrentPermissionRequest != null) {
            mCurrentPermissionRequest.cancel();
        }

        for (final CustomPermissionGrantedListener permissionGrantedListener : mPermissionGrantedListeners) {
            permissionGrantedListener.unregister();
        }
    }

    @Override
    public void onReady(final SystemContext systemContext) {
        if (Log.D) Log.d(TAG, "onReady(" + systemContext + ")");

        if (mDismissed) {
            return;
        }

        mSystemContext = systemContext;
        mPermissionSystemComponent = systemContext.getComponent(PermissionSystemComponent.class);

        final String[] missingPermissions = getMissingPermissions();
        if (missingPermissions.length != 0 && mPermissionSystemComponent.shouldUseRuntimePermissionModel() && !mPermissionsRequested) {
            for (final String missingPermission : missingPermissions) {
                new CustomPermissionGrantedListener(missingPermission).register();
            }

            if (Log.D) Log.d(TAG, "Requesting permissions: " + Arrays.toString(missingPermissions));
            mCurrentPermissionRequest = mPermissionSystemComponent.requestPermissions(missingPermissions, null,
                    new CustomPermissionResultListener());
            mPermissionsRequested = true;
        } else {
            callListenerIfNeeded();
        }
    }

    private void callListenerIfNeeded() {
        if (!mListenerCalled) {
            mListener.onReady(mSystemContext);
            mListenerCalled = true;

            dismiss();
        }
    }

    private String[] getMissingPermissions() {
        final List<String> missingPermissions = new ArrayList<>();

        for (final String permission : REQUIRED_PERMISSIONS) {
            if (!mPermissionSystemComponent.isGranted(permission)) {
                missingPermissions.add(permission);
            }
        }

        return missingPermissions.toArray(new String[missingPermissions.size()]);
    }

    private final class CustomPermissionGrantedListener implements PermissionGrantedListener {
        private final String mPermission;

        private CustomPermissionGrantedListener(final String permission) {
            mPermission = permission;
        }

        private void register() {
            mPermissionGrantedListeners.add(this);
            mPermissionSystemComponent.addPermissionGrantedListener(this);
        }

        private void unregister() {
            mPermissionGrantedListeners.remove(this);
            mPermissionSystemComponent.removePermissionGrantedListener(this);
        }

        @Override
        public void onPermissionGranted() {
            if (Log.D) Log.d(TAG, "onPermissionGranted() - " + mPermission);

            if (mDismissed) {
                return;
            }

            unregister();

            if (getMissingPermissions().length == 0) {
                callListenerIfNeeded();
            }
        }

        @NonNull
        @Override
        public String getPermission() {
            return mPermission;
        }
    }

    private final class CustomPermissionResultListener implements PermissionResultListener {
        @Override
        public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                               @NonNull final int[] grantResults) {
            if (Log.D) {
                Log.d(TAG, String.format("onRequestPermissionsResult($1%s, $2%s, $3%s)",
                        requestCode,
                        Arrays.toString(permissions),
                        Arrays.toString(grantResults)));
            }

            if (mDismissed) {
                return;
            }

            final String[] missingPermissions = getMissingPermissions();
            if (Log.D) Log.d(TAG, "Missing permissions: " + Arrays.toString(missingPermissions));

            if (missingPermissions.length == 0) {
                // All permissions granted, so call wrapped listener
                callListenerIfNeeded();
            } else {
                boolean shouldShowRationale = false;
                for (final String permission : missingPermissions) {
                    if (mPermissionSystemComponent.shouldShowRationale(permission)) {
                        shouldShowRationale = true;
                        break;
                    }
                }

                if (shouldShowRationale) {
                    if (Log.D) Log.d(TAG, "Re-requesting permissions: " + Arrays.toString(missingPermissions));
                    mCurrentPermissionRequest = mPermissionSystemComponent.requestPermissions(missingPermissions, null, this);
                } else {
                    if (Log.D) Log.d(TAG, "Launching settings screen");
                    final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", mSystemContext.getApplicationContext().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mSystemContext.getApplicationContext().startActivity(intent);
                }
            }
        }
    }
}
