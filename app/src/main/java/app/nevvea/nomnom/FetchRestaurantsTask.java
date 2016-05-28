package app.nevvea.nomnom;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import org.json.JSONException;

import java.util.HashMap;

import app.nevvea.nomnom.data.SearchResult;

/**
 * AsyncTask that calls Yelp Api and gets result back
 * Also calls Utility.processJson
 * Created by Anna on 7/31/15.
 */
public class FetchRestaurantsTask extends AsyncTask<Double, Void, Business>{
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
    protected Business doInBackground(Double... params) {
        // call yelp api
        Yelp yelp = Yelp.getYelp(mContext);
        SearchResponse response = yelp.search(mContext.getString(R.string.yelp_search_term), params[0], params[1]);

        return Utility.processSearchResponse(response, mContext);
    }

    /**
     * Called by doInBackground
     * When the result gets back from Yelp we pass it back to MainActivityFragment
     * @param result A SearchResult object that contains a restaurant's name, id and latlng
     */
    @Override
    protected void onPostExecute(Business result) {
        mFragment.onTaskFinished(result);
    }
}
