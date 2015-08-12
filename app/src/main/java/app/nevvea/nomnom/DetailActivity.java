package app.nevvea.nomnom;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import app.nevvea.nomnom.R;

public class DetailActivity extends ActionBarActivity
            implements OnMapReadyCallback{

    static final String REST_NAME_TAG = "REST_NAME_TAG";
    static final String REST_LAT_TAG = "REST_LAT_TAG";
    static final String REST_LNG_TAG = "REST_LNG_TAG";
    static final String DETAIL_BUNDLE_TAG = "DETAIL_BUNDLE_TAG";

    private GoogleMap mMap;
    private LatLng curLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailActivityFragment.DETAIL_URI, getIntent().getData());

            Bundle extras = getIntent().getBundleExtra(DETAIL_BUNDLE_TAG);
            if (extras != null) {
                getSupportActionBar().setTitle(extras.getString(REST_NAME_TAG));
                curLatLng = new LatLng(extras.getDouble(REST_LAT_TAG), extras.getDouble(REST_LNG_TAG));
            }

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);

            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.restaurant_detail_container, fragment)
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (curLatLng != null) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 15.5f));
        mMap.addMarker(new MarkerOptions().position(curLatLng));
        Log.d("map check", "map");
    }
}
