package app.nevvea.nomnom.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by Anna on 7/29/15.
 */
public class DataProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int DETAIL = 100;
    static final int DETAIL_WITH_ID = 101;
    static final int HISTORY = 200;
    static final int HISTORY_WITH_REST_ID = 201;

    private static final String sDetailIDSelection =
            DataContract.DetailEntry.TABLE_NAME + '.' +
                    DataContract.DetailEntry.COLUMN_RESTAURANT_ID + " = ? ";
    private static final String sHistoryIDSelection =
            DataContract.HistoryEntry.TABLE_NAME + '.' +
                    DataContract.HistoryEntry.COLUMN_RESTAURANT_ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DataContract.PATH_DETAIL, DETAIL);
        matcher.addURI(authority, DataContract.PATH_DETAIL + "/*", DETAIL_WITH_ID);
        matcher.addURI(authority, DataContract.PATH_HISTORY, HISTORY);
        matcher.addURI(authority, DataContract.PATH_HISTORY + "/*", HISTORY_WITH_REST_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // detail/*
            case DETAIL_WITH_ID: {
                retCursor = getDetailByID(uri, projection);
                break;
            }
            case DETAIL: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.DetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case HISTORY_WITH_REST_ID: {
                retCursor = getHistoryByID(uri, projection);
                break;
            }
            case HISTORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.HistoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getHistoryByID(Uri uri, String[] projection) {
        String restID = DataContract.getIDFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                DataContract.HistoryEntry.TABLE_NAME,
                projection,
                sHistoryIDSelection,
                new String[]{restID},
                null,
                null,
                null
        );
    }

    private Cursor getDetailByID(Uri uri, String[] projection) {
        String restID = DataContract.getIDFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                DataContract.DetailEntry.TABLE_NAME,
                projection,
                sDetailIDSelection,
                new String[]{restID},
                null,
                null,
                null
        );
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case DETAIL: {
                long _id = db.insert(DataContract.DetailEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DataContract.DetailEntry.buildDetailUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case HISTORY: {
                long _id = db.insert(DataContract.HistoryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DataContract.HistoryEntry.buildHistoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // we want only 1 detail so it's an item
            case DETAIL_WITH_ID:
                return DataContract.DetailEntry.CONTENT_ITEM_TYPE;
            case DETAIL:
                return DataContract.DetailEntry.CONTENT_ITEM_TYPE;
            // we want a list of history so it's dir
            case HISTORY:
                return DataContract.HistoryEntry.CONTENT_TYPE;
            // or we can check if a single restaurant is in the history list
            case HISTORY_WITH_REST_ID:
                return DataContract.HistoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case DETAIL:
                rowsUpdated = db.update(DataContract.DetailEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case HISTORY:
                rowsUpdated = db.update(DataContract.HistoryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case DETAIL:
                rowsDeleted = db.delete(
                        DataContract.DetailEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HISTORY:
                rowsDeleted = db.delete(
                        DataContract.HistoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DETAIL:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DataContract.DetailEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
