package app.nevvea.nomnom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Coordinate;
import com.yelp.clientlib.entities.Location;
import com.yelp.clientlib.entities.SearchResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import app.nevvea.nomnom.data.DataContract;
import app.nevvea.nomnom.data.DataContract.DetailEntry;
import app.nevvea.nomnom.data.DbHelper;

/**
 * This class provides utility methods including processing and formatting JSON
 * Created by Anna on 7/23/15.
 */
public class Utility {

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

        Vector<ContentValues> cVVector = new Vector<>(numOfRes);
        for (int i = 0; i < numOfRes; i++) {
            Business curBusiness = businesses.get(i);

            ContentValues detailValues = new ContentValues();

            detailValues.put(DetailEntry.COLUMN_RESTAURANT_ID, curBusiness.id());
            detailValues.put(DetailEntry.COLUMN_RESTAURANT_NAME, curBusiness.name());
            detailValues.put(DetailEntry.COLUMN_PHONE, curBusiness.phone());
            detailValues.put(DetailEntry.COLUMN_MOBILE_URL, curBusiness.mobileUrl());
            detailValues.put(DetailEntry.COLUMN_IMAGE_URL, curBusiness.imageUrl());
            detailValues.put(DetailEntry.COLUMN_ADDRESS, getAddressFromLoc(curBusiness.location()));
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

    public static LatLng getLatLng(Business business) {
        if (business.location() == null || business.location().coordinate() == null) return null;
        Coordinate coordinate = business.location().coordinate();
        if (coordinate.latitude() == null || coordinate.longitude() == null) return null;
        return new LatLng(coordinate.latitude(), coordinate.longitude());
    }

    /**
     * Get the restaurant's address from the JSONArray, and format it
     * @param location A JSONArray that contains restaurant's address
     * @return A formatted address String
     */
    private static String getAddressFromLoc(Location location) {
        ArrayList<String> addr = location.displayAddress();
        StringBuilder locationSb = new StringBuilder();

        locationSb.append(addr.get(0));
        for (int j = 1; j < addr.size(); j++) {
            locationSb.append(", ");
            locationSb.append(addr.get(j));
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
