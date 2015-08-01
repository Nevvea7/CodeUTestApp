package app.nevvea.nomnom;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;

import app.nevvea.nomnom.data.DataContract.DetailEntry;

/**
 * Created by Anna on 7/23/15.
 */
public class Utility {
    static HashMap<String, String> map = new HashMap<>();

    public static HashMap<String, String> processJson(String jsonStuff, Context mContext) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");

        Vector<ContentValues> cVVector = new Vector<ContentValues>(businesses.length());

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
            phone = business.getString("phone");
            mobileURL = getUrlFromJson(business.getString("mobile_url"));
            imageURL = getUrlFromJson(business.getString("image_url"));
            JSONArray location = business.getJSONObject("location").getJSONArray("display_address");

            map.put(restName, restID);

            StringBuilder locationSb = new StringBuilder();
            locationSb.append(location.get(0));
            for (int j = 1; j < location.length(); j++) {
                locationSb.append(", ");
                locationSb.append(location.get(j));
            }

            ContentValues detailValues = new ContentValues();

            detailValues.put(DetailEntry.COLUMN_RESTAURANT_ID, restID);
            detailValues.put(DetailEntry.COLUMN_RESTAURANT_NAME, restName);
            detailValues.put(DetailEntry.COLUMN_PHONE, phone);
            detailValues.put(DetailEntry.COLUMN_MOBILE_URL, mobileURL);
            detailValues.put(DetailEntry.COLUMN_IMAGE_URL, imageURL);
            detailValues.put(DetailEntry.COLUMN_ADDRESS, locationSb.toString());

            cVVector.add(detailValues);
        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(DetailEntry.CONTENT_URI, cvArray);
        }

        Log.d("Utility check", "FetchWeatherTask Complete. " + inserted + " Inserted");

        return map;
    }

    private static String getUrlFromJson(String jsonUrl) {
        return jsonUrl.replace("\\", "");
    }
}
