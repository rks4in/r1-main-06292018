/*
 * Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.r1navapp.presentation;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;
import com.tomtom.navui.util.Log;

public class PresentationHelper implements DisplayManager.DisplayListener {

    public interface Listener {
        void handleStartPresentation(Display display);
        void handleStopPresentation();
        void handleNoDisplayAndStopService();
    }

    private static final String TAG = "PresentationHelper";

    private final PresentationHelper.Listener mListener;
    private final DisplayManager mDisplayManager;
    private Display mActiveDisplay;

    public PresentationHelper(Context context, PresentationHelper.Listener listener) {
        mListener = listener;
        mDisplayManager = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE);
    }

    public void registerAndHandleDisplayEventForId(int displayId) {
        if (Log.D) {
            Log.d(TAG, " registerAndHandleDisplayEventForId : ( " + displayId + " )");
        }
        if (mActiveDisplay != null ) {
            if (Log.W) {
                Log.w(TAG, " Service already initialized for display ID : ( " +
                        mActiveDisplay.getDisplayId() + " )");
            }
        } else {
            startDisplayIfRequiredForDisplayId(displayId);
            mDisplayManager.registerDisplayListener(this, null);
        }
    }

    public void stopPresentationAndUnregister() {
        if (Log.D) {
            Log.d(TAG, " stopPresentationAndUnregister ");
        }
        mListener.handleStopPresentation();
        mActiveDisplay = null;
        unregisterDisplayListener();
    }

    public void unregisterDisplayListener() {
        mDisplayManager.unregisterDisplayListener(this);
    }

    private void startDisplayIfRequiredForDisplayId(int displayId) {
        Display display =
                mDisplayManager.getDisplay(displayId);

        if (display == null) {
            mListener.handleNoDisplayAndStopService();
        } else {
            if (display.isValid()) {
                mListener.handleStartPresentation(display);
                mActiveDisplay = display;
            } else {
                if (Log.E) {
                    Log.e(TAG, " display with id ( " + displayId + " )  is not valid");
                }
                mListener.handleNoDisplayAndStopService();
            }
        }
    }

    private void handleDisplayEventForId(int displayId) {
        Display display =
                mDisplayManager.getDisplay(displayId);

        if (display == null &&
                mActiveDisplay != null &&
                mActiveDisplay.getDisplayId() == displayId) {
            // the display has been removed so stop presentation
            mListener.handleStopPresentation();
        } else {
            if (display != null && display.isValid()) {
                // check if the display is already set, else we are already set.
                if (mActiveDisplay == null) {
                    mListener.handleStartPresentation(display);
                    mActiveDisplay = display;
                }
            }
        }
    }

    @Override
    public void onDisplayAdded(int displayId) {
        handleDisplayEventForId(displayId);
    }

    @Override
    public void onDisplayChanged(int displayId) {
        handleDisplayEventForId(displayId);
    }

    @Override
    public void onDisplayRemoved(int displayId) {
        handleDisplayEventForId(displayId);
    }
}