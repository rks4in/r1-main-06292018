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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.tomtom.navkit.map.InvalidEnvironment;
import com.tomtom.navkit.map.Map;
import com.tomtom.navkit.map.MapHolder;
import com.tomtom.navkit.map.Rectangle;
import com.tomtom.navkit.map.SurfaceAdapter;
import com.tomtom.navkit.map.TomTomNavKitMapJNI;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

/**
 * A GLSurfaceView hooked up with a TomTom NavKit Map API SurfaceAdapter.
 * Retrieve the MapHolder to add one or more Maps and to interact with them.
 */
public final class MapView extends GLSurfaceView {

    private static final String TAG = "MapView";

    private ClusterRendererEnvironment mEnvironment;
    private MapHolder mMapHolder;
    private SurfaceAdapterDelegateAdapter mSurfaceAdapterDelegateAdapter;

    private Rectangle mSafeArea;

    /**
     * Creates a MapView with a default Android environment.
     * @param context used for initialising the environment.
     */
    public MapView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Creates a MapView with a default Android environment, used when inflating from XML.
     * @param context used for initialising the environment.
     * @param attrs used when the view is inflated from a layout file.
     */
    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context);
        }
    }

    private void init(Context context) {
        TomTomNavKitMapJNI.nativeSetAssetManager(context.getAssets());

        mEnvironment = new ClusterRendererEnvironment(context);
        try {
            mMapHolder = MapHolder.createInstance(mEnvironment);
        } catch (InvalidEnvironment e) {
            throw new RuntimeException("Exception when creating map holder.", e);
        }

        setEGLContextFactory(new EGLContextFactory());
        setRenderer(new Renderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mSurfaceAdapterDelegateAdapter = new SurfaceAdapterDelegateAdapter(this);
        mMapHolder.getSurfaceAdapter().setDelegate(mSurfaceAdapterDelegateAdapter);
    }

    /**
     * @return the MapHolder for this view.
     */
    public MapHolder getMapHolder() {
        return mMapHolder;
    }

    /**
     * Should be called by the owner when the activity is paused. See GLSurfaceView docs for more info.
     * Call the base class implementation when overriding this function.
     */
    @Override
    public void onPause() {
        mMapHolder.getSurfaceAdapter().setDelegate(null);

        mMapHolder.pause();
        super.onPause();
    }

    /**
     * Should be called by the owner when the activity is resumed. See GLSurfaceView docs for more info.
     * Call the base class implementation when overriding this function.
     */
    @Override
    public void onResume() {
        super.onResume();

        mMapHolder.getSurfaceAdapter().setDelegate(mSurfaceAdapterDelegateAdapter);
        mMapHolder.resume();
    }

    /**
     * Called when the view is detached from its window. Call the base class implementation
     * when overriding this function
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mEnvironment.flushCallbacks();
    }

    private static class SurfaceAdapterDelegateAdapter extends SurfaceAdapter.SurfaceAdapterDelegate {

        private final GLSurfaceView mSurfaceView;

        private SurfaceAdapterDelegateAdapter(GLSurfaceView surfaceView) {
            mSurfaceView = surfaceView;
        }

        @Override
        public final void requestDraw() {
            mSurfaceView.requestRender();
        }
    }

    private final class Renderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mMapHolder.getSurfaceAdapter().onSurfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            try {
                mMapHolder.getSurfaceAdapter().onSurfaceChanged(width, height);
            } catch (SurfaceAdapter.NotYetInitialized | SurfaceAdapter.InvalidThreadAccess e) {
                throw new RuntimeException("Exception when handling surface changed.", e);
            }
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if ((mSafeArea != null) &&
                (mSafeArea != mMapHolder.getMap().getSafeArea())) {
                Rectangle viewportArea = getMapHolder().getMap().getViewport();
                if (viewportArea.getWidth() > 0 && viewportArea.getHeight() > 0) {
                    try {
                        mMapHolder.getMap().setSafeArea(mSafeArea);
                    } catch (Map.InvalidSafeArea invalidSafeArea) {
                        invalidSafeArea.printStackTrace();
                    }
                }
            }

            try {
                mMapHolder.getSurfaceAdapter().onDrawFrame();
            } catch (SurfaceAdapter.NotYetInitialized | SurfaceAdapter.InvalidThreadAccess e) {
                throw new RuntimeException("Exception when handling frame drawing.", e);
            }
        }
    }

    /**
     * Specific implementation of an GLSurfaceView.EGLContextFactory which will
     * create an OpenGL context according to the best capabilities of the device.
     *
     * It will first try to create an OpenGL ES 3.0 context. Then it will fallback to 2.0.
     */
    private static class EGLContextFactory implements GLSurfaceView.EGLContextFactory {

        private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        /**
         * Create a EGLContextFactory
         */
        EGLContextFactory() {
            // do nothing
        }

        /**
         * It creates an OpenGL ES context, trying first 3.0, then 2.0.
         *
         * It might return null if no context can be created.
         *
         * This method is internally called by the GL Thread associated to an Android Activity and should
         * never be called directly.
         */
        @Override
        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            // Trying to create an OpenGL ES 3.0 context
            int[] attribList = {EGL_CONTEXT_CLIENT_VERSION, 3, EGL10.EGL_NONE};
            EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attribList);

            if ((null == context) || context.equals(EGL10.EGL_NO_CONTEXT)) {
                attribList[1] = 2;
                context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attribList);
            }

            return context;
        }

        /**
         * It destroys the OpenGL Context.
         */
        @Override
        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            egl.eglDestroyContext(display, context);
        }
    }
}