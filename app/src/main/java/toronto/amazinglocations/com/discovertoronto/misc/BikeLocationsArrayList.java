package toronto.amazinglocations.com.discovertoronto.misc;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class BikeLocationsArrayList extends ArrayList<LatLng> {
    private static final String CLASS = BikeLocationsArrayList.class.getSimpleName();

    public ArrayList<LatLng> getNClosestLatLngPairs(Location fromLocation, int n) {
        Log.i(CLASS, "getNClosestLatLngPairs()");
        ArrayList<LatLng> latLngPairs = new ArrayList<LatLng>();
        ArrayList<LatLng> copy = new ArrayList<LatLng>();

        // Copying the contents of this ArrayList to 'copy'.
        for(int i = 0; i < size(); i++) {
            copy.add(get(i));
        }

        for(int i = 0; i < n; i++) {
            // Setting the closest LatLng to the first element of this ArrayList.
            LatLng closestLatLng = copy.get(0);

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
                // and closestLocation.
                if (distanceBetween(fromLocation, currentArrayListLocation) < distanceBetween(fromLocation, closestLocation)) {
                    closestLatLng = copy.get(j);
                }
            }

            copy.remove(closestLatLng);
            latLngPairs.add(closestLatLng);
        }

        return latLngPairs;
    }

    private double distanceBetween(Location loc1, Location loc2) {
        return loc1.distanceTo(loc2);
    }
}
