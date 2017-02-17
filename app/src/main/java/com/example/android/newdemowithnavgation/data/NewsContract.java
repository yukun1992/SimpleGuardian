package com.example.android.newdemowithnavgation.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by cheny on 2017/2/11.
 */

public class NewsContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.newdemowithnavgation";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NEWS = "news";

    public static final class NewsEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWS );

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;

        // Table name
        public static final String TABLE_NAME = "news";

        public final static String _ID = BaseColumns._ID;

        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String COLUMN_WEBTITLE= "webTitle";


        public static final String COLUMN_WEBURL = "webUrl";


        public static final String COLUMN_THUMBNAIL = "thumbnail";
        //public static final String COLUMN_COORD_LONG = "coord_long";
        public static final String COLUMN_TAGS = "tags";


    }
}
