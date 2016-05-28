package app.nevvea.nomnom;

import android.content.Context;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Customized Yelp API requests
 * Created by Anna on 7/16/15.
 */
public class Yelp {

    YelpAPI mYelpApi;

    public static Yelp getYelp(Context context) {
        YelpAPIFactory apiFactory = new YelpAPIFactory(context.getString(R.string.consumer_key),
                context.getString(R.string.consumer_secret),
                context.getString(R.string.token),
                context.getString(R.string.token_secret));
        YelpAPI yelpAPI = apiFactory.createAPI();
        return new Yelp(yelpAPI);
    }

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
     *
     * @param
     *
     */
    public Yelp(YelpAPI yelpAPI) {
        mYelpApi = yelpAPI;
    }

    /**
     * Search with term and location.
     *
     * @param term Search term
     * @param latitude Latitude
     * @param longitude Longitude
     * @return JSON string response
     */
    public SearchResponse search(String term, double latitude, double longitude) {
        Map<String, String> params = new HashMap<>();

        // general params
        params.put("category_filter", term);
        params.put("limit", "10");

        // coordinates
        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(latitude)
                .longitude(longitude).build();

        Call<SearchResponse> call = mYelpApi.search(coordinate, params);

        SearchResponse response = null;
        try {
            response = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

}
