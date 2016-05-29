/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.ui;

import android.content.Intent;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import toronto.amazinglocations.com.discovertoronto.R;
import toronto.amazinglocations.com.discovertoronto.misc.LocationEnabledChecker;
import toronto.amazinglocations.com.discovertoronto.misc.OptimizedImageLoader;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int ENABLE_LOCATION_REQUEST = 1;  // The request code.
    private boolean wasLocationSettingsActivityVisible = false;
    private GoogleMap mMap;
    private Location mLastLocation;
    private LatLngBounds.Builder mBuilder;
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest;
    private String mName;
    private double mLat;
    private double mLng;
    private BitmapDescriptor mPoint;
    private BitmapDescriptor mSelf;
    private int mLocationUpdateInterval = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    }

    protected void onResume() {
        super.onResume();

        boolean isLocationEnabled = LocationEnabledChecker.isLocationEnabled(this);
        // If Google location is not enabled, need to show the Activity from which user can enable it.
        if (!isLocationEnabled && !wasLocationSettingsActivityVisible) {
            Toast.makeText(this, getResources().getString(R.string.gps_network_not_enabled), Toast.LENGTH_LONG).show();
            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(locationSettingsIntent, ENABLE_LOCATION_REQUEST);
            return;
        }
        // Otherwise if location is enabled, continue.
        else if (isLocationEnabled) {
            wasLocationSettingsActivityVisible = false;
            enableActivity();
        }
        else {
            finish();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If location settings Activity was visible, the boolean flag would be true.
        if (requestCode == ENABLE_LOCATION_REQUEST) {
            wasLocationSettingsActivityVisible = true;
        }
    }

    protected void enableActivity() {
        Bundle bundle = getIntent().getExtras();
        mName = bundle.getString("name");
        mLat = bundle.getDouble("lat");
        mLng = bundle.getDouble("lng");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected void onPause() {
        super.onPause();

        terminateGoogleApiClient();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mSelf = BitmapDescriptorFactory.fromBitmap(OptimizedImageLoader.decodeSampledBitmapFromResource(getResources(), R.drawable.marker_position, 15, 15));
        mPoint = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

        buildGoogleApiClient();
    }

    public void updateMap(Location currentUserLocation) {
        mMap.clear();

        // Creates a boundary in terms of latitudes and longitudes.
        mBuilder = new LatLngBounds.Builder();

        if (currentUserLocation != null) {
            Marker self = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()))
                    .icon(mSelf)
                    .title("You"));

            self.showInfoWindow();
            // Adding self to mBuilder.
            mBuilder.include(self.getPosition());
        }

        Marker pointOfInterest =  mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLat, mLng))
                .icon(mPoint)
                .title(mName));
        // Adding the position of the point of interest to mBuilder.
        mBuilder.include(pointOfInterest.getPosition());

        // Creates the boundary object.
        LatLngBounds bounds = mBuilder.build();

        // offset from edges of the map in pixels.
        int padding = 80;

        // Centering the boundary within the screen.
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
    }

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
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

        if (mGoogleApiClient.isConnected()) {
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
