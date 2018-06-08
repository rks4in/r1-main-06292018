package com.tomtom.navkit;

interface NavKitLifelineRemoteBinder {

    /** Request non granted dangerous permissions from NavKit */
    List<String> getNonGrantedDangerousPermissions();
}
