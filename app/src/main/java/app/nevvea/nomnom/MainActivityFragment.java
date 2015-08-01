package app.nevvea.nomnom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnTaskFinishedListener{

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;

    TextView yelpResultTextView;
    Button getResultButton;

    double curLongitude;
    double curLatitude;
    LatLng mapCameraLatLng;

    // to store names of the last returned results
    HashMap<String, String> restaurants = new HashMap<>();

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

        yelpResultTextView = (TextView) rootView.findViewById(R.id.cur_location_result);

        getResultButton = (Button) rootView.findViewById(R.id.get_location_button);
        getResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGoogleApiClient.isConnected()) {

                    mapCameraLatLng = mMap.getCameraPosition().target;

                    curLatitude = mapCameraLatLng.latitude;
                    curLongitude = mapCameraLatLng.longitude;

                    onLocationChaged();
                }
            }
        });

        return rootView;
    }


    public void onLocationChaged() {
        FetchRestaurantsTask fetchRestaurantsTask = new FetchRestaurantsTask(getActivity(), this);
        fetchRestaurantsTask.execute(curLatitude, curLongitude);
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

    // called by FetchRestaurantsTask in PostExecute
    @Override
    public void onTaskFinished(HashMap<String, String> result) {
        restaurants = result;
        for (String key : restaurants.keySet()) {
            Log.d("map check", key);
        }

    }
}
