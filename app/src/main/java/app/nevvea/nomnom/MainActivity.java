package app.nevvea.nomnom;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yelp.clientlib.entities.Business;

import app.nevvea.nomnom.util.DialogBuilder;
import app.nevvea.nomnom.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        MainActivityFragment.Callback {

    private static final String LAT_TAG = "LAT_TAG";
    private static final String LNG_TAG = "LNG_TAG";
    private static final String LAST_FRAG_TAG = "LAST_FRAG_TAG";
    private static final String BUSI_TAG = "BUSI_TAG";

    private static final int MAIN = 0;
    private static final int ABOUT = 1;
    private static final int BLACKLIST = 2;
    private static final int FRAGMENT_COUNT = BLACKLIST + 1;
    private int lastFrag = -1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    Context mContext;
    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;
    double curLongitude;
    double curLatitude;
    Boolean tabletLayout;

    LatLng prevLatLng;
    LatLng prevMarkerLatLng;

    @BindView(R.id.get_location_button)
    BootstrapButton getResultButton;
    Button tabletHomeButton;
    Button tabletAboutButton;
    Button tabletBlacklistButton;
    @BindView(R.id.main_map_container)
    @Nullable
    RelativeLayout mapContainer;
    LinearLayout fragmentContainer;

    Location mLocation;

    MainActivityFragment mFragment;

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private static final int PERMISSION_CODE_COARSE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);
        mContext = this;

        buildGoogleApiClient();


        FragmentManager fm = getSupportFragmentManager();
        final SupportMapFragment mapFragment =
                (SupportMapFragment) fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // if it's a tablet, setup the layout accordingly
        if (findViewById(R.id.main_activity_container) != null) {
            setupTabletLayout(fm);
        } else {
            tabletLayout = false;
            // pass the GoogleApiClient to MainActivityFragment
            mFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.activity_fragment);
        }

        mFragment.setmGoogleApiClient(mGoogleApiClient);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSION_CODE_COARSE_LOCATION);
            return;
        }
    }

    private void setupTabletLayout(FragmentManager fm) {
        tabletLayout = true;

        MainActivityFragment mf = (MainActivityFragment) fm.findFragmentById(R.id.main_fragment);
        BlackListActivityFragment bf = (BlackListActivityFragment) fm.findFragmentById(R.id.blacklist_fragment);
        AboutActivityFragment af = (AboutActivityFragment) fm.findFragmentById(R.id.about_fragment);

        mFragment = mf;
        fragments[MAIN] = mf;
        fragments[ABOUT] = af;
        fragments[BLACKLIST] = bf;


        fragmentContainer = (LinearLayout) findViewById(R.id.main_activity_container);
        tabletHomeButton = (Button) findViewById(R.id.tablet_home_button);
        tabletAboutButton = (Button) findViewById(R.id.tablet_about_button);
        tabletBlacklistButton = (Button) findViewById(R.id.tablet_blacklist_button);

        showFragment(MAIN, false);

        tabletHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(MAIN, false);
                lastFrag = MAIN;
            }
        });

        tabletAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(ABOUT, false);
                lastFrag = ABOUT;
            }
        });

        tabletBlacklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(BLACKLIST, false);
                lastFrag = BLACKLIST;

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

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if (lastFrag != -1) showFragment(lastFrag, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
        double lat = mMap.getCameraPosition().target.latitude;
        double lng = mMap.getCameraPosition().target.longitude;
        prevLatLng = new LatLng(lat, lng);
    }


    /**
     * Once the map is ready, pass the reference to MainActivityFragment.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("asking", "onmap");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
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
        setupMapFocus();
    }

    private void setupMapFocus() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);  // LocationListener;

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            DialogBuilder
                    .buildAlert(mContext, getString(R.string.no_location))
                    .show();
            return;
        }
        curLongitude = mLocation.getLongitude();
        curLatitude = mLocation.getLatitude();

        MapsInitializer.initialize(this);

        if (prevLatLng == null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLatitude, curLongitude), 15.5f));

        else {
            CameraPosition movePos =
                    new CameraPosition.Builder().target(prevLatLng)
                            .zoom(15.5f)
                            .bearing(0)
                            .tilt(25)
                            .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(movePos));

            if (prevMarkerLatLng != null) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(prevMarkerLatLng));
                LatLngBounds.Builder llBuilder = LatLngBounds.builder();
                llBuilder.include(prevMarkerLatLng);
                llBuilder.include(prevLatLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llBuilder.build(), 50));
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_CODE_COARSE_LOCATION: {
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setupMapFocus();
                }
            }
        }
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        LatLng prevCameraPos = new LatLng(savedInstanceState.getDouble(LAT_TAG),
                savedInstanceState.getDouble(LNG_TAG));
        prevLatLng = prevCameraPos;

        if (tabletLayout) lastFrag = savedInstanceState.getInt(LAST_FRAG_TAG);


        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        double lat = mMap.getCameraPosition().target.latitude;
        double lng = mMap.getCameraPosition().target.longitude;

        outState.putDouble(LAT_TAG, lat);
        outState.putDouble(LNG_TAG, lng);

        if (tabletLayout) outState.putInt(LAST_FRAG_TAG, lastFrag);

        super.onSaveInstanceState(outState);
    }

    /**
     * Callback from MainActivityFragment to start other Activities
     * @param mUri uri with a restaurant's id. Passed to DetailActivity so that DetailActivity
     *             can query the database with the url
     */
    @Override
    public void onItemSelected(Uri mUri, Business searchResult) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUSI_TAG, searchResult);
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(mUri)
                .putExtra(DetailActivity.DETAIL_BUNDLE_TAG, bundle);
        startActivity(intent);
    }

    @Override
    public void showMarkerOnMap(LatLng latLng) {
        prevMarkerLatLng = latLng;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!tabletLayout)
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

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
                if (i == MAIN) {
                    mapContainer.setVisibility(View.VISIBLE);
                    fragmentContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.MATCH_PARENT, 3
                    ));
                }
                else {
                    mapContainer.setVisibility(View.GONE);
                    fragmentContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.MATCH_PARENT, 10
                    ));
                }
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @OnClick(R.id.get_location_button)
    void getResult() {
        if (!mGoogleApiClient.isConnected()) {
            DialogBuilder
                    .buildAlert(mContext, getString(R.string.no_internet_connection))
                    .show();
            return;
        }
        if (Utility.isConnectedToInternet(mContext)) {
            curLatitude = mMap.getCameraPosition().target.latitude;
            curLongitude = mMap.getCameraPosition().target.longitude;

            mFragment.setLatLng(curLatitude, curLongitude);
            mFragment.onLocationChaged(curLatitude, curLongitude);
        } else {
            DialogBuilder
                    .buildAlert(mContext, getString(R.string.no_internet_connection))
                    .show();
        }
    }
}
