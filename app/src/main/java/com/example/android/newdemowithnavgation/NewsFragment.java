package com.example.android.newdemowithnavgation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.android.newdemowithnavgation.data.NewsContract;
import com.example.android.newdemowithnavgation.sync.NewsSyncAdapter;

import java.util.ArrayList;
import java.util.Vector;

import static android.os.Build.VERSION_CODES.N;
import static com.example.android.newdemowithnavgation.R.id.textView;


/**
 * Created by cheny on 2017/2/4.
 */

public class NewsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener{
    private static final String ARG_POSITION = "position";

    private int position;
    private String[] TITLES = {"Home", "US", "Politics", "World"};

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private Uri mUri;

    private ProgressBar progressBar;

    private static final String SELECTED_KEY = "selected_position";

    private static final int News_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            NewsContract.NewsEntry.TABLE_NAME + "." + NewsContract.NewsEntry._ID,
            NewsContract.NewsEntry.COLUMN_WEBTITLE,
            NewsContract.NewsEntry.COLUMN_WEBURL,
            NewsContract.NewsEntry.COLUMN_THUMBNAIL,
            NewsContract.NewsEntry.COLUMN_TAGS

    };

    static final int COL_ID = 0;
    static final int COL_WEBTITLE = 1;
    static final int COL_WEBURL = 2;
    static final int COL_THUMBNAIL = 3;

    private NewsAdapter mNewsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;



    private ArrayList<String> getDragTags() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dragTips",
                Context.MODE_PRIVATE);
        ArrayList<String> defaultDragTips = new ArrayList<>();
        for (int i = 0; i < TITLES.length; i++) {
            defaultDragTips.add(TITLES[i]);
        }
        int size = sharedPreferences.getInt("status_size", 0);
        if (size == 0) {
            return defaultDragTips;
        } else{
            for(int i =0; i < size; i++) {
                defaultDragTips.add(sharedPreferences.getString("status"+ i, null));
            }
        }

        return defaultDragTips;
    }

    public static NewsFragment newInstance(int position) {
        NewsFragment f = new NewsFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       position = getArguments().getInt(ARG_POSITION);
       // Log.i("demo1", String.valueOf(position));

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);

        return rootView;
    }


    @Override
    protected void initData() {
        //getLoaderManager().initLoader(News_LOADER, null, this);

        ArrayList<String> res = getDragTags();
        Log.i("onCreateView", res.get(position));

        NewsSyncAdapter.syncImmediately(getActivity(),res.get(position));


        getLoaderManager().restartLoader(News_LOADER,null, this);

        swipeRefreshLayout.setOnRefreshListener(this);
        onRefresh();

        mNewsAdapter = new NewsAdapter(getActivity(), null, 0);
        mListView.setAdapter(mNewsAdapter);
        progressBar.setVisibility(View.GONE);



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Intent intent = new Intent(getActivity(), DetailsActivity.class);
                //MovieInfo movie = mAdapter.getItem(position);
                //intent.putExtra(Intent.EXTRA_TEXT, movie);
                //startActivity(intent);


            }
        });
    }


    public void refreshData() {

            //如果被回收的Fragment会重新从Bundle里获取数据,所以也要更新一下
            Bundle args = getArguments();
            if (args != null) {
                args.putInt(ARG_POSITION, position);
            }

            if (isFragmentVisible()) {
                initData();
            } else {
                setForceLoad(true);
            }

    }

    @Override
    protected void setDefaultFragmentTitle(String title) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //getLoaderManager().initLoader(News_LOADER, null, this);
       // swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh);
        //swipeRefreshLayout.setOnRefreshListener(this);
        //swipeRefreshLayout.setRefreshing(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mUri = NewsContract.NewsEntry.CONTENT_URI;
        ArrayList<String> res = getDragTags();
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    NewsContract.NewsEntry.COLUMN_TAGS +"=?",
                    new String[]{res.get(position)},
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNewsAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNewsAdapter.swapCursor(null);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {


        ArrayList<String> res = getDragTags();
        NewsSyncAdapter.syncImmediately(getActivity(),res.get(position));
        swipeRefreshLayout.setRefreshing(false);
        //Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();

    }
}