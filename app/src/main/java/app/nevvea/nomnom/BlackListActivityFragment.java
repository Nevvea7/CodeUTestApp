package app.nevvea.nomnom;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import app.nevvea.nomnom.data.DataContract;

public class BlackListActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView myListView;
    TextView myTextView;
    private static final String[] HISTORY_COLUMNS = {
            DataContract.HistoryEntry._ID,
            DataContract.HistoryEntry.COLUMN_RESTAURANT_ID,
            DataContract.HistoryEntry.COLUMN_RESTAURANT_NAME
    };

    private BlackListAdapter mBlackListAdapter;
    private int HISTORY_LOADER = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blacklist, container, false);
        myListView = (ListView) rootView.findViewById(R.id.blacklist_list);
        myTextView = (TextView) rootView.findViewById(R.id.blacklist_empty);

        getLoaderManager().initLoader(HISTORY_LOADER, null, this);

        mBlackListAdapter = new BlackListAdapter(getActivity(), null);
        myListView.setAdapter(mBlackListAdapter);
        myListView.setEmptyView(myTextView);

        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                Log.d("click check", Integer.toString(position));
                if (c != null) {
                    Log.d("click check", "cursor not null");
                    final String restID = c.getString(BlackListActivity.COL_REST_ID);
                    //pop up alert
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder
                            .setMessage("Do you want to remove this place from the black list?")
                            .setPositiveButton("Yeah, I'll give it a chance.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getActivity().getContentResolver().delete(DataContract.HistoryEntry.CONTENT_URI
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

        return rootView;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = DataContract.HistoryEntry._ID + " ASC";
        Uri historyUri = DataContract.HistoryEntry.CONTENT_URI;

        Cursor test = getActivity().getContentResolver().query(
                DataContract.HistoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (test != null && test.moveToFirst()) {

            Log.d("cursor check", test.getString(0));
        }

        return new CursorLoader(getActivity(),
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