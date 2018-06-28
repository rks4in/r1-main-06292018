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

package com.tomtom.r1navapp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;

import com.tomtom.r1navapp.presentation.PresentationService;
import com.tomtom.navkit.map.InvalidExtensionId;
import com.tomtom.navkit.map.Map;
import com.tomtom.navkit.map.MapHolder;
import com.tomtom.navkit.map.camera.CameraOperator;
import com.tomtom.navkit.map.camera.CameraOperatorStack;
import com.tomtom.navkit.map.extension.mlg.MlgExtension;
import com.tomtom.navkit.map.extension.positioning.PositioningExtension;
import com.tomtom.navkit.map.extension.routes.RouteExtension;
import com.tomtom.navui.util.Log;
import com.tomtom.navui.sigrendererkit3.visual.StyleFileConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.logging.Handler;

import com.tomtom.r1navapp.R;

public class ClusterDisplayService extends PresentationService {

    private static final String TAG = "ClusterDisplayService";

    private static final String STYLESHEET =  "default.json";

    private View mView;
    private MapView mMapView;

    private RouteExtension mRouteExtension;
    private PositioningExtension mPositioningExtension;
    private MlgExtension mMlgExtension;

    @Override
    public void onCreate() {
        if (Log.D) {
            Log.d(TAG, "onCreate()");
        }

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (Log.D) {
            Log.d(TAG, "onDestroy()");
        }

        super.onDestroy();

        if (mMlgExtension != null) {
            mMlgExtension.stop();
            mMlgExtension = null;
        }

        if (mPositioningExtension != null) {
            mPositioningExtension.stop();
            mPositioningExtension = null;
        }

        if (mRouteExtension != null) {
            mRouteExtension.stop();
            mRouteExtension = null;
        }

        mMapView = null;
        mView = null;
    }

    @Override
    protected int getThemeId() {
        return(R.style.r1_Theme);
    }

    @Override
    protected View buildPresentationView(Context context, LayoutInflater inflater) {
        if (Log.D) {
            Log.d(TAG, "buildPresentationView()");
        }

        mView = inflater.inflate(R.layout.r1_cluster, null);
        acquireMapView();
        if (mMapView != null) {
            applyStyle();

            createPositioningExtension();
            createRouteExtension();
            createMlgExtension();

            setupCamera();
        }

        return mView;
    }

    private void acquireMapView() {
        mMapView = (MapView) mView.findViewById(R.id.viewMap);
    }

    private void applyStyle() {
        try {
            String defaultStyle = readTextFileAsset(getAssets(), STYLESHEET);
            mMapView.getMapHolder().getMap().setStyle(defaultStyle);
            mMapView.getMapHolder().getMap().setStyleConstantOverride(StyleFileConstants.TIME_OF_DAY_SWITCH,
                    StyleFileConstants.DAY);
            mMapView.getMapHolder().getMap().setStyleConstantOverride(StyleFileConstants.INTERACTION_MODE_SWITCH,
                    StyleFileConstants.DRIVING);
        } catch (IOException e) {
            if (Log.E) {
                Log.e(TAG, "Could not open style assets", e);
            }
        } catch (Map.InvalidConstant | Map.InvalidStyleDefinition | Map.NoStyleAvailable e) {
            if (Log.E) {
                Log.e(TAG, "Problem in stylesheet", e);
            }
        }
    }

    private static String readTextFileAsset(final AssetManager assetManager,
                                            final String assetFilename) throws IOException {
        final StringBuilder out = new StringBuilder(512);
        final InputStream iStream = assetManager.open(assetFilename);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
        } finally {
            try {
                iStream.close();
            } catch (IOException e) {
                if (Log.E) {
                    Log.e(TAG, "Problem closing inputstream", e);
                }
            }
        }
        return out.toString();
    }

    private void createMlgExtension() {
        try {
            mMlgExtension = MlgExtension.create(mMapView.getMapHolder().getMap(),
                    StyleFileConstants.ExtensionIds.MLG_EXTENSION_ID);
        } catch (Map.LayerNotFound | InvalidExtensionId e) {
            if (Log.E) {
                Log.e(TAG, "Unable to create MLG extension", e);
            }
        }

        mMlgExtension.enable();
    }

    private void createPositioningExtension() {
        try {
            mPositioningExtension = PositioningExtension.create(mMapView.getMapHolder(),
                    StyleFileConstants.ExtensionIds.POSITIONING_EXTENSION_ID);
        } catch (InvalidExtensionId | MapHolder.MapHolderEmpty e) {
            if (Log.E) {
                Log.e(TAG, "Unable to create positioning extension", e);
            }
        }
    }

    private void createRouteExtension() {
        try {
            mRouteExtension = RouteExtension.create(mMapView.getMapHolder().getMap(),
                    StyleFileConstants.ExtensionIds.ROUTE_EXTENSION_ID);
        } catch (InvalidExtensionId | Map.LayerNotFound e) {
            if (Log.E) {
                Log.e(TAG, "Unable to create route extension", e);
            }
        }
        mRouteExtension.enable();
        mRouteExtension.setPolicy(RouteExtension.Policy.kAutomatic);
    }

    private void setupCamera() {
        final CameraOperatorStack cameraOperatorStack =
                mMapView.getMapHolder().getMap().getCameraOperatorStack();
        final CameraOperator followRouteCameraOperator =
                cameraOperatorStack.createFollowRouteCameraOperator();
        cameraOperatorStack.pushCameraOperator(followRouteCameraOperator);
    }
}