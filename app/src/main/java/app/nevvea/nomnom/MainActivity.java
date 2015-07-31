package app.nevvea.nomnom;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback {

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;
    Marker marker;
    double curLongitude;
    double curLatitude;

    TextView mCameraTextView;

    Location mLocation;

    MainActivityFragment mFragment;

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mCameraTextView = (TextView) findViewById(R.id.camera_position);

        mFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.activity_fragment);
        mFragment.setmGoogleApiClient(mGoogleApiClient);

    }
        private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    @Override
    public void onResume() {
            super.onResume();
            mGoogleApiClient.connect();
            }

    @Override
    public void onPause() {
            super.onPause();
            mGoogleApiClient.disconnect();
            }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);
        mFragment.setmMap(googleMap);

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        marker.setPosition(cameraPosition.target);
        mCameraTextView.setText(cameraPosition.toString());
    }

    /**
    * Implementation of {@link com.google.android.gms.location.LocationListener}.
    */
    @Override
    public void onLocationChanged(Location location) {
        curLongitude = location.getLongitude();
        curLatitude = location.getLatitude();

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLatitude, curLongitude), 16));

        }

    /**
     * Callback called when connected to GCore. Implementation of {@link com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
        mGoogleApiClient,
        REQUEST,
        this);  // LocationListener;

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        curLongitude = mLocation.getLongitude();
        curLatitude = mLocation.getLatitude();

        // TODO: check if map is null
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLatitude, curLongitude), 16));
        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(curLatitude, curLongitude)));


    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnectionSuspended(int cause) {
            // Do nothing
        }

    /**
     * Implementation of {@link com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
        }

    @Override
    public boolean onMyLocationButtonClick() {
            Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
            }
}
