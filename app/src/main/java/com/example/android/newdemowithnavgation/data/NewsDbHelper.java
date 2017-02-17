package com.example.android.newdemowithnavgation.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import  com.example.android.newdemowithnavgation.data.NewsContract;

/**
 * Created by cheny on 2017/2/11.
 */

public class NewsDbHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "news.db";

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + NewsContract.NewsEntry.TABLE_NAME + " (" +
                NewsContract.NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NewsContract.NewsEntry.COLUMN_WEBTITLE + " TEXT  NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_WEBURL + " TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_THUMBNAIL + " TEXT, " +
                NewsContract.NewsEntry.COLUMN_TAGS + " TEXT NOT NULL " + " );";
        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsContract.NewsEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
