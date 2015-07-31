package app.nevvea.nomnom;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by Anna on 7/31/15.
 */
public class FetchRestaurantsTask extends AsyncTask<Double, Void, HashMap<String, String>>{
    private Context mContext;
    MainActivityFragment mFragment;

    public FetchRestaurantsTask(Context context, MainActivityFragment fragment) {
        mContext = context;
        mFragment = fragment;
    }
    @Override
    protected HashMap<String, String> doInBackground(Double... params) {
        Yelp yelp = Yelp.getYelp(mContext);
        String businesses = yelp.search("restaurants", params[0], params[1]);
        try {
            return Utility.processJson(businesses);
        } catch (JSONException e) {
            Log.e("json error", e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(HashMap<String, String> result) {
        mFragment.onTaskFinished(result);
    }
}
