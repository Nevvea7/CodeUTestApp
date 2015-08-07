package app.nevvea.nomnom;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.util.HashMap;

import app.nevvea.nomnom.data.SearchResult;

/**
 * AsyncTask that calls Yelp Api and gets result back
 * Also calls Utility.processJson
 * Created by Anna on 7/31/15.
 */
public class FetchRestaurantsTask extends AsyncTask<Double, Void, SearchResult>{
    private Context mContext;
    MainActivityFragment mFragment;

    public FetchRestaurantsTask(Context context, MainActivityFragment fragment) {
        mContext = context;
        mFragment = fragment;
    }

    /**
     * Calls onPostExecute
     * @param params Lat and Lng
     * @return A SearchResult object that contains a restaurant's name, id and latlng
     */
    @Override
    protected SearchResult doInBackground(Double... params) {
        // call yelp api
        Yelp yelp = Yelp.getYelp(mContext);
        String businesses = yelp.search("restaurants", params[0], params[1]);
        try {
            return Utility.processJson(businesses, mContext);
        } catch (JSONException e) {
            Log.e("json error", e.toString());
            return null;
        }
    }

    /**
     * Called by doInBackground
     * When the result gets back from Yelp we pass it back to MainActivityFragment
     * @param result A SearchResult object that contains a restaurant's name, id and latlng
     */
    @Override
    protected void onPostExecute(SearchResult result) {
        mFragment.onTaskFinished(result);
    }
}
