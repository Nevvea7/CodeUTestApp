package app.nevvea.nomnom.data;

import android.net.Uri;
import android.test.AndroidTestCase;
import app.nevvea.nomnom.data.DataContract.DetailEntry;


public class TestDataContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_ID = "bertuccis-jersey-city-2";

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildWeatherLocation() {
        Uri locationUri = DetailEntry.buildDetailWithId(TEST_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "WeatherContract.",
                locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                TEST_ID, locationUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                locationUri.toString(),
                "content://app.nevvea.nomnom/detail/bertuccis-jersey-city-2");
    }
}

