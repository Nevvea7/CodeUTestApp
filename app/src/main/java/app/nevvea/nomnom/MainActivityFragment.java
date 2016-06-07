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
import app.nevvea.nomnom.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnTaskFinishedListener{

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;

    private static final String HAS_REST_TAG = "HAS_REST_TAG";
    private static final String SEARCHED_TAG = "SEARCHED_TAG";
    private static final String BUSI_TAG = "BUSI_TAG";

    @BindView(R.id.cur_location_result)
    TextView yelpResultTextView;
    @BindView(R.id.instruction_text)
    TextView instructionTextView;
    @BindView(R.id.add_to_blacklist_button)
    BootstrapButton addToBlacklistButton;
    @BindView(R.id.goto_detail_button)
    BootstrapButton showDetailButton;
    @BindView(R.id.mainfragment_linear)
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

        ButterKnife.bind(this, rootView);

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
            LatLng latLng = Utility.getLatLng(mBusiness);
            if (latLng != null) {
                ((Callback) getActivity()).showMarkerOnMap(latLng);
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
            LatLng latLng = Utility.getLatLng(mBusiness);

            if (latLng != null) {
                mMap.addMarker(new MarkerOptions()
                    .position(latLng));
                if (mMap.getProjection().getVisibleRegion().latLngBounds.contains(latLng))
                    return;
                LatLngBounds.Builder llBuilder = LatLngBounds.builder();
                llBuilder.include(mMap.getProjection().getVisibleRegion().latLngBounds.northeast);
                llBuilder.include(mMap.getProjection().getVisibleRegion().latLngBounds.southwest);

                llBuilder.include(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llBuilder.build(), 50));
            }
        }
    }

    @OnClick(R.id.goto_detail_button)
    void showDetail() {
        if (mBusiness == null) return;
        ((Callback) getActivity())
                .onItemSelected(DataContract.DetailEntry.buildDetailWithId(
                        mBusiness.id()
                ), mBusiness);
    }

    @OnClick(R.id.add_to_blacklist_button)
    void addToBlackList() {
        // check if the search result returned null
        if (mBusiness == null) return;
        // add this restaurant to blacklist
        ContentValues historyValues = new ContentValues();

        historyValues.put(DataContract.HistoryEntry.COLUMN_RESTAURANT_ID, mBusiness.id());
        historyValues.put(DataContract.HistoryEntry.COLUMN_RESTAURANT_NAME, mBusiness.id());

        Uri uri = getActivity().getContentResolver().insert(DataContract.HistoryEntry.CONTENT_URI,
                historyValues);

        // call random function again since the user doesn't want this restaurant
        onLocationChaged(curLatitude, curLongitude);
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
