package app.nevvea.nomnom.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Anna on 7/23/15.
 */
public class DataContract {

    public static final String CONTENT_AUTHORITY = "app.nevvea.nomnom";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_HISTORY = "history";
    public static final String PATH_DETAIL = "detail";

    public static final class HistoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;

        // Table name
        public static final String TABLE_NAME = "history";

        public static final String COLUMN_RESTAURANT_ID = "rest_id";
        public static final String COLUMN_RESTAURANT_NAME = "rest_name";

        public static Uri buildHistoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildHistoryWithRestID(String restid) {
            return CONTENT_URI.buildUpon().appendPath(restid).build();
        }



    }

    public static final class DetailEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DETAIL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DETAIL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DETAIL;

        // Table name
        public static final String TABLE_NAME = "detail";

        public static final String COLUMN_RESTAURANT_ID = "rest_id";
        public static final String COLUMN_RESTAURANT_NAME = "rest_name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_MOBILE_URL = "mobile_url";
        public static final String COLUMN_IMAGE_URL = "snippet_image_url";
        public static final String COLUMN_ADDRESS = "address";

        public static Uri buildDetailUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildDetailWithId(String ID) {
            return CONTENT_URI.buildUpon().appendPath(ID).build();
        }

    }

    public static String getIDFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }
}
