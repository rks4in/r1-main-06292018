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

package com.tomtom.navkit;

import java.io.*;
import java.util.zip.*;
import java.util.Enumeration;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class ContentExtractor {
    static final String TAG = "ContentExtractor"; // logging tag

    private Context mContext;
    private String mRoot;
    private String mSharedRoot;
    private String mTargetPath;

    /**
     * Given a context, it creates a ContentExtractor
     *
     * @param context
     */
    public ContentExtractor(Context context) {
        mContext = context;

        ApplicationInfo appinfo = context.getApplicationInfo();
        mTargetPath = appinfo.sourceDir;

        // On stock android, extract to external storage.
        // Add ttndata/files to the path, as getFilesDir() used below also
        // includes that.
        mRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ttndata" + File.separator + "files";

        Log.v(TAG, "Files dir = " + mRoot);
        Log.v(TAG, "Source dir = " + mTargetPath);
    }
    /**
     * This method extracts the resource files by treating the .apk file as an
     * ordinary zip archive (which it basically is), and iterating over the
     * contents. This approach is necessary because the AssetManager is
     * apparently unable to unzip files larger than 1MB. Each entry in the zip
     * file provides a CRC value, which is stored in the shared preferences.
     * This way, when a new version of the .apk is installed, we can check
     * whether individual .chk files need to be extracted again, or can be left
     * alone.
     */
    public boolean extractUsingZip() {
        // The CRC values of the assets are stored in the shared preferences, so
        // we can check for changes
        String packageName = mContext.getPackageName();
        SharedPreferences prefs = mContext.getSharedPreferences(packageName, Context.MODE_PRIVATE);

        // Buffer to transfer data from zipfile to internal storage
        final int BUFFER_SIZE = 64 * 1024;
        byte data[] = new byte[BUFFER_SIZE];

        final String localSearchProvidersIconsFileName = "localsearchprovidericons.zip";
        boolean extractionSuccessful = true;
        try {
            final String assetPath = "assets";
            // Treat the .apk file as an ordinary zip archive
            ZipFile zipfile = new ZipFile(mTargetPath);
            Enumeration<? extends ZipEntry> e = zipfile.entries();

            // Iterate over zipfile contents
            while (e.hasMoreElements()) {
                ZipEntry zipentry = e.nextElement();
                String path = zipentry.getName();

            if (path.startsWith(assetPath)) {

              long zipCrc = zipentry.getCrc();
              long extractionCrc = -1;
              String name = path.substring(assetPath.length() + 1, path.length());
              String crcKey = name + ".crc";
              File outFile = new File(mRoot, name);

                // Limit extraction to .chk, .sql(ite), junction.ini, localsearchprovider.zip, and .s3db files for
                // NavKitService.
                // There may be more assets stored in the apk for other services
                // - but those other
                // files should be extracted by the respective service's
                // ContentExtractors
                // NOTE: if other assets are added, it should be checked that
                // those are extracted
                // properly
              if ( path.endsWith(".chk")
                            || path.endsWith("providers.xml")
                            || path.endsWith("mapviewer2memoryconfig.xml")
                            || path.endsWith("junction.ini")
                            || path.endsWith(".sql")
                            || path.endsWith(".sqlite")
                            || path.endsWith(".s3db")
                    ) {

                if (!outFile.exists() || !prefs.contains(crcKey) || (prefs.getLong(crcKey, 0L) != zipCrc)) {
                  SharedPreferences.Editor editor = prefs.edit();
                  editor.putLong(crcKey, -1L);
                  editor.commit();

                  extractionCrc = CopyFileContentsAndReturnCrc(zipfile, zipentry, outFile);
                  if (extractionCrc != zipCrc || extractionCrc == -1) {
                    Log.e(TAG, "Extracting " + outFile.getName() + " failed. ZIP CRC32 = " + zipCrc + "; Extracted CRC32 = " + extractionCrc);
                    extractionSuccessful = false;
                        } else {
                            Log.v(TAG, "Extracted " + outFile.getName() + " successfully.");
                        // Store crc code
                    editor.putLong(crcKey, zipCrc);
                        editor.commit();
                  }
                    } else {
                        Log.v(TAG, "Skipping extraction of " + outFile.getAbsolutePath());
                    }
              }

              else if (path.endsWith(localSearchProvidersIconsFileName)) {
                if (!outFile.exists() || !prefs.contains(crcKey) || (prefs.getLong(crcKey, 0L) != zipCrc)) {
                  SharedPreferences.Editor editor = prefs.edit();
                  editor.putLong(crcKey, -1L);
                  editor.commit();

                  extractionCrc = CopyFileContentsAndReturnCrc(zipfile, zipentry, outFile);
                  if (extractionCrc != zipCrc || extractionCrc == -1) {
                    Log.e(TAG, "Extracting " + outFile.getName() + " failed. ZIP CRC32 = " + zipCrc + "; Extracted CRC32 = " + extractionCrc);
                    extractionSuccessful = false;
                        } else {
                            Log.v(TAG, "Extracted " + outFile.getName() + " successfully.");
                    // Unzip to the SharedContent directory.
                    if (!UnpackIconsProviderData(outFile)) {
                      Log.e(TAG, "Extracting from " + localSearchProvidersIconsFileName + " failed.");
                      extractionSuccessful = false;
                        }
                    else {
                        // Store crc code
                      editor.putLong(crcKey, zipCrc);
                        editor.commit();
                    }
                  }
                    } else {
                        Log.v(TAG, "Skipping extraction of " + outFile.getAbsolutePath());
                    }
                }
            }
          }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            extractionSuccessful = false;
        }

        return extractionSuccessful;
    }

    public boolean UnpackIconsProviderData(File iconsProviderFile) throws java.util.zip.ZipException, java.io.IOException {
        ZipFile zipfile = new ZipFile(iconsProviderFile);
        File sharedContentRoot = new File(mSharedRoot);
        boolean extractionSuccessful = true;

        try {
        Enumeration<? extends ZipEntry> e = zipfile.entries();
        while (e.hasMoreElements()) {
            ZipEntry zipentry = e.nextElement();
            long zipCrc = zipentry.getCrc();
            long extractionCrc = -1;

            if (!zipentry.isDirectory()) {
                File targetFile = new File(sharedContentRoot, zipentry.getName());
              extractionCrc = CopyFileContentsAndReturnCrc(zipfile, zipentry, targetFile);
              if (extractionCrc != zipCrc || extractionCrc == -1) {
                Log.e(TAG, "Extracting " + targetFile.getName() + " failed. ZIP CRC32 = " + zipCrc + "; Extracted CRC32 = " + extractionCrc);
                extractionSuccessful = false;
                }
            }
        }
    }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            extractionSuccessful = false;
        }
        return extractionSuccessful;
    }

    private long CopyFileContentsAndReturnCrc(ZipFile zipfile, ZipEntry zipentry, File outFile) {
        final int BUFFER_SIZE = 64 * 1024;
        byte data[] = new byte[BUFFER_SIZE];

        InputStream is = null;
        BufferedInputStream bis = null;

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        CheckedOutputStream crcDest = null;
        long checksumValue = -1;

        try {
            File outFilePath = outFile.getParentFile();
            if (outFilePath != null && !outFilePath.exists() && !outFilePath.mkdirs()) {
                Log.v(TAG, "Error creating path for " + outFilePath.getAbsolutePath());
                return -1;
            }

            is = zipfile.getInputStream(zipentry);
            bis = new BufferedInputStream(is, BUFFER_SIZE);

            fos =     new FileOutputStream    (outFile);
            bos =     new BufferedOutputStream(fos, BUFFER_SIZE);
            crcDest = new CheckedOutputStream (bos, new CRC32());

            int count = 0;
            while ((count = bis.read(data)) != -1) {
                crcDest.write(data, 0, count);
            }
            crcDest.flush();
            FileDescriptor destFileDescriptor = fos.getFD();
            destFileDescriptor.sync();
            checksumValue = crcDest.getChecksum().getValue();
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            checksumValue = -1;
        }
        finally {
            CloseInputStream(bis, zipentry.getName());
            CloseOutputStream(crcDest, outFile.getName());
        }

        return checksumValue;
    }

    /**
     * This method extracts the resources from the .apk file using the
     * AssetManager (i.e. the 'proper' way). However, the AssetManager
     * apparently cannot unzip files larger than 1MB. So, in order to use this
     * method, resources should be smaller than 1MB, or should be stored
     * uncompressed.
     */
    public void extractUsingAssetManager() {
        AssetManager mgr = mContext.getAssets();

        final int BUFFER_SIZE = 64 * 1024;
        byte data[] = new byte[BUFFER_SIZE];

        try {
            String[] files = mgr.list("");

            for (int i = 0; i < files.length; ++i) {
                if (files[i].endsWith(".chk")) {
                    Log.v(TAG, "Extracting " + files[i]);
                    InputStream is = null;
                    BufferedInputStream bis = null;
                    FileOutputStream fos = null;
                    BufferedOutputStream bos = null;
                    File outFile = new File(mRoot, files[i]);
                    String outFilePath = outFile.getAbsolutePath();
                    try {
                        // Construct buffered input stream from AssetManager
                        is = mgr.open(files[i]);
                        bis = new BufferedInputStream(is, BUFFER_SIZE);

                        // Construct buffered output stream on external storage
                        fos = new FileOutputStream(outFile);
                        bos = new BufferedOutputStream(fos, BUFFER_SIZE);

                        // copy input to output
                        int count;
                        while ((count = bis.read(data)) != -1) {
                            bos.write(data, 0, count);
                        }
                    } finally {
                        CloseInputStream(bis, files[i]);
                        CloseInputStream(is, files[i]);
                        CloseOutputStream(bos, outFilePath);
                        CloseOutputStream(fos, outFilePath);
                    }
                } else {
                    Log.i(TAG, "Skipping " + files[i]);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error unpacking resource files: " + e.getMessage());
        }

        Log.v(TAG, "Done!");
    }

    /**
     * Returns the current Working directory
     *
     * @return
     */
    public synchronized String getWorkingDirectory() {
        return mRoot;
    }

    /**
     * Set the current Working directory
     *
     * @param mWorkingDirectory
     */
    public synchronized void setWorkingDirectory(String mWorkingDirectory) {
        this.mRoot = mWorkingDirectory;
    }

    /**
     * Set the current Shared directory
     *
     * @param mSharedDirectory
     */
    public synchronized void setSharedDirectory(String mSharedDirectory) {
        this.mSharedRoot = mSharedDirectory;
    }

    private void CloseInputStream(InputStream aStream, String aStreamName) {
        if (aStream != null) {
            try {
                aStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close input stream \"" + aStreamName + "\": " + e.getMessage());
            }
        }
    }

    private void CloseOutputStream(OutputStream aStream, String aStreamName) {
        if (aStream != null) {
            try {
                aStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close output stream \"" + aStreamName + "\": " + e.getMessage());
            }
        }
    }
}
