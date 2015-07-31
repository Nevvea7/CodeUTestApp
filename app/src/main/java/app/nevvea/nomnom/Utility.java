package app.nevvea.nomnom;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Anna on 7/23/15.
 */
public class Utility {
    static HashMap<String, String> map = new HashMap<>();

    public static HashMap<String, String> processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");
        ArrayList<String> businessNames = new ArrayList<>();

        Random rand = new Random();
        //int index = rand.nextInt(businesses.length());
        //JSONObject business = businesses.getJSONObject(index);
//
//        Log.d("json check", business.toString());
//
//        businessNames.add(business.getString("name"));
//
//        //businessNames.add(business.getString("url"));
//
//        JSONArray location = business.getJSONObject("location").getJSONArray("display_address");
//
//        StringBuilder locationSb = new StringBuilder();
//        locationSb.append(location.get(0));
//        for (int i = 1; i < location.length(); i++) {
//            locationSb.append(", ");
//            locationSb.append(location.get(i));
//        }
//
//        businessNames.add(locationSb.toString());

        for (int i = 0; i < businesses.length(); i++) {
            JSONObject bus = businesses.getJSONObject(i);
            map.put(bus.getString("name"), bus.getString("id"));
        }
        return map;
    }
}
