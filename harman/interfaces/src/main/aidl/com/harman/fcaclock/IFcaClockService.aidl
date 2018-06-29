package com.harman.fcaclock;

interface IFcaClockService {

    //Navigation interfaces
    int setTimeZoneOffset(int timeZoneOffset, String timeZoneId);    //offset in min
    int setDayLightSavingsOffset(int daylightSavingsOffset);         //offset in min

}