package toronto.amazinglocations.com.discovertoronto;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CurrentLocationViewFragment extends Fragment implements OnMapReadyCallback {
    private static final String CLASS = CurrentLocationViewFragment.class.getSimpleName();
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private LatLngBounds.Builder mBuilder;
    private int mLocationUpdateInterval = 5000;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(CLASS, "onCreateView()");
        View v = inflater.inflate(R.layout.fragment_current_location_view, container, false);

        return v;
    }

    public void onResume() {
        super.onResume();
        Log.i(CLASS, "onResume()");

        // Getting the child map Fragment and getting it ready by calling getMapAsync().
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.currentLocationMap);
        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(this);
        }
    }

    public void onPause() {
        // Stopping the Location services.
        terminateGoogleApiClient();
        // Removing this Fragment.
        getFragmentManager().beginTransaction().remove(this).commit();

        Log.i(CLASS, "onPause()");
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(CLASS, "onMapReady()");

        mMap = googleMap;
        // Disabling map scrolling.
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        // Starts Google Location services.
        buildGoogleApiClient();
    }

    public void updateMap(Location currentUserLocation) {
        Log.i(CLASS, "updateMap()");

        if(getActivity() == null) {
            return;
        }

        mMap.clear();

        // Creates a boundary in terms of latitudes and longitudes.
        mBuilder = new LatLngBounds.Builder();

        // Adding this user's current Location Marker to the map.
        if (currentUserLocation != null) {
            Marker self = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker_position)))
                    .title("You"));

            self.showInfoWindow();
            // Adding self to mBuilder.
            mBuilder.include(self.getPosition());
        }

        // Creates the boundary object.
        LatLngBounds bounds = mBuilder.build();

        // offset from edges of the map in pixels.
        int padding = 80;

        // Centering the boundary within the screen.
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
    }

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(locationServicesCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    public void terminateGoogleApiClient(){
        if (mGoogleApiClient == null) {
            return;
        }

        if(mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(mLocationUpdateInterval);
        mLocationRequest.setFastestInterval(mLocationUpdateInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    GoogleApiClient.ConnectionCallbacks locationServicesCallbacks = new GoogleApiClient.ConnectionCallbacks(){
        public void onConnected(Bundle connectionHint) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                updateMap(mLastLocation);
            }

            // Creating the location request.
            createLocationRequest();

            // Starts the location updates.
            startLocationUpdates();
        }

        public void startLocationUpdates() {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
        }

        public void onConnectionSuspended (int cause){}
    };

    GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener(){
        public void onConnectionFailed(ConnectionResult c){
        }
    };

    LocationListener locationListener = new LocationListener(){
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            updateMap(mLastLocation);
        }
    };
}
