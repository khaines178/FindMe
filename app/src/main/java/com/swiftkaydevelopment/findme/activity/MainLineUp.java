package com.swiftkaydevelopment.findme.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.fragment.NavigationDrawerFragment;
import com.swiftkaydevelopment.findme.gcm.QuickstartPreferences;
import com.swiftkaydevelopment.findme.gcm.RegistrationIntentService;
import com.swiftkaydevelopment.findme.managers.UserManager;
import com.swiftkaydevelopment.findme.newsfeed.NewsFeedFrag;


/**
 * Created by Kevin Haines on 2/5/2015.
 */
public class MainLineUp extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final int NEWSFEED = 1;
    private static final int FINDPEOPLE = 2;
    private static final int PROFILEVIEWS = 3;
    private static final int MATCHES = 1000;
    private static final int PHOTOS = 4;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /**
     * Creates a new Intent to start this activity
     *
     * @param context Calling Context
     * @return new Intent to start this activity with
     */
    public static Intent createIntent(Context context){
        return new Intent(context, MainLineUp.class);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.mainlineup;
    }

    @Override
    protected Context getContext() {
        return this;
    }

    @Override
    protected void createActivity(Bundle b) {
        mToolbar = (Toolbar) findViewById(R.id.toolbarNavigation);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        setSupportActionBar(mToolbar);

        initializeDrawer();

        displayView(NEWSFEED);

        registerGCMReceiver();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean sentToken = sharedPreferences
                .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
        if (checkPlayServices() && !sentToken) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Registers the Gcm Broadcast receiver
     *
     */
    private void registerGCMReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (!sentToken) {
                    Toast.makeText(MainLineUp.this, getString(R.string.token_error_message), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Initializes the Navigation Drawer
     *
     */
    public void initializeDrawer(){
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navigationDrawerFragment.setUid(uid);
        // Set up the drawer.
        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        displayView(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    /**
     * Displays the appropriate fragment based on navigation drawer selection
     *
     * @param position postion selected
     */
    private void displayView(int position) {
        switch (position) {
            case NEWSFEED:
                if (getSupportFragmentManager().findFragmentByTag(NewsFeedFrag.TAG) == null) {
                    NewsFeedFrag newsFeedFrag = NewsFeedFrag.getInstance(uid);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, newsFeedFrag, NewsFeedFrag.TAG)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case FINDPEOPLE:
                startActivity(FindPeopleActivity.createIntent(MainLineUp.this));
                break;
            case PROFILEVIEWS:
                startActivity(ProfileViewsActivity.createIntent(this));
                break;
            case MATCHES:
                startActivity(MatchActivity.createIntent(this));
                break;
            case PHOTOS:
                startActivity(ViewPhotos.createIntent(this, UserManager.getInstance(uid, this).me()));
                break;
            default:
                break;
        }
    }
}