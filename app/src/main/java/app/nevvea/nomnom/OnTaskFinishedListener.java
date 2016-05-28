package app.nevvea.nomnom;


import com.yelp.clientlib.entities.Business;

/**
 * Created by Anna on 7/31/15.
 */
public interface OnTaskFinishedListener {
    void onTaskFinished(Business result);
}
