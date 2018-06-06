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
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

public class AndroidProperties {
    private static final String TAG = "AndroidProperties";
    private Context context = null;
    private String workingDirectory;
    private String privateContentDirectory;
    private String sharedContentDirectory;
    private String persistentDirectory;
    private String temporaryFilesDirectory;
    private static final String kDefaultReflectionAddress = "IP4:127.0.0.1:3000";
    private static final String kReflectionSocketName = "navkit";
    private static String reflectionBaseAddress = null;

    private void closeReader(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                Log.e(TAG, "closeReader: close() failed");
            }
        }
    }

    private void getTargetLocations() {
        final String ALTERNATIVE_PATH_FILE_NAME     = "altpath.dat";
        final String NAVKIT_FOLDER                  = "ttndata";
        final String NAVKIT_HOME_SUB_FOLDER         = "files";
        final String NAVKIT_PRIVATE_CONTENT_FOLDER  = "content";
        final String NAVKIT_SHARED_CONTENT_FOLDER   = "sharedContent";

        workingDirectory = context.getFilesDir().getPath();
        privateContentDirectory = context.getDir(NAVKIT_PRIVATE_CONTENT_FOLDER, Context.MODE_PRIVATE).getPath();
        sharedContentDirectory = context.getDir(NAVKIT_SHARED_CONTENT_FOLDER, Context.MODE_PRIVATE).getPath();
        persistentDirectory = workingDirectory;
        temporaryFilesDirectory = context.getCacheDir().getPath();

        if (Environment.getExternalStorageState().compareTo(Environment.MEDIA_MOUNTED) == 0) {
            workingDirectory = Environment.getExternalStorageDirectory().getPath() + File.separator + NAVKIT_FOLDER + File.separator + NAVKIT_HOME_SUB_FOLDER;
            privateContentDirectory = Environment.getExternalStorageDirectory().getPath() + File.separator + NAVKIT_FOLDER;
            sharedContentDirectory = Environment.getExternalStorageDirectory().getPath() + File.separator + NAVKIT_FOLDER + File.separator + NAVKIT_SHARED_CONTENT_FOLDER;
            // Although we are checking this condition for private path, we will follow this for shared path as well....
            File alternativePathFile = new File(privateContentDirectory + File.separator + ALTERNATIVE_PATH_FILE_NAME);
            if (alternativePathFile.exists()) {
                FileReader fileReader = null;
                BufferedReader bufferedReader = null;
                try {
                    fileReader = new FileReader(alternativePathFile);
                    bufferedReader = new BufferedReader(fileReader);
                    String alternativePath = bufferedReader.readLine();
                    alternativePathFile = new File(alternativePath);
                    if (alternativePathFile.exists()) {
                        privateContentDirectory = alternativePath;
                    } else {
                        privateContentDirectory += File.separator + NAVKIT_PRIVATE_CONTENT_FOLDER;
                    }
                } catch (Exception e) {
                    Log.v(TAG, "Cannot read alternative path file " + ALTERNATIVE_PATH_FILE_NAME);
                } finally {
                    closeReader(bufferedReader);
                    closeReader(fileReader);
                }
            } else {
                privateContentDirectory += File.separator + NAVKIT_PRIVATE_CONTENT_FOLDER;
            }
        }
    }

    public AndroidProperties(Context context) {
        this.context = context;
        getTargetLocations();
    }
    
    public static void setReflectionBaseAddress(String address) {
        reflectionBaseAddress = address;
    }

    // ======================================================================
    // C-to-Java callbacks
    // ======================================================================
    public String getWorkingDirectory() {
      return workingDirectory;
    }

    public String getPersistentDirectory(){
      return persistentDirectory;
    }

    public String getPrivateContentDirectory()
    {
        return privateContentDirectory;
    }

    public String getSharedContentDirectory()
    {
        return sharedContentDirectory;
    }

    public String getTemporaryFilesDirectory()
    {
        return temporaryFilesDirectory;
    }

    public String getReflectionListenerAddress()
    {
        String reflectionAddress;
        if (reflectionBaseAddress == null || reflectionBaseAddress.isEmpty()) {
            reflectionAddress = kDefaultReflectionAddress;
        } else {
            reflectionAddress = reflectionBaseAddress;
            int indexColon = reflectionAddress.indexOf(':');
            if (indexColon != -1) {
                reflectionAddress = reflectionAddress.substring(0, indexColon).toUpperCase(Locale.ENGLISH)
                                    + reflectionAddress.substring(indexColon);
            }
            if (reflectionAddress.startsWith("UDS:")) {
                reflectionAddress += "/" + kReflectionSocketName;
            }
        }
        Log.v(TAG, "Using reflection address = " + reflectionAddress);
        return reflectionAddress;
    }

}

