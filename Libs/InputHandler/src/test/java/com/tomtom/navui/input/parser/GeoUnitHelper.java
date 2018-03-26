package com.tomtom.navui.input.parser;

import android.content.Intent;
import android.net.Uri;

public class GeoUnitHelper {

    private GeoUnitHelper() {
    }
    

    public static Intent createIntentfromUrl(final String uriString) {
        final Intent intent = new Intent();
        final Uri data = Uri.parse(uriString);
        intent.setData(data); 
        return intent;
    }
    
}
