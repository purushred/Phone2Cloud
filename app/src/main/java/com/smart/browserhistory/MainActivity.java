package com.smart.browserhistory;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dropbox.core.android.Auth;
import com.smart.browserhistory.dropbox.DropboxClientFactory;
import com.smart.browserhistory.fragment.CustomFragment;
import com.smart.browserhistory.fragment.MediaTabFragment;
import com.smart.browserhistory.util.AppUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private CustomFragment fragment;
    private Snackbar snackbar;
    private MenuItem selectAllMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fabButton);
//        fabButton.setVisibility(View.GONE);
        fabButton.setOnClickListener(this);
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        fragment = new MediaTabFragment();
        mFragmentTransaction.add(R.id.containerView, fragment, "whatsapp_media_fragment").commit();
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().findItem(R.id.nav_item_whatsapp).setChecked(true);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), "", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mFragmentManager.beginTransaction().replace(R.id.containerView, fragment).commit();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (selectAllMenuItem != null) {
            selectAllMenuItem.setChecked(false);
            selectAllMenuItem.setIcon(R.drawable.ic_check_box_outline_blank_white_24dp);
        }
        switch (menuItem.getItemId()) {
            case R.id.nav_item_whatsapp:
                mNavigationView.getMenu().findItem(R.id.nav_item_whatsapp).setChecked(true);
                fragment = new MediaTabFragment();
                break;
        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                /*snackbar.setText("Connecting to dropbox ..");
                snackbar.show();*/
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                if (AppUtil.getAccessToken(MainActivity.this) == null) {
                    String accessToken = Auth.getOAuth2Token();
                    if (accessToken != null) {
                        AppUtil.setAccessToken(accessToken, MainActivity.this);
                        if (!DropboxClientFactory.isDbxClientInitialised())
                            DropboxClientFactory.init(accessToken);
                    } else
                        Log.e("MainActivity.onResume", "WhatsApp Not linked:");
                } else {
                    if (!DropboxClientFactory.isDbxClientInitialised())
                        DropboxClientFactory.init(AppUtil.getAccessToken(MainActivity.this));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                /*snackbar.setText("Conncted to dropbox.");
                snackbar.setDuration(Snackbar.LENGTH_SHORT);
                snackbar.show();*/
            }
        }.execute();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        selectAllMenuItem = menu.findItem(R.id.action_select_all);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.sortBySize:
                fragment.sort(AppUtil.SORT_BY_SIZE);
                break;
            case R.id.sortByUploadStatus:
                fragment.sort(AppUtil.SORT_BY_UPDATE_STATUS);
                break;
            case R.id.action_share:
                fragment.share();
                break;
            case R.id.action_select_all:
                if (item.isChecked()) {
                    item.setIcon(R.drawable.ic_check_box_outline_blank_white_24dp);
                    item.setChecked(false);
                    fragment.handleCheckboxClick(false);
                } else {
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_check_box_white_24dp);
                    fragment.handleCheckboxClick(true);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method returns the backup location from the preferences.
     *
     * @return
     */
    public String getBackupLocation() {
        SharedPreferences settings = getSharedPreferences(AppUtil.PHONE2CLOUD_PREF, Activity.MODE_PRIVATE);
        return settings.getString("backupDirPath",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
    }

    @Override
    public void onClick(View view) {
        fragment.handleFABClick();
    }
}