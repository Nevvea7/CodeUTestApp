package app.nevvea.nomnom;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import app.nevvea.nomnom.data.DataContract.DetailEntry;
import app.nevvea.nomnom.data.SearchResult;

/**
 * Created by Anna on 7/23/15.
 */
public class Utility {
    public static SearchResult processJson(String jsonStuff, Context mContext) throws JSONException {

        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");

        // prevent random.nextint error
        if (businesses.length() <= 0) {
            return null;
        }
        Vector<ContentValues> cVVector = new Vector<>(businesses.length());

        // get a random object as return result
        Random random = new Random();
        int index = random.nextInt(businesses.length());
        JSONObject returnRest = businesses.getJSONObject(index);
        String returnName, returnID;
        LatLng returnLatLng;

        returnName = returnRest.getString("name");
        returnLatLng = geLatLngFromJson(returnRest.getJSONObject("location").getJSONObject("coordinate"));
        returnID = returnRest.getString("id");

        SearchResult searchResult = new SearchResult(returnName, returnLatLng, returnID);

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

    private static String getUrlFromJson(String jsonUrl) {
        return jsonUrl.replace("\\", "");
    }

    /**
     * @param location
     * @return
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


}
