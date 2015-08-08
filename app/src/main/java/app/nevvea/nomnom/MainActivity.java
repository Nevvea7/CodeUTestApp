package app.nevvea.nomnom;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.beardedhen.androidbootstrap.BootstrapButton;
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


public class MainActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        MainActivityFragment.Callback {

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;
    double curLongitude;
    double curLatitude;

    BootstrapButton getResultButton;


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

        // pass the GoogleApiClient to MainActivityFragment
        mFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.activity_fragment);
        mFragment.setmGoogleApiClient(mGoogleApiClient);

        getResultButton = (BootstrapButton) findViewById(R.id.get_location_button);
        getResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGoogleApiClient.isConnected()) {

                    curLatitude = mMap.getCameraPosition().target.latitude;
                    curLongitude = mMap.getCameraPosition().target.longitude;

                    mFragment.setLatLng(curLatitude, curLongitude);
                    mFragment.onLocationChaged(curLatitude, curLongitude);

                } else {
                    //TODO say that internet is not connected
                }
            }
        });

    }

    /**
     * Helper method to build the GoogleApiClient
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    // TODO: record the marker/restaurant results and show it when activity is resumed.
    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        Log.d("activity check", "on resume called");
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
        Log.d("activity check", "on pause called");
    }


    /**
     * Once the map is ready, pass the reference to MainActivityFragment.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);
        mFragment.setmMap(googleMap);

    }

    /**
     * On camera change, record the new lat/lng
     * If implement the location bar this method will be useful.
     * @param cameraPosition the new camera position from which we can get our latlng
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        curLongitude = cameraPosition.target.latitude;
        curLatitude = cameraPosition.target.longitude;
    }

    /**
    * Implementation of {@link com.google.android.gms.location.LocationListener}.
    */
    @Override
    public void onLocationChanged(Location location) {
        curLongitude = location.getLongitude();
        curLatitude = location.getLatitude();
    }

    /**
     * Callback called when connected to GCore.
     * Implementation of {@link com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks}.
     *
     * Once connected, get the lat/lng of the current location and set the map's focus
     * to the current location.
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLatitude, curLongitude), 15.5f));


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
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
            }

    /**
     * Callback from MainActivityFragment to start other Activities
     * @param mUri uri with a restaurant's id. Passed to DetailActivity so that DetailActivity
     *             can query the database with the url
     */
    @Override
    public void onItemSelected(Uri mUri) {
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(mUri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // start the blacklist activity
        if (id == R.id.action_blacklist) {
            startActivity(new Intent(this, BlackListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
