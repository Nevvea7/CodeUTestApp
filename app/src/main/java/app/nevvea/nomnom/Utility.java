package app.nevvea.nomnom;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Anna on 7/23/15.
 */
public class Utility {

    public static String processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");
        ArrayList<String> businessNames = new ArrayList<>();

        Random rand = new Random();
        int index = rand.nextInt(businesses.length());
        JSONObject business = businesses.getJSONObject(index);

        businessNames.add(business.getString("name"));
        businessNames.add(business.getString("url"));

        JSONArray location = business.getJSONObject("location").getJSONArray("display_address");

        StringBuilder locationSb = new StringBuilder();
        locationSb.append(location.get(0));
        for (int i = 1; i < location.length(); i++) {
            locationSb.append(", ");
            locationSb.append(location.get(i));
        }

        businessNames.add(locationSb.toString());

        return TextUtils.join("\n", businessNames);
    }
}
