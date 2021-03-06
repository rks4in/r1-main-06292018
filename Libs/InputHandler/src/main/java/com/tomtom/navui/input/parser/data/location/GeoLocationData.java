
package com.tomtom.navui.input.parser.data.location;

import com.tomtom.navui.input.parser.data.ParsedData;

public class GeoLocationData extends ParsedData {

    private static final String TAG = "GeoLocationData";

    private final int mLatitude;
    private final int mLongitude;

    public GeoLocationData(final int latitude, final int longitude) {
        super();
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public int getLatitude() {
        return mLatitude;
    }

    public int getLongitude() {
        return mLongitude;
    }

    @Override
    public String toString() {
        return TAG + " ll=( " + mLatitude + ", " + mLongitude + " )";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mLatitude;
        result = prime * result + mLongitude;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeoLocationData other = (GeoLocationData) obj;
        if (mLatitude != other.mLatitude) {
            return false;
        }
        if (mLongitude != other.mLongitude) {
            return false;
        }
        return true;
    }

}
