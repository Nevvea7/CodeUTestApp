package app.nevvea.nomnom;


import com.google.android.gms.maps.model.LatLng;

import app.nevvea.nomnom.data.SearchResult;

/**
 * Created by Anna on 7/31/15.
 */
public interface OnTaskFinishedListener {
    void onTaskFinished(SearchResult result);
}
