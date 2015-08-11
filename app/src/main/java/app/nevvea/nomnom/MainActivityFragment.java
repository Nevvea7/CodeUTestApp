package app.nevvea.nomnom;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import app.nevvea.nomnom.data.DataContract;
import app.nevvea.nomnom.data.SearchResult;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnTaskFinishedListener{

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;


    TextView yelpResultTextView;
    TextView instructionTextView;
    BootstrapButton addToBlacklistButton;
    BootstrapButton showDetailButton;
    LinearLayout linearContainer;

    double curLongitude;
    double curLatitude;
    SearchResult mSearchResult;

    Boolean hasRestaurant = false;

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

        instructionTextView = (TextView) rootView.findViewById(R.id.instruction_text);
        linearContainer = (LinearLayout) rootView.findViewById(R.id.mainfragment_linear);
        yelpResultTextView = (TextView) rootView.findViewById(R.id.cur_location_result);

        showDetailButton = (BootstrapButton) rootView.findViewById(R.id.goto_detail_button);
        showDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSearchResult != null) {
                    ((Callback) getActivity())
                            .onItemSelected(DataContract.DetailEntry.buildDetailWithId(
                                    mSearchResult.getRestID()
                            ), mSearchResult);
                }
            }
        });

        addToBlacklistButton = (BootstrapButton) rootView.findViewById(R.id.add_to_blacklist_button);
        addToBlacklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if the search result returned null
                if (mSearchResult != null) {
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
            }
        });

        return rootView;
    }


    public void onLocationChaged(double lat, double longt) {
        // clear all existing markers
        mMap.clear();
        FetchRestaurantsTask fetchRestaurantsTask = new FetchRestaurantsTask(getActivity(), this);
        fetchRestaurantsTask.execute(lat, longt);

        if (!hasRestaurant) {
            hasRestaurant = true;
            instructionTextView.setVisibility(View.GONE);
            linearContainer.setVisibility(View.VISIBLE);
            addToBlacklistButton.setVisibility(View.VISIBLE);
        }
    }

    public void setmGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public void setmMap(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void setLatLng(double lat, double lng){
        curLatitude = lat;
        curLongitude = lng;
    }

    // called by FetchRestaurantsTask in PostExecute
    @Override
    public void onTaskFinished(SearchResult result) {
        mSearchResult = result;
        if (result == null) {
            yelpResultTextView.setText("There's no restaurant around this point. Try somewhere else!");
            showDetailButton.setVisibility(View.GONE);
        }
        else {
            yelpResultTextView.setText(result.getRestName());
            showDetailButton.setVisibility(View.VISIBLE);
            if (result.getLatLng() != null) {
                mMap.addMarker(new MarkerOptions()
                    .position(result.getLatLng()));
                LatLngBounds.Builder llBuilder = LatLngBounds.builder();
                llBuilder.include(mMap.getCameraPosition().target);
                llBuilder.include(result.getLatLng());
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llBuilder.build(), 50));
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
        public void onItemSelected(Uri mUri, SearchResult searchResult);


    }
}
