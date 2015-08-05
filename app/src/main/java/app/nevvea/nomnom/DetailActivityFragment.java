package app.nevvea.nomnom;

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
import android.widget.TextView;

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
    TextView restImgTextView;
    TextView restPhoneTextView;
    TextView restUrlTextView;
    TextView restAddrTextView;

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
        restImgTextView = (TextView) rootView.findViewById(R.id.detail_rest_img);
        restPhoneTextView = (TextView) rootView.findViewById(R.id.detail_rest_phone);
        restAddrTextView = (TextView) rootView.findViewById(R.id.detail_rest_addr);
        restUrlTextView = (TextView) rootView.findViewById(R.id.detail_rest_url);

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

            String restPhone = data.getString(COL_PHONE);
            restPhoneTextView.setText(restPhone);

            String restImg = data.getString(COL_IMG_URL);
            restImgTextView.setText(restImg);

            String restUrl = data.getString(COL_MOBILE_URL);
            restUrlTextView.setText(restUrl);

            String restAddr = data.getString(COL_ADDR);
            restUrlTextView.setText(restAddr);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
