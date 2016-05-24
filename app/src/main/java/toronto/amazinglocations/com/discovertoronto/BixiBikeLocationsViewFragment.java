package toronto.amazinglocations.com.discovertoronto;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import toronto.amazinglocations.com.discovertoronto.misc.BikeLocationsArrayList;
import toronto.amazinglocations.com.discovertoronto.misc.BikesLocationReaderAsyncTask;

public class BixiBikeLocationsViewFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private static final String CLASS = BixiBikeLocationsViewFragment.class.getSimpleName();
    private static BikeLocationsArrayList sBikeStandLocationLatLngPairs = null;
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mMap;
    private LatLngBounds.Builder mBuilder;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int mLocationUpdateInterval = 5000;
    private int mNumOfBikeStands = 1;
    private boolean mBixiLocationsShown = true;
    private ImageView mShowMoreBikeStandLocationsImageView;
    private ImageView mShowLessBikeStandLocationsImageView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(CLASS, "onCreateView()");

        View v = inflater.inflate(R.layout.fragment_bixi_bike_locations_view, container, false);

        // Pressing on 'mShowMoreBikeStandLocationsImageView' shows more bike locations.
        mShowMoreBikeStandLocationsImageView = (ImageView)v.findViewById(R.id.showMoreBikeStandLocationsImageView);
        mShowMoreBikeStandLocationsImageView.setOnClickListener(this);

        // // Pressing on 'mShowLessBikeStandLocationsImageView' shows less bike locations.
        mShowLessBikeStandLocationsImageView = (ImageView)v.findViewById(R.id.showLessBikeStandLocationsImageView);
        mShowLessBikeStandLocationsImageView.setOnClickListener(this);

        return v;
    }

    public void onResume() {
        super.onResume();
        Log.i(CLASS, "onResume()");

        // Getting the child map Fragment and getting it ready by calling getMapAsync().
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.bixiMap);
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
        // Using AsyncTask to fetch bixi bike locations. The information returned is stored in the static
        // 'sBikeStandLocationLatLngPairs'
        if (sBikeStandLocationLatLngPairs == null) {
            new BikesLocationReaderAsyncTask(postReadBikesLocationHandler).execute("http://www.bikesharetoronto.com/stations/json");
        }

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

        // Needed for boundary object within which all Markers will appear.
        if (mBixiLocationsShown && sBikeStandLocationLatLngPairs != null) {
            // If user clicks on the '+' button too many times, 'mNumOfBikeStands' might be bigger than 'sBikeStandLocationLatLngPairs'.
            // The value of 'mNumOfBikeStands' should not be bigger than 'sBikeStandLocationLatLngPairs'.
            if (mNumOfBikeStands > sBikeStandLocationLatLngPairs.size()) {
                mNumOfBikeStands = sBikeStandLocationLatLngPairs.size();
            }
            // Getting the closest 'mNumOfBikeStands' many bike stands starting with the closest one.
            ArrayList<LatLng> nClosestLatLngPairs = sBikeStandLocationLatLngPairs.getNClosestLatLngPairs(currentUserLocation, mNumOfBikeStands);

            for (int i = 0; i < nClosestLatLngPairs.size(); i++) {
                // Creating Marker from LatLng object at index i of the ArrayList 'mBikeLocationLatLngPairs'.
                Marker bikeStandMarker = mMap.addMarker(new MarkerOptions()
                        .position(nClosestLatLngPairs.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                mBuilder.include(bikeStandMarker.getPosition());
            }
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

    private final Handler postReadBikesLocationHandler = new Handler() {
        public void handleMessage(Message message) {
            sBikeStandLocationLatLngPairs = new BikeLocationsArrayList();

            try {
                // Creating the root level JSON object from the String returned by the AsyncTask.
                JSONObject bikesLocationDetails = new JSONObject(message.getData().getString("bikesLocationDetails"));
                // Retrieving the 'stationBeanList' JSONArray that contains the details of the bike-stands.
                JSONArray stationBeanList = bikesLocationDetails.optJSONArray("stationBeanList");
                // Making pass through the JSONArray to retrieve the details of each bike stand.
                for(int i = 0; i < stationBeanList.length(); i++) {
                    // Retrieving the bike-stand details at index i.
                    JSONObject stationBeanElement = stationBeanList.getJSONObject(i);
                    // Retrieving the latitude and longitude of the bike-stand at index i.
                    double latitude = Double.parseDouble(stationBeanElement.optString("latitude"));
                    double longitude = Double.parseDouble(stationBeanElement.optString("longitude"));
                    // Creating a LatLng object from the latitude and longitude and adding it to the
                    // ArrayList 'mBikeLocationLatLngPairs'.
                    sBikeStandLocationLatLngPairs.add(new LatLng(latitude, longitude));
                }

                updateMap(mLastLocation);
            }
            catch(JSONException ex) {

            }
        }
    };

    public void onClick(View view) {
        int id = view.getId();
        // If user clicked on the '+' button.
        if (id == mShowMoreBikeStandLocationsImageView.getId()) {
            mNumOfBikeStands++;
        }
        // If user clicked on the '-' button.
        else if (id == mShowLessBikeStandLocationsImageView.getId()) {
            if (mNumOfBikeStands > 1) {
                mNumOfBikeStands--;
            }
        }
        // Update the map to show the new number of bike stands.
        updateMap(mLastLocation);
    }
}
