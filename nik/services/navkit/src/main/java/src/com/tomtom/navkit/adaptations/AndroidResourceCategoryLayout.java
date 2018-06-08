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

package com.tomtom.navkit.adaptations;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import java.io.File;

public class AndroidResourceCategoryLayout {
    private Context context = null;
    private String categorySharedDirectory;
    private String categoryRegistryDirectory;
    private String categoryTemporaryDirectory;
    private String categoryStateDirectory;
    private String categoryConfigurationDirectory;
    private String categoryRuntimeDirectory;
    private String categoryLegacyDirectory;
    private String categoryMapsDirectory;
    private String categoryPOIsDirectory;
    private String categorySpeedCamerasDirectory;

    private void getTargetLocations() {
        final String NAVKIT_FOLDER                  = "ttndata";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
          categoryTemporaryDirectory     = context.getExternalCacheDir().getPath(); 
          categorySharedDirectory        = Environment.getExternalStoragePublicDirectory(null).getPath() + File.separator + NAVKIT_FOLDER + File.separator + "shared"; // this call does not create a requested directory
          categoryRegistryDirectory      = Environment.getExternalStoragePublicDirectory(null).getPath() + File.separator + NAVKIT_FOLDER + File.separator + "registry"; // this call does not create a requested directory
          categoryStateDirectory         = context.getExternalFilesDir(null).getPath() + File.separator + "state";
          categoryConfigurationDirectory = context.getExternalFilesDir(null).getPath() + File.separator + "configuration";
          categoryRuntimeDirectory       = context.getExternalFilesDir(null).getPath() + File.separator + "runtime";
          categoryLegacyDirectory        = context.getExternalFilesDir(null).getPath() + File.separator + "content";
          categoryMapsDirectory          = context.getExternalFilesDir(null).getPath() + File.separator + "maps";
          categoryPOIsDirectory          = context.getExternalFilesDir(null).getPath() + File.separator + "pois";
          categorySpeedCamerasDirectory  = context.getExternalFilesDir(null).getPath() + File.separator + "speedcameras";
        }
        else {
          categoryTemporaryDirectory     = context.getCacheDir().getPath();
          categorySharedDirectory        = context.getDir("shared",        Context.MODE_PRIVATE).getPath();
          // this location will not survive uninstall
          categoryRegistryDirectory      = context.getDir("registry",      Context.MODE_PRIVATE).getPath();
          categoryStateDirectory         = context.getDir("state",         Context.MODE_PRIVATE).getPath();
          categoryConfigurationDirectory = context.getDir("configuration", Context.MODE_PRIVATE).getPath();
          categoryRuntimeDirectory       = context.getDir("runtime",       Context.MODE_PRIVATE).getPath();
          categoryLegacyDirectory        = context.getDir("content",       Context.MODE_PRIVATE).getPath();
          categoryMapsDirectory          = context.getDir("maps",          Context.MODE_PRIVATE).getPath();
          categoryPOIsDirectory          = context.getDir("pois",          Context.MODE_PRIVATE).getPath();
          categorySpeedCamerasDirectory  = context.getDir("speedcameras",  Context.MODE_PRIVATE).getPath();
        }
    }

    public AndroidResourceCategoryLayout(Context context) {
        this.context = context;
        getTargetLocations();
    }

    // ======================================================================
    // C-to-Java callbacks
    // ======================================================================
    public String getCategorySharedDirectory() {
      return categorySharedDirectory;
    }

    public String getCategoryRegistryDirectory(){
      return categoryRegistryDirectory;
    }

    public String getCategoryTemporaryDirectory()
    {
        return categoryTemporaryDirectory;
    }

    public String getCategoryStateDirectory()
    {
        return categoryStateDirectory;
    }

    public String getCategoryConfigurationDirectory()
    {
        return categoryConfigurationDirectory;
    }

    public String getCategoryRuntimeDirectory()
    {
      return categoryRuntimeDirectory;
    }

    public String getCategoryMapsDirectory()
    {
      return categoryMapsDirectory;
    }

    public String getCategoryPOIsDirectory()
    {
      return categoryPOIsDirectory;
    }

    public String getCategorySpeedCamerasDirectory()
    {
      return categorySpeedCamerasDirectory;
    }
}
