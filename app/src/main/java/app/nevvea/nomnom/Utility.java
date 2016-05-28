package app.nevvea.nomnom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import app.nevvea.nomnom.data.DataContract;
import app.nevvea.nomnom.data.DataContract.DetailEntry;
import app.nevvea.nomnom.data.DbHelper;
import app.nevvea.nomnom.data.SearchResult;

/**
 * This class provides utility methods including processing and formatting JSON
 * Created by Anna on 7/23/15.
 */
public class Utility {

    /**
     * Called from FetchRestaurantsTask when yelp result returns
     * Return a random result to display in MainActivityFragment,
     * Also insert any places that aren't already in db into db.
     *
     * @param jsonStuff the json string we get from calling yelp api
     * @param mContext context of MainActivityFragment in order to get the content resolver
     * @return a SearchResult object that contains the restaurant's name, id and lat/lng
     * @throws JSONException the json might be null
     */
    public static SearchResult processJson(String jsonStuff, Context mContext) throws JSONException {

        // turn json string to a jsonObject
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");

        // prevent random.nextint error
        if (businesses.length() <= 0) {
            return null;
        }


        // get a random object as return result
        Random random = new Random();
        int index = random.nextInt(businesses.length());
        JSONObject returnRest = businesses.getJSONObject(index);
        String returnName, returnID;
        LatLng returnLatLng;
        returnID = returnRest.getString("id");
        // if the place is in the blacklist, then roll something else
        // TODO: prevent infinite loop
        while (isInBlacklist(returnID, mContext)) {
            index = random.nextInt(businesses.length());
            returnRest = businesses.getJSONObject(index);
            returnID = returnRest.getString("id");
        }

        returnName = returnRest.getString("name");
        returnLatLng = geLatLngFromJson(returnRest.getJSONObject("location").getJSONObject("coordinate"));

        SearchResult searchResult = new SearchResult(returnName, returnLatLng, returnID);

        // store the values to be inserted into db
        // TODO: optimize the performance here
        Vector<ContentValues> cVVector = new Vector<>(businesses.length());

        // bulk insert to database
        for (int i = 0; i < businesses.length(); i++) {
            String restID;
            String restName;
            String phone;
            String mobileURL;
            String imageURL;

            JSONObject business = businesses.getJSONObject(i);
            restID = business.getString("id");
            restName = business.getString("name");

            // these fields might be null
            try {
                phone = business.getString("phone");
            } catch (JSONException e) {
                phone = "";
            }
            try {
                imageURL = getUrlFromJson(business.getString("image_url"));
            } catch (JSONException e) {
                imageURL = "";
            }
            try {
                mobileURL = getUrlFromJson(business.getString("mobile_url"));
                Log.d("json check mobile", mobileURL);
            } catch (JSONException e) {
                mobileURL = "";
            }


            JSONArray location = business.getJSONObject("location").getJSONArray("display_address");

            ContentValues detailValues = new ContentValues();

            detailValues.put(DetailEntry.COLUMN_RESTAURANT_ID, restID);
            detailValues.put(DetailEntry.COLUMN_RESTAURANT_NAME, restName);
            detailValues.put(DetailEntry.COLUMN_PHONE, phone);
            detailValues.put(DetailEntry.COLUMN_MOBILE_URL, mobileURL);
            detailValues.put(DetailEntry.COLUMN_IMAGE_URL, imageURL);
            detailValues.put(DetailEntry.COLUMN_ADDRESS, getAddressFromJson(location));

            cVVector.add(detailValues);
        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(DetailEntry.CONTENT_URI, cvArray);
        }

        Log.d("Utility check", "FetchRestaurantTask Complete. " + inserted + " Inserted");

        return searchResult;
    }

    public static Business processSearchResponse(SearchResponse response, Context mContext) {
        if (response == null) return null;

        ArrayList<Business> businesses = response.businesses();
        int numOfRes = businesses.size();
        if (numOfRes == 0) return null;

        Random random = new Random();
        int index = random.nextInt(numOfRes);
        while (isInBlacklist(businesses.get(index).id(), mContext)) {
            index = random.nextInt(numOfRes);
        }
        return businesses.get(index);
    }

    /**
     * Check if a restaurant is in black list
     * @param returnID id of the restaurant that we want to check
     * @param context context of the MainActivityFragment to get access to db
     * @return true if in blacklist, false otherwise
     * TODO: merge this with the method in DataProvider
     */
    private static boolean isInBlacklist(String returnID, Context context) {
        final SQLiteDatabase checkDb = new DbHelper(context).getWritableDatabase();
        final String query = "select * from " + DataContract.HistoryEntry.TABLE_NAME + " where rest_id = ?";

        Cursor c = checkDb.rawQuery(query, new String[]{returnID});
        // TODO: check what's wrong here
        if (c == null) return false;
        c.moveToFirst();
        if (c.getCount() == 0) {
            c.close();
            return false;
        }else {
            c.close();
            return true;
        }
    }


    /**
     * Get LatLng from a JSONObject
     * @param coordinates
     * @return
     */
    private static LatLng geLatLngFromJson(JSONObject coordinates) {
        try {
            double lat = coordinates.getDouble("latitude");
            double lng = coordinates.getDouble("longitude");
            return new LatLng(lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Format the url returned from yelp since it has escape characters
     * @param jsonUrl
     * @return
     */
    private static String getUrlFromJson(String jsonUrl) {
        return jsonUrl.replace("\\", "");
    }

    /**
     * Get the restaurant's address from the JSONArray, and format it
     * @param location A JSONArray that contains restaurant's address
     * @return A formatted address String
     */
    private static String getAddressFromJson(JSONArray location) {
        StringBuilder locationSb = new StringBuilder();
        try {
            locationSb.append(location.get(0));
            for (int j = 1; j < location.length(); j++) {
                locationSb.append(", ");
                locationSb.append(location.get(j));
            }
        } catch (JSONException e) {
            // TODO: do something with the error
            e.printStackTrace();
        }
        return locationSb.toString();
    }

    static String getAddressQuery(String address) {
        try {
            return URLEncoder.encode(address, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }


}
