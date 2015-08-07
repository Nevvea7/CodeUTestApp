package app.nevvea.nomnom;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import app.nevvea.nomnom.data.DataContract;
import app.nevvea.nomnom.data.SearchResult;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnTaskFinishedListener{

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;


    TextView yelpResultTextView;
    Button getResultButton;
    Button addToBlacklistButton;

    double curLongitude;
    double curLatitude;
    LatLng mapCameraLatLng;
    SearchResult mSearchResult;

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

        yelpResultTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSearchResult != null) {
                    ((Callback) getActivity())
                            .onItemSelected(DataContract.DetailEntry.buildDetailWithId(
                                    mSearchResult.getRestID()
                            ));
                }
            }
        });

        getResultButton = (Button) rootView.findViewById(R.id.get_location_button);
        getResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGoogleApiClient.isConnected()) {

                    mapCameraLatLng = mMap.getCameraPosition().target;

                    curLatitude = mapCameraLatLng.latitude;
                    curLongitude = mapCameraLatLng.longitude;

                    onLocationChaged(curLatitude, curLongitude);

                } else {
                    //TODO say that internet is not connected
                }
            }
        });

        addToBlacklistButton = (Button) rootView.findViewById(R.id.add_to_blacklist_button);
        addToBlacklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add this restaurant to blacklist
                ContentValues historyValues = new ContentValues();

                historyValues.put(DataContract.HistoryEntry.COLUMN_RESTAURANT_ID, mSearchResult.getRestID());
                historyValues.put(DataContract.HistoryEntry.COLUMN_RESTAURANT_NAME, mSearchResult.getRestName());

                Uri uri = getActivity().getContentResolver().insert(DataContract.HistoryEntry.CONTENT_URI,
                        historyValues);
                Log.d("history check", uri.toString());

                // call random function again since the user doesn't want this restaurant
                onLocationChaged(curLatitude, curLongitude);
            }
        });

        return rootView;
    }


    public void onLocationChaged(double lat, double longt) {
        // clear all existing markers
        mMap.clear();
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
    public void onTaskFinished(SearchResult result) {
        mSearchResult = result;
        if (result == null) {
            yelpResultTextView.setText("There's no restaurant around this point. Try somewhere else!");
        }
        else {
            yelpResultTextView.setText(result.getRestName());
            if (result.getLatLng() != null) {
                mMap.addMarker(new MarkerOptions()
                    .position(result.getLatLng()));
            }
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri mUri);
    }
}
