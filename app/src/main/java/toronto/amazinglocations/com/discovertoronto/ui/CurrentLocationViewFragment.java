/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.ui;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import toronto.amazinglocations.com.discovertoronto.misc.OptimizedImageLoader;

public class CurrentLocationViewFragment extends Fragment implements OnMapReadyCallback {
    private static final String CLASS = CurrentLocationViewFragment.class.getSimpleName();
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private LatLngBounds.Builder mBuilder;
    private boolean mDefaultZoomReached = false;
    private BitmapDescriptor mSelf;
    private int mLocationUpdateInterval = 5000;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(CLASS, "onCreateView()");
        View v = inflater.inflate(R.layout.fragment_current_location_view, container, false);

        return v;
    }

    public void onResume() {
        super.onResume();
        Log.i(CLASS, "onResume()");

        FragmentManager fm = getChildFragmentManager();

        // Getting the child map Fragment by tag.
        mSupportMapFragment = (SupportMapFragment)fm.findFragmentByTag("mapFragment");
        if (mSupportMapFragment == null) {
            mSupportMapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapHolderLayout, mSupportMapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mSupportMapFragment.getMapAsync(this);
    }

    public void onPause() {
        Log.i(CLASS, "onPause()");
        // Stopping the Location services.
        terminateGoogleApiClient();
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(CLASS, "onMapReady()");

        mMap = googleMap;
        // Disabling map scrolling.
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        // Retrieving the layout inflater service and inflating the self_marker_layout.
        View selfMarker = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.self_marker_layout, null);
        // Retrieving a handle to 'markerImageView'.
        ImageView markerImageView = (ImageView)selfMarker.findViewById(R.id.markerImageView);
        // Setting the Bitmap for 'markerImageView'.
        markerImageView.setImageBitmap(OptimizedImageLoader.decodeSampledBitmapFromResource(getResources(), R.drawable.marker_position, 15, 15));

        mSelf = BitmapDescriptorFactory.fromBitmap(RoundImage.createDrawableFromView(getActivity(), selfMarker));

        // Starts Google Location services.
        buildGoogleApiClient();
    }

    public void updateMap(Location currentUserLocation) {
        Log.i(CLASS, "updateMap()");

        if(getActivity() == null) {
            return;
        }

        mMap.clear();

        // Adding this user's current Location Marker to the map.
        if (currentUserLocation != null) {
            // Retrieving user's current latitude and longitude, and creating LatLng object from the pair.
            LatLng currentLatLng = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
            // Creating marker on the map representing user's current location.
            Marker self = mMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .icon(mSelf));
            // If the view is zoomed once, it would not zoom again until the Fragment's onDestroy() is called.
            if (!mDefaultZoomReached) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()), 14.0f));
                mDefaultZoomReached = !mDefaultZoomReached;
            }
        }
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
