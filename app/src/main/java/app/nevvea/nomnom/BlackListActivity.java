package app.nevvea.nomnom;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import app.nevvea.nomnom.data.DataContract;

public class BlackListActivity extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;

    private BlackListAdapter mBlackListAdapter;

    private int HISTORY_LOADER = 0;

    private static final String[] HISTORY_COLUMNS = {
            DataContract.HistoryEntry._ID,
            DataContract.HistoryEntry.COLUMN_RESTAURANT_ID,
            DataContract.HistoryEntry.COLUMN_RESTAURANT_NAME
    };

    static final int COL_ID = 0;
    static final int COL_REST_ID = 1;
    static final int COL_REST_NAME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);

        getLoaderManager().initLoader(HISTORY_LOADER, null, this);

        mContext = this;

        mBlackListAdapter = new BlackListAdapter(this, null);
        setListAdapter(mBlackListAdapter);

        ListView mListView = getListView();
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                Log.d("click check", Integer.toString(position));
                if (c != null) {
                    Log.d("click check", "cursor not null");
                    final String restID = c.getString(BlackListActivity.COL_REST_ID);
                    //pop up alert
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder
                            .setMessage("Do you want to remove this place from the black list?")
                            .setPositiveButton("Yeah, I'll give it a chance.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mContext.getContentResolver().delete(DataContract.HistoryEntry.CONTENT_URI
                                            , "rest_id = ?", new String[]{restID});
                                }
                            });

                    //create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    //show it
                    alertDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = (Cursor) l.getItemAtPosition(position);
        if (c != null) {
            Log.d("click check", "cursor not null");
            final String restID = c.getString(BlackListActivity.COL_REST_ID);
            Uri mUri = DataContract.DetailEntry.buildDetailWithId(restID);
            // link to detail activity
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(mUri);
            startActivity(intent);
        }
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
