/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.misc;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;

public class LocationEnabledChecker {
    public static boolean isLocationEnabled(Activity context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        // Checking if gps location is enabled.
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex) {}
        // Checking if network location is enabled.
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex) {}

        // If both are disabled return false.
        if (!gpsEnabled && !networkEnabled) {
            return false;
        }
        // Else return true.
        return true;
    }
}
