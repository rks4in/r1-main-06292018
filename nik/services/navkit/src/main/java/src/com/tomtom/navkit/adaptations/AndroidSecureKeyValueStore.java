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
import android.util.Log;
import android.provider.Settings.Secure;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStoreException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Provides access to the secure Key store of Android.
 */
public class AndroidSecureKeyValueStore {
    private static final String TAG = AndroidSecureKeyValueStore.class.getSimpleName();

    private Context context = null;

    private static File originalFile;
    private static File backupFile;
    private KeyStore ks;

    public AndroidSecureKeyValueStore(Context context) {
        Log.i(TAG, "AndroidSecureKeyValueStore created");
        this.context = context;
        originalFile = new File(context.getFilesDir(), "confidential.keystore");
        backupFile = new File(context.getFilesDir(), "confidential.keystore_write");
    }

    /**
     * Store a key in the key store and save the store.
     * 
     * @param alias The alias (identifying name) for the key entry.
     * @param keyValues The key data values.
     * @return True if the key was successfully stored, false otherwise.
     */
    public boolean storeValue(String alias, byte[] keyValues) {
        Log.i(TAG, "storing value for " + alias);

        // Initialize key store if not already done.
        if (ks == null) {
            if (!initializeKeyStore()) {
                return false;
            }
        }
        try {
            // Add key to key store.
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyValues, "AES");
            PasswordProtection pass = new PasswordProtection(getKeyPassword());
            KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(secretKeySpec);
            ks.setEntry(alias, skEntry, pass);
            Log.v(TAG, "key added to store");

            // Save key store.
            if (context == null) {
                Log.e(TAG, "AndroidKeyValueStore created without context object, cannot store keys");
                return false;
            }
            // Store it as a separate file until it's properly written, to prevent half-written stores.
            // Logic taken from Android API 17's AtomicFile.
            if (originalFile.exists()) {
                if (!backupFile.exists()) {
                    if (!originalFile.renameTo(backupFile)) {
                        Log.e(TAG, "Couldn't rename key store file, fatal!");
                        return false;
                    }
                } else {
                    // Not the backup, because this new original may have been incomplete.
                    originalFile.delete();
                }
            }
            OutputStream writeStream = context.openFileOutput(originalFile.getName(), Context.MODE_PRIVATE);
            ks.store(writeStream, getStorePassword());
            //FileUtils.sync(writeStream);
            try {
                writeStream.close();
                backupFile.delete();
            } catch (Exception e) {
                Log.e(TAG, "Received error closing keystore file: ", e);
                return false;
            }
            Log.v(TAG, "key store written to file");
        } catch (Exception e) {
            Log.e(TAG, "Storing key failed", e);
            return false;
        }
        return true;
    }

    /**
     * Retrieve a key from the key store.
     * 
     * @param alias The alias (identifying name) for the key entry.
     * @return The key data values if the key was successfully retrieved, null otherwise.
     */
    public byte[] retrieveValue(String alias) {
        Log.d(TAG, "retrieving value for " + alias);

        // Initialize key store if not already done.
        if (ks == null) {
            if (!initializeKeyStore()) {
                return null;
            }
        }

        // Retieve key
        byte[] keyValues = null;
        try {
            Key retrieveKey = ks.getKey(alias, getKeyPassword());
            if (retrieveKey == null) {
                Log.w(TAG, "retrieving value for " + alias + " returned null");
            } else {
                keyValues = retrieveKey.getEncoded();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "retrieving value for " + alias + " failed");
        }

        return keyValues;
    }

    /**
     * Initialize the key store. Load from file if that is present.
     * 
     * @return True if the key store was successfully initialized, false otherwise.
     */
    private boolean initializeKeyStore() {
        // Get key store instance.
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            e.printStackTrace();
            Log.w(TAG, "getting key store instance failed");
            return false;
        }

        // Setup key store.
        try {
            if (backupFile.exists()) {
                originalFile.delete();
                backupFile.renameTo(originalFile);
            }
            if (originalFile.exists()) {
                // Load key store from file.
                InputStream readStream = context.openFileInput(originalFile.getName());
                ks.load(readStream, getStorePassword());
                readStream.close();
            }
            else {
                // Only initialize the key store.
                ks.load(null, getStorePassword());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "initializing key store failed, resetting. All state will be lost");
            if (!originalFile.delete()) {
                Log.e(TAG, "Could not delete old corrupt key store file! Not recoverable!");
                ks = null;
                return false;
            } else {
                try {
                    ks.load(null, getStorePassword());
                    return true;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    Log.e(TAG, "Resetting key store also failed. Not recoverable!");
                    ks = null;
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get the password to store and retrieve keys.
     * 
     * @return The password.
     */
    private char[] getKeyPassword() {
        return Secure.ANDROID_ID.toCharArray();
    }

    /**
     * Get the password access the key store.
     * 
     * @return The password.
     */
    private char[] getStorePassword() {
        return Secure.ANDROID_ID.toCharArray();
    }

}
