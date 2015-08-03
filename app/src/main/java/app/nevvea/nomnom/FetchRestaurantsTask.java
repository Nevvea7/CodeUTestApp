package app.nevvea.nomnom;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.util.HashMap;

import app.nevvea.nomnom.data.SearchResult;

/**
 * Created by Anna on 7/31/15.
 */
public class FetchRestaurantsTask extends AsyncTask<Double, Void, SearchResult>{
    private Context mContext;
    MainActivityFragment mFragment;

    public FetchRestaurantsTask(Context context, MainActivityFragment fragment) {
        mContext = context;
        mFragment = fragment;
    }

    @Override
    protected SearchResult doInBackground(Double... params) {
        Yelp yelp = Yelp.getYelp(mContext);
        String businesses = yelp.search("restaurants", params[0], params[1]);
        try {
            return Utility.processJson(businesses, mContext);
        } catch (JSONException e) {
            Log.e("json error", e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(SearchResult result) {
        mFragment.onTaskFinished(result);
    }
}
