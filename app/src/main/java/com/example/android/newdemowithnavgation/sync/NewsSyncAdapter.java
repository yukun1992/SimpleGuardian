package com.example.android.newdemowithnavgation.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.newdemowithnavgation.NewsFragment;
import com.example.android.newdemowithnavgation.R;
import com.example.android.newdemowithnavgation.data.NewsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by cheny on 2017/2/11.
 */

public class NewsSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = NewsSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[]{
            NewsContract.NewsEntry.COLUMN_WEBTITLE,
            NewsContract.NewsEntry.COLUMN_WEBURL,
            NewsContract.NewsEntry.COLUMN_THUMBNAIL
    };

    // these indices must match the projection
    private static final int INDEX_WEBTITLE = 0;
    private static final int INDEX_WEBURL = 1;
    private static final int INDEX_THUMBNAIL = 2;


    public static String mtab;


    public NewsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the  JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String News_BASE_URL="https://content.guardianapis.com/search?api-key=";
        String News_API_KEY ="" + "&q=";
        String News_BASE_URL2 ="&show-fields=thumbnail,headline";
        String News_FINAL_URL = News_BASE_URL + News_API_KEY + mtab.toLowerCase() + News_BASE_URL2;
        Log.i("News_FINAL_URL", News_FINAL_URL);
        try{

            URL url = createUrl(News_FINAL_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }
            getNewsDataFromJson(jsonResponse);


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;

    }


    private void getNewsDataFromJson(String NewsJsonStr)
            throws JSONException {


        try {


            Log.i("getNewsDataFromJson", "try");
            JSONObject baseJsonResponse = new JSONObject(NewsJsonStr);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray resultsInfo = response.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(resultsInfo.length());

            for (int i = 0; i < resultsInfo.length(); i++) {
                ContentValues NewsValues = new ContentValues();

                JSONObject currentInfo = resultsInfo.getJSONObject(i);
                String webTitle = currentInfo.getString("webTitle");
                String webUrl = currentInfo.getString("webUrl");
                NewsValues.put(NewsContract.NewsEntry.COLUMN_WEBTITLE, webTitle);
                NewsValues.put(NewsContract.NewsEntry.COLUMN_WEBURL, webUrl);

                if (currentInfo.has("fields")){
                    JSONObject fields = currentInfo .getJSONObject("fields");
                    String thumbnailUrl = fields.getString("thumbnail");
                    if ( thumbnailUrl != null) {
                        NewsValues.put(NewsContract.NewsEntry.COLUMN_THUMBNAIL, thumbnailUrl);
                    }

                } else{
                    NewsValues.put(NewsContract.NewsEntry.COLUMN_THUMBNAIL, "");
                }
                NewsValues.put(NewsContract.NewsEntry.COLUMN_TAGS, mtab);

                cVVector.add(NewsValues);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("recordTags", Context.MODE_PRIVATE);


                String previousTags = sharedPreferences.getString("previous", null);

                String currentTags = sharedPreferences.getString("current", null);
                if (currentTags != null && currentTags.equals(mtab)) {
                    getContext().getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI, NewsContract.NewsEntry.COLUMN_TAGS +"=?",
                            new String[]{currentTags });
                } else{
                    getContext().getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI, NewsContract.NewsEntry.COLUMN_TAGS +"=?",
                            new String[]{previousTags });
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("previous", previousTags);
                editor.putString("current", mtab);
                editor.commit();

                getContext().getContentResolver().bulkInsert(NewsContract.NewsEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.i("configurePeriodicSync", "configurePeriodicSync");
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    public static void syncImmediately(Context context, String tab) {
        Log.i("syncImmediately", "syncImmediately");
        mtab = tab;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


    public static Account getSyncAccount(Context context) {

        Log.i("getSyncAccount", "getSyncAccount");
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        NewsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context,mtab);
    }


    public static void initializeSyncAdapter(Context context) {
        Log.i("initializeSyncAdapter", "initializeSyncAdapter");
        getSyncAccount(context);

    }
}
