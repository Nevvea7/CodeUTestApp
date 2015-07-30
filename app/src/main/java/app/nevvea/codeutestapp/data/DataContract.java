package app.nevvea.codeutestapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Anna on 7/23/15.
 */
public class DataContract {

    public static final String CONTENT_AUTHORITY = "app.nevvea.codeutestapp.data";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_HISTORY = "history";
    public static final String PATH_DETAIL = "detail";

    public static final class HistoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();

    }

    public static final class DetailEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DETAIL).build();
    }
}
