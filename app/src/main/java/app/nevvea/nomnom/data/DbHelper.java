package app.nevvea.nomnom.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.nevvea.nomnom.data.DataContract.HistoryEntry;
import app.nevvea.nomnom.data.DataContract.DetailEntry;

/**
 * Created by Anna on 7/29/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "restaurants.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                HistoryEntry._ID + " INTEGER PRIMARY KEY, " +
                HistoryEntry.COLUMN_RESTAURANT_ID + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + HistoryEntry.COLUMN_RESTAURANT_ID + ") REFERENCES " +
                DetailEntry.TABLE_NAME + " (" + DetailEntry.COLUMN_RESTAURANT_ID + ")" +
                " );";

        final String SQL_CREATE_DETAIL_TABLE = "CREATE TABLE " + DetailEntry.TABLE_NAME + " (" +
                DetailEntry.COLUMN_RESTAURANT_ID + " TEXT PRIMARY KEY, " +
                DetailEntry.COLUMN_RESTAURANT_NAME + " TEXT NOT NULL, " +
                DetailEntry.COLUMN_PHONE + " TEXT NOT NULL, " +
                DetailEntry.COLUMN_MOBILE_URL + " TEXT NOT NULL, " +
                DetailEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                DetailEntry.COLUMN_SNIPPET_IMG_UTL + " TEXT NOT NULL, " +
                DetailEntry.COLUMN_HISTORY_KEY + " INTEGER NOT NULL " +
                " );";

        // since we wan the history table to reference detail table, we create the detail table first
        sqLiteDatabase.execSQL(SQL_CREATE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HistoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DetailEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
