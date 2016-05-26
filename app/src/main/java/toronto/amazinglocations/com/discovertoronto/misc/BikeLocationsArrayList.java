/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.misc;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class BikeLocationsArrayList extends ArrayList<LatLng> {
    private static final String CLASS = BikeLocationsArrayList.class.getSimpleName();

    public ArrayList<LatLng> getNClosestLatLngPairs(Location fromLocation, int n) {
        Log.i(CLASS, "getNClosestLatLngPairs()");
        // The ArrayList that would be returned.
        ArrayList<LatLng> nClosestLatLngPairs = new ArrayList<LatLng>();
        // A copy of this ArrayList.
        ArrayList<LatLng> copy = new ArrayList<LatLng>();

        // Copying the contents of this ArrayList to 'copy'.
        for(int i = 0; i < size(); i++) {
            copy.add(get(i));
        }

        for(int i = 0; i < n; i++) {
            // Setting the closest LatLng to the first element of this ArrayList.
            LatLng closestLatLng = copy.get(0);
            // After each pass through the inner loop, the next closest LatLng is identified
            // and added to the ArrayList 'nClosestLatLngPairs'.
            for(int j = 1; j < copy.size(); j++) {
                // Creating Location object based on 'closestLatLng'.
                Location closestLocation = new Location("");
                closestLocation.setLatitude(closestLatLng.latitude);
                closestLocation.setLongitude(closestLatLng.longitude);

                // Creating Location based on current LatLng.
                Location currentArrayListLocation = new Location("");
                currentArrayListLocation.setLatitude(copy.get(j).latitude);
                currentArrayListLocation.setLongitude(copy.get(j).longitude);

                // If distance between 'fromLocation' and 'currentArrayListLocation' is less than the distance between 'fromLocation'
                // and closestLocation, then closestLatLng gets set to the jth element of 'copy'.
                if (distanceBetween(fromLocation, currentArrayListLocation) < distanceBetween(fromLocation, closestLocation)) {
                    closestLatLng = copy.get(j);
                }
            }

            // Removing this LatLng from 'copy' so that it does not come up in next search.
            copy.remove(closestLatLng);
            // Adding to the ArrayList to be returned.
            nClosestLatLngPairs.add(closestLatLng);
        }

        return nClosestLatLngPairs;
    }

    private double distanceBetween(Location loc1, Location loc2) {
        return loc1.distanceTo(loc2);
    }
}
