package com.example.android.newdemowithnavgation.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.R.attr.name;

/**
 * Created by cheny on 2017/2/11.
 */

public class NewsProvider extends ContentProvider {

    public static final String LOG_TAG = NewsProvider.class.getSimpleName();

    // The URI Matcher used by this content provider.
   // private static final UriMatcher sUriMatcher = buildUriMatcher();


    /** URI matcher code for the content URI for the products table */
    private static final int NEWS = 100;

    /** URI matcher code for the content URI for a single product in the pets table */
    private static final int NEWS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {

        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.PATH_NEWS, NEWS );


        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.PATH_NEWS + "/#", NEWS_ID);
    }

    /** Database helper object */
    private NewsDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new NewsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                cursor = database.query(NewsContract.NewsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case NEWS_ID:
                selection = NewsContract.NewsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(NewsContract.NewsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                return NewsContract.NewsEntry.CONTENT_LIST_TYPE;
         case NEWS_ID:
              return NewsContract.NewsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case NEWS:
                long id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    return ContentUris.withAppendedId(uri, id);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case NEWS:
                rowsDeleted = db.delete(
                        NewsContract.NewsEntry.TABLE_NAME, selection, selectionArgs);
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
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case NEWS:
                rowsUpdated = db.update(NewsContract.NewsEntry.TABLE_NAME, values, selection,
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
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, value);
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
