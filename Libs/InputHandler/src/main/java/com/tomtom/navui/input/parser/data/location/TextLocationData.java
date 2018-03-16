
package com.tomtom.navui.input.parser.data.location;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tomtom.navui.input.parser.data.ParsedData;

/**
 * Class describe location, it can be City, address, home number or place name.
 * 
 */
public class TextLocationData extends ParsedData {

    private static final String LOCATION_SEPARATOR = " ";
    private final String mTextLocation;

    public TextLocationData(final String textLocation) {
        super();
        this.mTextLocation = normalizeLocation(textLocation);
    }

    private String normalizeLocation(final String textLocation) {
        final Pattern removeNewLinesPattern = Pattern.compile("\\r\\n|\\r|\\n");
        final Matcher matcher = removeNewLinesPattern.matcher("");
        final String normalizedLocation = matcher.reset(textLocation).replaceAll(LOCATION_SEPARATOR);
        return normalizedLocation;
    }

    public String getTextLocation() {
        return mTextLocation;
    }

    public static TextLocationData create(final String textQuery) {
        return new TextLocationData(textQuery);
    }

    @Override
    public String toString() {
        return getTextLocation();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mTextLocation == null) ? 0 : mTextLocation.hashCode());
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
        final TextLocationData other = (TextLocationData) obj;
        if (mTextLocation == null) {
            if (other.mTextLocation != null) {
                return false;
            }
        } else if (!mTextLocation.equals(other.mTextLocation)) {
            return false;
        }
        return true;
    }
}
