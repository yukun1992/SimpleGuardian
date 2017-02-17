package com.example.android.newdemowithnavgation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.example.android.newdemowithnavgation.addtags.ui.TagActivity;
import com.example.android.newdemowithnavgation.sync.NewsSyncAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);


        adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.init();

        pager.setAdapter(adapter);


        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        NewsSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onRestart() {


       // adapter.notifyDataSetChanged();
        //tabs.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        adapter.refreshAllFragment();
        tabs.notifyDataSetChanged();

        super.onRestart();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, TagActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private String[] TITLES = {"Home", "US", "Politics", "World"};
        private List<NewsFragment> fragmentList = new ArrayList<>();



        public ArrayList<String> getDragTags() {
            SharedPreferences sharedPreferences = getSharedPreferences("dragTips", Context.MODE_PRIVATE);
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

        public void init() {
            fragmentList.clear();
            ArrayList<String> res = getDragTags();
            for (int i = 0; i < res.size(); i++) {
                fragmentList.add(NewsFragment.newInstance(i));
            }
        }

        public void refreshAllFragment() {

                for (NewsFragment fragment : fragmentList) {

                    String pageTitle = fragment.getTitle();
                    if (pageTitle != null ) {
                        fragment.refreshData();
                    }
                }

        }


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            ArrayList<String> res = getDragTags();
            return res.get(position);
        }

        @Override
        public int getCount() {
            ArrayList<String> res = getDragTags();
            return res.size();
        }

        @Override
        public Fragment getItem(int position) {

            return NewsFragment.newInstance(position);
        }


    }



}
