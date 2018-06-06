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

package com.tomtom.logcollector;

/**
 * TomTom Android Log Collector
 *
 * Saves important logging from logcat to external storage.
 * When possible, existing logfiles are uploaded to a server
 * and removed from the device.
 *
 * To see debug logging in the logcat run the following command:
 *     adb shell setprop log.tag.LogCollector VERBOSE
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;

public class LogCollectorService extends Service {
    //! Key for Intent.getExtras() to configure the service externally
    public static final String CONFIG_CRASH_PATH = "CRASHPATH";
    
    // If false globally disabled all debug code / messages
    private static final boolean DBG = true;

    private final static String COLLECTOR_SUBDIR = "navkit_tombstones";
    private final static String TAG              = "LogCollector";
    private final static String SERVER_HOSTNAME  = "navkit-logs.ttg.global";
    private final static String SERVERURL        = "http://" + SERVER_HOSTNAME + "/fileupload.php";
    private final static String CGI_SCRIPT_NAME  = "uploadedfile";

    // These variables are used to determine how long a crash log file is kept
    // on the SDCARD
    private final static int KEEP_DAYS_NEWFILES = 21;
    private final static int KEEP_DAYS_UPLOADED = 21;

    private final static int INITDELAY = 1000 * 10;           // 10 seconds
    private final static int INTERVAL  = 1000 * 60 * 30;      // 30 minutes
    private final static int MINAGE    = 1000 * 60 * 5;       // 5 minutes
    private final static int PERDAY    = 1000 * 60 * 60 * 24; // 24 hours

    // Sets the maximum number of items processed during each run of
    // the logcollector code
    private final static int MAX_QUEUE_LENGTH = 30;

    private Timer mTimer = new Timer();
    private static boolean isRunning = false;
    private int runCounter = 0;

    // Log filename extensions
    private final static String CRASH_LOGS  = ".txt";
    private final static String BACKUP_LOGS = ".txt.bak";

    private String mCrashPath = null;
    
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "LogCollector - onBind()");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (isRunning == false) {
            Log.i(TAG, "LogCollector Service Created.");
            mTimer.scheduleAtFixedRate(new ProcessCrashLogs(), INITDELAY,
                    INTERVAL);
            isRunning = true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        if (intent != null && intent.getExtras() != null)
        {
            Bundle extras = intent.getExtras();
            mCrashPath = (String)extras.get(CONFIG_CRASH_PATH);
        }
        Log.d(TAG, "CrashPath='" + mCrashPath + "'");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY; // Run until explicitly stopped.
    }

    @Override
    public void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }

        Log.i(TAG, "Service Stopped.");
        isRunning = false;
        super.onDestroy();
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        LogCollectorService getService() {
            return LogCollectorService.this;
        }
    }

    // ////////////////////////////////////////
    // Nested classes
    // ////////////////////////////////////////

    /**
     * LogCollector Timer class This script is scheduled to be ran at periodic
     * intervals. The script:
     *
     * 1) Checks to see if there are any crashlog files which needs to be
     * uploaded to the crashlog server. If there are it attempts to do this
     *
     * 2) Purges any log files (on the Android device) which are older than the
     * crashlog retention period (uploaded crashlogs are kept for
     * {@link #KEEP_DAYS_UPLOADED} days, which files which haven't been uploaded
     * are kept for a maximum of {@link #KEEP_DAYS_NEWFILES} days
     */
    private class ProcessCrashLogs extends TimerTask {
        @Override
        public void run() {
            try {
                runCounter++;
                if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Run Counter: " + runCounter);
                }

                String dirName = mCrashPath;
                if (dirName == null)
                {
                    // Note: this will point to internalstorage in case of Galaxy Tab (/mnt/sdcard/) instead of the real external sdcard.
                    String sdcardName = Environment.getExternalStorageDirectory().getAbsolutePath();
                    dirName = sdcardName + File.separator + COLLECTOR_SUBDIR;
                }
                File sdcard = new File(dirName);

                File[] logFiles = getCrashLogFiles(sdcard);
                if (logFiles != null) {
                    int numberFiles = logFiles.length;

                    if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Found " + numberFiles + " log files");
                    }

                    if (numberFiles > 0) {
                        if (isOnline() && ping(SERVER_HOSTNAME)) {
                            // Upload logfiles to the server and create backup
                            // logfiles
                            int uploadedFileCount = UploadAndRenameLogFiles(
                                    logFiles, SERVERURL);
                            Log.v(TAG, "Uploaded " + uploadedFileCount + "/"
                                    + numberFiles + " log files to server");
                        } else {
                            // Purge old logfiles
                            int purgedFiles = PurgeLogFiles(logFiles,
                                    KEEP_DAYS_NEWFILES);
                            Log.v(TAG, "Purged: " + purgedFiles + "/"
                                    + numberFiles + " log files");
                        }
                    }
                }

                File[] bakFiles = getCrashLogBackupFiles(sdcard);
                if (bakFiles != null) {
                    // Purge backup logfiles
                    int numberFiles = bakFiles.length;
                    if (numberFiles > 0) {
                        int purgedFiles = PurgeLogFiles(bakFiles,
                                KEEP_DAYS_UPLOADED);
                        Log.v(TAG, "Purged: " + purgedFiles + "/"
                                + numberFiles + " backup files");
                    }
                }
            } catch (Throwable t) { // you should always ultimately catch all
                                    // exceptions in timer tasks.
                Log.e("TimerTick", "Timer Tick Failed.", t);
            }
        }
    }

    /**
     * Used to upload newly created NavKit crashlogs from versioned builds to
     * the crashlog server. Successfully uploaded crashlog files and non-
     * version build files get renamed from '.txt' --> '.txt.bak'
     *
     * Only process a maximum of MAX_QUEUE_LENGTH files in any one run
     *
     * @param logFileList
     *            File list of crashlogs to be uploaded
     * @param urlString
     *            URL of the crashlog server
     * @return number of successfully uploaded files
     */
    private int UploadAndRenameLogFiles(File[] logFileList, String urlString) {
        // Checks the crashlog servers URL is not malformed
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e1) {
            Log.e(TAG, "Malformed url: " + urlString);
            return 0;
        }

        long minimumAge = System.currentTimeMillis() - MINAGE;
        int uploadedFileCount = 0;

        int counter = 0;
        for (File logFile : logFileList) {
            counter += 1;
            if (counter > MAX_QUEUE_LENGTH) {
                break;
            }

            String logFilename = logFile.getName();

            // Parse the build version number. The build version should
            // consist of 4 sets of numbers separated by decimal points
            // (for example:   13.100.123456.2)
            //
            // If the 3rd number is a non-zero number then this is a
            // versioned build
            //
            // Only versioned builds are uploaded to the navkit-logs server
            int begin = logFilename.indexOf("(");
            int end = logFilename.indexOf(")");
            if (begin >= 0 && begin < end) {
		        String version_number = logFilename.substring(begin + 1, end);
		        String[] versionParts = version_number.split("\\.");
		
		        // Check to see if this crashdump came from a versioned build
		        int p4_chgnum = 0;
		        if(versionParts.length == 4) {
		            try {
		                p4_chgnum = Integer.parseInt(versionParts[2]);
		            } catch (NumberFormatException e) {
		                if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
		                    Log.d(TAG, "Bad version number: " + version_number);
		                }
		            }
		        }
		        else {
		            if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
		                Log.d(TAG, "Bad versioning number: " + version_number);
		            }
		        }
		
		        boolean upload_file = (p4_chgnum != 0);
		        if (upload_file) {
		            // Check age of file
		            if (logFile.lastModified() >= minimumAge) {
		                if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
		                    Log.d(TAG, "LogFile: " + logFile.getName()
		                            + " (too recent to send)");
		                }
		            } else if (upload_file(url, logFile)) {
		                uploadedFileCount++;
		                rename_logfile(logFile);
		            }
		        } else {
		            rename_logfile(logFile);
		        }
		    }
        }
        return uploadedFileCount;
    }

    /**
     * Used to rename the crashlog files to prevent future upload attempts
     */
    private void rename_logfile(File logFile) {
        File newName = new File(logFile.getAbsoluteFile() + ".bak");
        if (!logFile.renameTo(newName)) {
            // newName.setLastModified(modifiedTime);
            if (DBG && Log.isLoggable(TAG, Log.ERROR)) {
                Log.e(TAG, "Failed to rename uploaded log file " + logFile
                        + " --> " + newName);
            }
        }
    }

    /**
     * Used to upload a file to the navkit-logs servers
     *
     * @param url
     *            URL of the crashlog server
     * @param logFile
     *            Filename to upload
     * @return true if successfully uploaded, false otherwise
     */
    private boolean upload_file(URL url, File logFile) {
        final String EOL = "\r\n";
        final String BOUNDARY = "*****";
        final String summaryLine =
                "------------------------" + EOL
                + "MANUFACTURER: " + android.os.Build.MANUFACTURER + EOL
                + "MODEL: " + android.os.Build.MODEL + EOL
                + "VERSION: " + android.os.Build.VERSION.RELEASE + EOL
                + "BUILD ID: " + android.os.Build.DISPLAY + EOL
                + "ANDROID ID: " + getAndroidID() + EOL
                + "------------------------" + EOL;

        boolean upload_status = false;
        HttpURLConnection connection = null;
        FileInputStream in = null;
        DataOutputStream out = null;
        byte[] buffer = new byte[1024];
        int bytesRead;

        // ------------------------------------------------------------
        // Setup connection, this may fail if server is unreachable
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + BOUNDARY);
            out = new DataOutputStream(connection.getOutputStream());
        } catch (IOException e) {
            if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Connection to server failed");
            }
            return upload_status;
        }

        // ------------------------------------------------------------
        // Double check that something else has deleted our logfile
        try {
            in = new FileInputStream(logFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Could not find logfile: '" + logFile + "'");
            return upload_status;
        }

        // ------------------------------------------------------------
        /* Upload file to server */
        Log.i(TAG, "Upload: " + logFile.getName());

        try {
            // HTML header lines
            out.writeBytes(EOL + "--" + BOUNDARY + EOL);
            out.writeBytes("Content-Disposition: form-data;"
                    + "name=\"" + CGI_SCRIPT_NAME + "\";"
                    + "filename=\"" + logFile.getName() + "\""
                    + EOL + EOL);

            // HTML Body -- Android device summary lines
            out.writeBytes(summaryLine);

            // HTML Body -- Crash log data
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.writeBytes(EOL + "--" + BOUNDARY + "--" + EOL);

            in.close();
            out.close();

            // ------------------------------------------------------------
            /* Check response and delete file if upload was successful */
            int response = connection.getResponseCode();
            if (response == 200) {
                DataInputStream answer = new DataInputStream(
                        connection.getInputStream());
                upload_status = "LOGFILEUPLOADOK".equals(answer.readLine());
                answer.close();
            } else if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Received invalid response from server");
            }
        } catch (IOException e) {
            if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Error while talking to server");
            }
        }
        return upload_status;
    }

    /**
     * Purges crashlog files that are older than the retention period
     *
     * @param purgeFileList
     *            a list of File objects to be checked and possibly purged
     * @param keepdays
     *            the number of days an item is kept before it is purged
     * @return number of successfully purged files
     */
    private int PurgeLogFiles(File[] purgeFileList, int keepdays) {
        long timeNow = System.currentTimeMillis();
        long minimumAge = timeNow - MINAGE - (PERDAY * keepdays);
        int purgedFilesCount = 0;

        int counter = 0;
        for (File logFile : purgeFileList) {
            counter += 1;
            if (counter > MAX_QUEUE_LENGTH) {
                break;
            }

            long fileAgeInMilliseconds = logFile.lastModified();
            if (fileAgeInMilliseconds >= minimumAge) {
                if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                    long fileAgeInDays = (timeNow - fileAgeInMilliseconds)
                        / PERDAY;
                    Log.d(TAG,
                            "LogFile: " + logFile.getName()
                                    + " (too recent to purged [Age:"
                                    + String.format("%d", fileAgeInDays)
                                    + " days])");
                }
                continue;
            }

            if (logFile.delete()) {
                if (DBG && Log.isLoggable(TAG, Log.INFO)) {
                    Log.i(TAG, "Purge: " + logFile.getName());
                }
                purgedFilesCount++;
            } else {
                if (DBG && Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, "Failed to delete file: " + logFile.getName());
                }
            }
        }
        return purgedFilesCount;
    }

    /**
     * Returns the list of unprocessed NavKit crashlogs files (ie ones that
     * haven't yet been uploaded to the crash log server). New crashlogs are
     * created by the CrashHandler and initially have a '.txt' file extension.
     * Once they have been successfully uploaded to the crash log server they
     * are renamed to have a '.txt.bak' extension.
     *
     * @param logFileDirectory
     *            directory holding the crash logs
     * @return the 'new' crash log files (or null if no valid logs are found)
     */
    private File[] getCrashLogFiles(File logFileDirectory) {
        if (logFileDirectory.exists()) {
            FilenameFilter mFilter = new FilenameFilter() {
                public boolean accept(File logFileDirectory, String fileName) {
                    return fileName.endsWith(CRASH_LOGS);
                }
            };
            return logFileDirectory.listFiles(mFilter);
        } else {
            if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Directory: " + logFileDirectory.getName()
                        + " not found");
            }
            return null;
        }
    }

    /**
     * Returns the list of old crashlogs (ie ones that have already been
     * uploaded to the crashlog server but still remain on the device).
     * Initially crashlogs are created with the '.txt' file extension but after
     * they have been successfully uploaded to the crash log server they are
     * renamed to have a '.txt.bak' extension.
     *
     * @param logFileDirectory
     *            directory holding the crash logs
     * @return the 'old' crash log files (or null if no valid logs are found)
     */
    private File[] getCrashLogBackupFiles(File dir) {
        if (dir.exists()) {
            FilenameFilter mFilter = new FilenameFilter() {
                public boolean accept(File dir, String fileName) {
                    return fileName.endsWith(BACKUP_LOGS);
                }
            };
            return dir.listFiles(mFilter);
        } else {
            if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Directory: " + dir.getName() + " not found");
            }
            return null;
        }
    }

    /**
     * Used to check if a given machine is pingable
     *
     * @param host
     *            fully qualified hostname
     * @return <code>true</code> if the host is reachable; <code>false</code>
     *         otherwise
     */
    public Boolean ping(String host) {
        final int timeout = 500;
        try {
            InetAddress addr = InetAddress.getByName(host.toString());
            Boolean pingable = addr.isReachable(timeout);
            if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Ping: " + host + " (OK)");
            }
            return pingable;
        } catch (UnknownHostException e) {
            if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Ping: " + host + " (FAILED, UnknownHostException)");
            }
            return false;
        } catch (IOException e) {
            if (DBG && Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Ping: " + host + " (FAILED, IOException)");
            }
            return false;
        }
    }

    /**
     * Used to check the current network status of the Android device
     *
     * @return <code>true</code> if online; <code>false</code> otherwise
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isAvailable() && netInfo
                .isConnected());
    }

    /**
     * Gets the Android device ID
     *
     * @return Secure.ANDROID_ID
     */
    public String getAndroidID() {
        return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
    }
}
