package app.nevvea.nomnom;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.yelp.clientlib.entities.Business;

import app.nevvea.nomnom.data.DataContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnTaskFinishedListener{

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;

    private static final String HAS_REST_TAG = "HAS_REST_TAG";
    private static final String SEARCHED_TAG = "SEARCHED_TAG";
    private static final String REST_NAME_TAG = "REST_NAME_TAG";
    private static final String REST_ID_TAG = "REST_ID_TAG";
    private static final String REST_LAT = "REST_LAT";
    private static final String REST_LNG = "REST_LNG";
    private static final String BUSI_TAG = "BUSI_TAG";

    TextView yelpResultTextView;
    TextView instructionTextView;
    BootstrapButton addToBlacklistButton;
    BootstrapButton showDetailButton;
    LinearLayout linearContainer;

    double curLongitude;
    double curLatitude;

    Business mBusiness;

    Boolean hasRestaurant = false;
    Boolean searched = false;

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
                if (mBusiness != null) {
                    ((Callback) getActivity())
                            .onItemSelected(DataContract.DetailEntry.buildDetailWithId(
                                    mBusiness.id()
                            ), mBusiness);
                }
            }
        });

        addToBlacklistButton = (BootstrapButton) rootView.findViewById(R.id.add_to_blacklist_button);
        addToBlacklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if the search result returned null
                if (mBusiness != null) {
                    // add this restaurant to blacklist
                    ContentValues historyValues = new ContentValues();

                    historyValues.put(DataContract.HistoryEntry.COLUMN_RESTAURANT_ID, mBusiness.id());
                    historyValues.put(DataContract.HistoryEntry.COLUMN_RESTAURANT_NAME, mBusiness.id());

                    Uri uri = getActivity().getContentResolver().insert(DataContract.HistoryEntry.CONTENT_URI,
                            historyValues);

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

        searched = true;
        if (!hasRestaurant) {
            instructionTextView.setVisibility(View.GONE);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(HAS_REST_TAG, hasRestaurant);
        outState.putBoolean(SEARCHED_TAG, searched);
        if (hasRestaurant) {
            outState.putString(REST_ID_TAG, mBusiness.id());
            outState.putString(REST_NAME_TAG, mBusiness.name());
            outState.putDouble(REST_LAT, mBusiness.location().coordinate().latitude());
            outState.putDouble(REST_LNG, mBusiness.location().coordinate().longitude());
            outState.putSerializable(BUSI_TAG, mBusiness);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            super.onViewStateRestored(savedInstanceState);
            return;
        }
        hasRestaurant = savedInstanceState.getBoolean(HAS_REST_TAG);
        searched = savedInstanceState.getBoolean(SEARCHED_TAG);
        if (searched && !hasRestaurant) {
            instructionTextView.setVisibility(View.GONE);
            linearContainer.setVisibility(View.VISIBLE);
            addToBlacklistButton.setVisibility(View.GONE);
            yelpResultTextView.setText(getActivity().getResources().getString(R.string.no_restaurant));
            showDetailButton.setVisibility(View.GONE);
        }
        if (hasRestaurant) {

            mBusiness = (Business) savedInstanceState.getSerializable(BUSI_TAG);

            instructionTextView.setVisibility(View.GONE);
            linearContainer.setVisibility(View.VISIBLE);
            addToBlacklistButton.setVisibility(View.VISIBLE);
            yelpResultTextView.setText(mBusiness.name());
            showDetailButton.setVisibility(View.VISIBLE);
            if (mBusiness.location() != null && mBusiness.location().coordinate() != null) {
                ((Callback) getActivity()).showMarkerOnMap(new LatLng(
                        mBusiness.location().coordinate().latitude(),
                        mBusiness.location().coordinate().longitude()
                ));
            }
        }

        super.onViewStateRestored(savedInstanceState);
    }

    // called by FetchRestaurantsTask in PostExecute
    @Override
    public void onTaskFinished(Business result) {
        mBusiness = result;
        // if can't find any restaurants around then tell the user to try somewhere else
        if (result == null) {
            hasRestaurant = false;
            linearContainer.setVisibility(View.VISIBLE);
            yelpResultTextView.setText(getActivity().getResources().getString(R.string.no_restaurant));
            showDetailButton.setVisibility(View.GONE);
            addToBlacklistButton.setVisibility(View.GONE);
        }
        // if there is then show it on the map
        else {
            hasRestaurant = true;
            yelpResultTextView.setText(result.name());
            linearContainer.setVisibility(View.VISIBLE);
            showDetailButton.setVisibility(View.VISIBLE);
            addToBlacklistButton.setVisibility(View.VISIBLE);
            LatLng latLng = new LatLng(result.location().coordinate().latitude(),
                    result.location().coordinate().longitude());
            if (result.location() != null) {
                mMap.addMarker(new MarkerOptions()
                    .position(latLng));

                LatLngBounds.Builder llBuilder = LatLngBounds.builder();
                llBuilder.include(mMap.getCameraPosition().target);
                llBuilder.include(latLng);
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
        void onItemSelected(Uri mUri, Business searchResult);

        void showMarkerOnMap(LatLng latLng);

    }

}
