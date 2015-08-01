package app.nevvea.nomnom.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Anna on 7/31/15.
 */
public class TestUtilities extends AndroidTestCase {
    static ContentValues createDetailValues() {
        ContentValues detailValues = new ContentValues();
        detailValues.put(DataContract.DetailEntry.COLUMN_RESTAURANT_ID, "bertuccis-jersey-city-2");
        detailValues.put(DataContract.DetailEntry.COLUMN_IMAGE_URL, "http://s3-media3.fl.yelpcdn.com/bphoto/0UCz0CUwiRX0Q4BlZnWoHQ/ms.jpg");
        detailValues.put(DataContract.DetailEntry.COLUMN_MOBILE_URL, "http://m.yelp.com/biz/bertuccis-jersey-city-2");
        detailValues.put(DataContract.DetailEntry.COLUMN_PHONE, "2012228088");
        detailValues.put(DataContract.DetailEntry.COLUMN_RESTAURANT_NAME, "Bertucci's");
        return detailValues;

    }
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
