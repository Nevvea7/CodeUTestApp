package app.nevvea.nomnom.data;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.nevvea.nomnom.R;
import app.nevvea.nomnom.data.DataContract.DetailEntry;

/**
 * Query the database and dispay details of our restaurants.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
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

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
