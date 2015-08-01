package app.nevvea.nomnom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;


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
    List<String> keySet;

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

                    onLocationChaged(curLatitude, curLongitude);

//                    if (keySet != null && keySet.size() > 0) {
//                        Random random = new Random();
//                        int index = random.nextInt(keySet.size());
//                        yelpResultTextView.setText(keySet.get(index));
//                    } else {
//                        yelpResultTextView.setText("No fit restaurant around this point. Try somewhere else!");
//                    }


                } else {
                    //TODO say that internet is not connected
                }
            }
        });

        return rootView;
    }


    public void onLocationChaged(double lat, double longt) {
        FetchRestaurantsTask fetchRestaurantsTask = new FetchRestaurantsTask(getActivity(), this);
        fetchRestaurantsTask.execute(lat, longt);
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
    public void onTaskFinished(String result) {
        //restaurants = result;
//        if (restaurants != null) {
//            Set<String> tmp = restaurants.keySet();
//            keySet = new ArrayList<>();
//            for (String t: tmp) {
//                keySet.add(t);
//            }
//        }

        yelpResultTextView.setText(result);
    }
}
