#!/bin/bash

# check if the device is connected
function checkIfDeviceConnected(){
    timeout 5 adb wait-for-device
    adb get-state 1 | if grep "device"; then return 0; else return 1; fi ;
}

# check if the device is R1 device or anyother device
function checkDeviceType(){
    adb devices | if grep -qi "R1J.*"; then return 0; else return 1; fi;
}

# uninstall the NavApp
function uninstallNavApp(){
    echo "Uninstalling the current NavApp"
    if [[ $(adb uninstall com.tomtom.r1navapp | grep Success) = Success ]]; then echo Uninstall OK; else echo Uninstall Failed; fi;
}

# start and verify NavApp
function startAndCheckNavApp(){
    rm -rf NavApp_Logging
    # clear the logcat buffer
    adb logcat -c
    adb logcat -v time > NavApp_Logging&
    logcat_pid=$!
    startTime="$(TZ=UTC0 printf '%(%s)T\n' '-1')"
    echo "Starting NavApp"
    adb shell am start -n "com.tomtom.r1navapp/.R1MainActivity"
    tail -f -n+1 NavApp_Logging | while read line
    do
        if (echo $line | grep "$1" ); then echo "TEST PASS: NAVAPP STARTED SUCCESSFULLY"; exit 0; fi;
        currentTime=$(TZ=UTC0 printf '%(%s)T\n' '-1')
        timeElapsed=$(expr $currentTime - $startTime)
        if [ $timeElapsed -gt $2 ]
            then echo "TEST FAIL: NAVAPP NOT STARTED"; cat NavApp_Logging; exit 1;
        fi
    done
    kill $logcat_pid
}

#install the NavApp
function installNavApp(){
    path=$PWD/build/artifacts/app/$1
    if [[ $(adb install -r -g $path | grep Success) = Success ]]; then echo $2 Install OK; else echo $2 Install Failed; exit 1; fi;
}

# install the NavApp
function installAndStartUpNavApp(){
    if checkIfDeviceConnected $0; then echo "Device Connected"; else echo "Device not Connected" ; exit 1; fi;
    uninstallNavApp
    if checkDeviceType $0
        then installNavApp "app-x86_64-release.apk" "x86"
        else installNavApp "app-armeabi-v7a-release.apk" "Arm"
    fi
    logString="D/SigHomeScreen(.*): onResume"
    timeout=20
    startAndCheckNavApp "$logString" $timeout
}

installAndStartUpNavApp