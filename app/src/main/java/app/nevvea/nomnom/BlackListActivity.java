package app.nevvea.nomnom;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import app.nevvea.nomnom.data.DataContract;

public class BlackListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private BlackListAdapter mBlackListAdapter;
    private ListView mListView;

    private int HISTORY_LOADER = 0;

    private static final String[] HISTORY_COLUMNS = {
            DataContract.HistoryEntry._ID,
            DataContract.HistoryEntry.COLUMN_RESTAURANT_ID,
            DataContract.HistoryEntry.COLUMN_RESTAURANT_NAME
    };

    static final int COL_REST_ID = 0;
    static final int COL_REST_NAME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);

        getLoaderManager().initLoader(HISTORY_LOADER, null, this);

        mBlackListAdapter = new BlackListAdapter(this, null);
        mListView = (ListView) findViewById(R.id.blacklist_listView);
        mListView.setAdapter(mBlackListAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_black_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = DataContract.HistoryEntry._ID + " ASC";
        Uri historyUri = DataContract.HistoryEntry.CONTENT_URI;

        Log.d("cursor check", "created");
        Cursor test = this.getContentResolver().query(
                DataContract.HistoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (test != null && test.moveToFirst()) {

            Log.d("cursor check", test.getString(0));
        }

        return new CursorLoader(this,
                historyUri,
                HISTORY_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mBlackListAdapter.swapCursor(cursor);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBlackListAdapter.swapCursor(null);
    }
}
