package app.nevvea.nomnom;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import app.nevvea.nomnom.data.DataContract;

public class BlackListActivity extends ActionBarActivity {

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
}