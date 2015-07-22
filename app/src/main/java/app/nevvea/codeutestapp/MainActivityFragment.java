package app.nevvea.codeutestapp;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;

    TextView longitudeTextView;
    TextView latitudeTextView;
    TextView yelpResultTextView;
    Button getResultButton;

    double curLongitude;
    double curLatitude;
    Location mLocation;

    private final String LONG_LABEL = "Longitude: ";
    private final String LAT_LABEL = "Latitude: ";


    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        longitudeTextView = (TextView) rootView.findViewById(R.id.cur_location_longitude);
        latitudeTextView = (TextView) rootView.findViewById(R.id.cur_location_latitude);
        yelpResultTextView = (TextView) rootView.findViewById(R.id.cur_location_result);

        getResultButton = (Button) rootView.findViewById(R.id.get_location_button);
        getResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGoogleApiClient.isConnected()) {

                    mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    curLongitude = mLocation.getLongitude();
                    curLatitude = mLocation.getLatitude();

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLatitude, curLongitude), 16));

                    longitudeTextView.setText(LONG_LABEL + Double.toString(curLongitude));
                    latitudeTextView.setText(LAT_LABEL + Double.toString(curLatitude));

                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {
                            Yelp yelp = Yelp.getYelp(getActivity());
                            String businesses = yelp.search("restaurants", curLatitude, curLongitude);
                            try {
                                return processJson(businesses);
                            } catch (JSONException e) {
                                return businesses;
                            }
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            yelpResultTextView.setText(result);
                        }
                    }.execute();
                }
            }
        });

        return rootView;
    }


    String processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");
        ArrayList<String> businessNames = new ArrayList<String>(businesses.length());
        for (int i = 0; i < businesses.length(); i++) {
            JSONObject business = businesses.getJSONObject(i);
            businessNames.add(business.getString("name"));
        }
        return TextUtils.join("\n", businessNames);
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


    public void setmGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public void setmMap(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
