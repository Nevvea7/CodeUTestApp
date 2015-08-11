package app.nevvea.nomnom;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.squareup.picasso.Picasso;

import app.nevvea.nomnom.data.DataContract.DetailEntry;

/**
 * Query the database and dispay details of our restaurants.
 */
public class DetailActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{
    static final String DETAIL_URI = "DETAIL_URI";

    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            DetailEntry.COLUMN_RESTAURANT_ID,
            DetailEntry.COLUMN_RESTAURANT_NAME,
            DetailEntry.COLUMN_ADDRESS,
            DetailEntry.COLUMN_IMAGE_URL,
            DetailEntry.COLUMN_MOBILE_URL,
            DetailEntry.COLUMN_PHONE
    };

    static final int COL_REST_ID = 0;
    static final int COL_REST_NAME = 1;
    static final int COL_ADDR = 2;
    static final int COL_IMG_URL = 3;
    static final int COL_MOBILE_URL = 4;
    static final int COL_PHONE = 5;

    TextView restNameTextView;
    TextView restPhoneTextView;
    TextView restUrlTextView;
    TextView restAddrTextView;
    ImageView resImgView;
    BootstrapButton callButton;
    BootstrapButton mapButton;
    BootstrapButton yelpButton;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
            Log.d("detail check 1", mUri.toString());
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        restNameTextView = (TextView) rootView.findViewById(R.id.detail_rest_name);
        resImgView = (ImageView) rootView.findViewById(R.id.detail_rest_img);
        callButton = (BootstrapButton) rootView.findViewById(R.id.detail_call_rest);
        mapButton = (BootstrapButton) rootView.findViewById(R.id.detail_show_map);
        yelpButton = (BootstrapButton) rootView.findViewById(R.id.detail_show_yelp);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("detail check 3", "on create loader called");
        if ( null != mUri ) {
            Log.d("detail check 5", mUri.toString());
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            String restName = data.getString(COL_REST_NAME);
            restNameTextView.setText(restName);

            final String restPhone = data.getString(COL_PHONE);
            //restPhoneTextView.setText(restPhone);
            if (!restPhone.equals("")) {
                callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + restPhone));
                        startActivity(callIntent);
                    }
                });
            } else callButton.setBootstrapButtonEnabled(false);

            String restImg = data.getString(COL_IMG_URL);
            if (!restImg.equals("")){
                Picasso.with(getActivity())
                        .load(restImg)
                        .fit()
                        .centerCrop()
                        .into(resImgView);
            }

            final String restUrl = data.getString(COL_MOBILE_URL);
            //restUrlTextView.setText(restUrl);
            yelpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri webpage = Uri.parse(restUrl);
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    startActivity(webIntent);
                }
            });

            final String restAddr = data.getString(COL_ADDR);
            //restAddrTextView.setText(restAddr);
            if (!restAddr.equals("")) {
                mapButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri geoLocation = Uri.parse("geo:0,0?q=" + Utility.getAddressQuery(restAddr));
                        intent.setData(geoLocation);
                        startActivity(intent);
                    }
                });
            } else mapButton.setBootstrapButtonEnabled(false);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
