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

import java.util.ArrayList;
import java.util.List;
import android.content.pm.PackageManager;
import android.content.Context;
import android.util.Log;

public class NavKitPermissionUtil {
    
    public final static String TAG = NavKitPermissionUtil.class.getSimpleName();
    
    private class PermissionStatus {
        String mPermissionName;  
        int mPermissionGrantCode = PackageManager.PERMISSION_DENIED;
        
        public PermissionStatus(String aPermissionName) {
            this.mPermissionName = aPermissionName;
        } 
    }
    
    private List<PermissionStatus> mPermissionStatusList = new ArrayList<PermissionStatus>(); 
    
    private Context mContext;
    
    private void initializeDangerousPermissionStatus() {
        mPermissionStatusList.add(new PermissionStatus("android.permission.WRITE_EXTERNAL_STORAGE"));
        mPermissionStatusList.add(new PermissionStatus("android.permission.READ_PHONE_STATE"));
        mPermissionStatusList.add(new PermissionStatus("android.permission.ACCESS_FINE_LOCATION"));
    }
    
    public NavKitPermissionUtil(Context aContext) {
        this.mContext = aContext;
        initializeDangerousPermissionStatus();
        checkRequiredPermissions();
    }
    
    private void checkRequiredPermissions() {
        for (PermissionStatus permissionStatus : mPermissionStatusList) {
            permissionStatus.mPermissionGrantCode = mContext.checkPermission(permissionStatus.mPermissionName, android.os.Process.myPid(), android.os.Process.myUid());
            Log.d(TAG, "Permission check for " + permissionStatus.mPermissionName + " is " + 
                (permissionStatus.mPermissionGrantCode == PackageManager.PERMISSION_DENIED ? "DENIED" : "GRANTED"));
        }
    }
    
    public List<String> getNonGrantedDangerousPermissions() {
        List<String> nonGrantDangerousPermissionList = new ArrayList<String>();
        for (PermissionStatus permissionStatus : mPermissionStatusList) {
            if (permissionStatus.mPermissionGrantCode == PackageManager.PERMISSION_DENIED) {
                nonGrantDangerousPermissionList.add(permissionStatus.mPermissionName);
            }
        }
        return nonGrantDangerousPermissionList;
    }
}

