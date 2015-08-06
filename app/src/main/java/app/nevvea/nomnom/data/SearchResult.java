package app.nevvea.nomnom.data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Wrapper object to return search result from Utility.processJson to
 * Created by Anna on 8/3/15.
 */
public class SearchResult {
    private String restName;
    private String restID;
    private LatLng latLng;

    public SearchResult(String name, LatLng latLng, String restID) {
        this.restName = name;
        this.latLng = latLng;
        this.restID = restID;
    }

    public String getRestID() {
        return restID;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getRestName() {
        return restName;
    }
}
