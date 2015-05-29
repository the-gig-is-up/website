package com.dolphin.thegigisup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import com.dolphin.thegigisup.activitys.LoginActivity;
import com.dolphin.thegigisup.activitys.SignUpActivity;
import com.dolphin.thegigisup.api.*;
import com.dolphin.thegigisup.fragments.*;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import retrofit.client.Response;
import java.util.concurrent.Executors;

/**
 * Main activity that handles the navigation drawer, the fragment changes,
 * the login user information and communication between fragment pages
 *
 * @author Team Dolphin
 */
public class MainActivity extends ActionBarActivity
        implements View.OnClickListener {

    // Thread pooling
    private Runner runner;
    private final Runner.Scope taskScope = new Runner.Scope();

    // The Service API object
    private ServiceInterface service;

    // Shared prefs
    SharedPreferences sharedPreferences;
    public static final String  MYPREFERENCES = "MyPreferences",
                                USERNAME = "Username",
                                TOKEN = "Token",
                                EMAIL = "Email",
                                USERID = "UserID",
                                USERIMAGE = "UserImage";

    // Fragment management
    private FragmentManager fragmentManager;
    private Fragment currFragment;
    private String currFragmentTitle;

    // Drawer components
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private LinearLayout    drawerParent,
                            userDetailsLayout,
                            loginButtonLayout;

    String[] drawerLabels;
    int[]    drawerIDs;
    private int lastChecked;

    // User login
    private String accessToken;
    private static int userID = -1;
    private RoundedImageView userImage;

    private TextView    usernameText,
                        emailText;


    /**
     * Called on creation of the activity to find and initialise variables
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_container);

        // Create thread pool runner
        runner = new Runner(Executors.newCachedThreadPool(),
                            new Handler());

        service = ServiceFactory.createInstance();

        fragmentManager = getFragmentManager();

        usernameText      = (TextView) findViewById(R.id.act_iv_user_name);
        emailText         = (TextView) findViewById(R.id.act_iv_user_email);
        userDetailsLayout = (LinearLayout)
                                findViewById(R.id.act_ll_user_details);
        loginButtonLayout = (LinearLayout)
                                findViewById(R.id.act_ll_login_button);
        userImage         = (RoundedImageView)
                                findViewById(R.id.act_iv_user_image);

        usernameText.setText("");
        emailText.setText("");

        currFragmentTitle = "";

        sharedPreferences = getSharedPreferences(MYPREFERENCES,
                                                 Context.MODE_PRIVATE);
        if (sharedPreferences.contains(USERNAME)) {
            loginUser(sharedPreferences.getString(USERNAME, null),
                    sharedPreferences.getString(EMAIL, null),
                    sharedPreferences.getString(TOKEN, null),
                    sharedPreferences.getInt(USERID, -1), 0);
        }

        /*
         * Drawer configuration
         */
        // Load the drawer data
        TypedArray drawerElements = getResources()
                .obtainTypedArray(R.array.drawer_array);

        // Build a list of drawer item ids and the strings themselves
        drawerIDs    = new int   [drawerElements.length()];
        drawerLabels = new String[drawerElements.length()];

        for (int i = 0; i != drawerElements.length(); i++) {
            drawerIDs   [i] = drawerElements.getResourceId(i, -1);
            drawerLabels[i] = drawerElements.getString(i);
        }

        // Load the drawer layout
        drawerLayout    = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerParent    = (LinearLayout) findViewById(R.id.left_drawer);
        drawerList      = (ListView) findViewById(R.id.left_drawer_list);


        // Set the custom drawer shadow to overlay the main fragment content
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                                     GravityCompat.START);

        // Set up the drawer listview with titles and clickListeners
        drawerList.setAdapter(new ArrayAdapter<>(this,
                                                 R.layout.drawer_list_item,
                                                 drawerLabels));

        // Listens for clicks on drawer items
        ListView.OnItemClickListener drawerClickListener
                = new SlideMenuClickListener();

        drawerList.setOnItemClickListener(drawerClickListener);
        drawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // ActionBarDrawerToggle ties together the the interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = new ActionBarDrawerToggle(this,
                                                 drawerLayout,
                                                 R.string.drawer_open,
                                                 R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(currFragmentTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        // Enable ActionBar app icon to behave as action to toggle the drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            boolean hasComeFromConfirmed =
                    intent.getBooleanExtra(CheckoutConfirmFragment.CONFIRMED,
                                           false);
            // First time starting up
            if (hasComeFromConfirmed) {
                changeFragment(new BookingsFragment(), R.string.frag_bookings);
                lastChecked = 2;
            } else {
                changeFragment(new FeaturedFragment(), R.string.frag_featured);
                lastChecked = 0;
            }

        }
        drawerList.setItemChecked(lastChecked, true);
    }


    /**
     * Handle the on click methods for buttons within the navigation drawer
     * and on some of the fragment pages
     * @param v A clicked view v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.fea_bt_view_all):
                findAndForceDrawerSelect(R.string.frag_events);
                break;

            case (R.id.act_bt_login):
                launchLoginActivity();
                drawerLayout.closeDrawer(drawerParent);
                break;

            case (R.id.act_bt_logout):
                logoutUser();
                drawerLayout.closeDrawer(drawerParent);
                break;

            case (R.id.act_bt_signup):
                launchSignUpActivity();
                drawerLayout.closeDrawer(drawerParent);
                break;

            case (R.id.act_ll_user_layout):
                findAndForceDrawerSelect(R.string.frag_settings);
                break;

            default:
                break;
        }
    }

    /**
     * Find the position of where a fragment is
     * in the fragment drawer and the fragment array of names
     * and then force change it to override the check
     */
    private void findAndForceDrawerSelect(int stringID) {
        String nameOfFrag = getString(stringID);
        for (int i = 0; i < drawerLabels.length; i++) {
            if (nameOfFrag.equalsIgnoreCase(drawerLabels[i])) {
                forceDrawerSelect(i, stringID);
                break;
            }
        }
    }

    /**
     * Launch an intent to open the login page and wait for the results
     * inputted by the user
     */
    private void launchLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, LoginActivity.REQUEST_LOGIN);
    }

    /**
     * Launch an intent for the user to sign up and wait for the results
     * from that page
     */
    private void launchSignUpActivity() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivityForResult(i, SignUpActivity.REQUEST_SIGNUP);
    }

    /**
     * On results from the log in page or the sign up page, use the data to
     * update the navigation drawer and the shared preferences
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        // If the activity that finished was the
        // Login activity
        if (requestCode == LoginActivity.REQUEST_LOGIN) {
            // If the login activity returned a login
            if (resultCode == LoginActivity.RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    String userName = extras.getString(LoginActivity.USERNAME);
                    String userEmail = extras.getString(LoginActivity.EMAIL);
                    String accessToken = extras.getString(LoginActivity.TOKEN);
                    int userID = extras.getInt(LoginActivity.USERID);
                    loginUser(userName, userEmail, accessToken, userID, 1);
                }
            }
        }
        else if (requestCode == SignUpActivity.REQUEST_SIGNUP) {
            // If the sign up activity signed up a valid user and logged them in
            if (resultCode == SignUpActivity.RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    String userName = extras.getString(SignUpActivity.USERNAME);
                    String userEmail = extras.getString(SignUpActivity.EMAIL);
                    String accessToken = extras.getString(SignUpActivity.TOKEN);
                    int userID = extras.getInt(SignUpActivity.USERID);
                    loginUser(userName, userEmail, accessToken, userID, 1);
                }
            }
        }
    }

    /**
     * Creates the options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        createIconsInActionBar(menu);
        createSearchManager(menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when an item is selected from the options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            refreshFragment();
            return true;
        }


        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    /**
     * Create a search manager, to allow the user to search for artists/events
     * @param menu A given menu
     */
    private void createSearchManager(Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        // Make the search view hint text white
        searchView.setQueryHint(Html.fromHtml(getHtmlText()));
    }

    /**
     * Returns a valid HTML String object for use with the search bar
     * @return String object that is used in the search bar
     */
    private String getHtmlText() {
        return "<font color = #ffffff>Search for an event</font>";
    }

    /**
     * Create items in the menu bar for the user to search and refresh pages
     */
    private void createIconsInActionBar(Menu menu) {
        menu.findItem(R.id.action_refresh)
                .setIcon(
                        new IconicsDrawable(this,
                            GoogleMaterial.Icon.gmd_refresh)
                            .colorRes(R.color.color_white)
                            .sizeDp(20));
        menu.findItem(R.id.action_search)
                .setIcon(
                        new IconicsDrawable(this,
                            GoogleMaterial.Icon.gmd_search)
                            .colorRes(R.color.color_white)
                            .sizeDp(20));
    }


    /**
     * Transitions to a new fragment
     *
     * @param fragment The fragment to transition to.
     * @param titleID The resource ID of the fragment title.
     */
    private void changeFragment(Fragment fragment, int titleID) {

        // Switch to the new fragment
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
        currFragment = fragment;

        // Update the title
        currFragmentTitle = getString(titleID);
        getSupportActionBar().setTitle(currFragmentTitle);

        // Refresh it if necessary
        refreshFragment();
    }

    /**
     * Refreshes the current fragment from the API
     */
    private void refreshFragment() {
        if (currFragment == null)
            return;

        // Some fragments don't need refreshing
        if (!(currFragment instanceof Refreshable))
            return;

        Refreshable refreshable = (Refreshable) currFragment;
        refreshable.refresh(runner, service);
    }


    /**
     * Called when an item is selected in the drawer.
     *
     * @param position Position of the drawer item.
     * @param id Resource ID of the drawer item.
     */
    private void drawerSelect(int position, int id) {
        boolean isSameFragment =
                drawerList.getCheckedItemPosition() == lastChecked;
        if (!isSameFragment) {
            // Switch to the intended fragment
            switch (id) {
                case R.string.frag_featured:
                    changeFragment(new FeaturedFragment(), id);
                    break;

                case R.string.frag_events:
                    changeFragment(new EventsFragment(), id);
                    break;

                case R.string.frag_artists:
                    changeFragment(new ArtistFragment(), id);
                    break;

                case R.string.frag_bookings:
                    changeFragment(new BookingsFragment(), id);
                    break;

                case R.string.frag_settings:
                    changeFragment(new SettingsFragment(), id);
                    break;

                default:
            }

            // Update the selected item and title, then close the drawer
            drawerList.setItemChecked(position, true);
            lastChecked = position;
        }
        drawerLayout.closeDrawer(drawerParent);
    }

    /**
     * Switch the fragment regardless of whether or
     * not it is in view
     *
     * @param position Position of the drawer item.
     * @param id Resource ID of the drawer item.
     */
    private void forceDrawerSelect(int position, int id) {
        lastChecked = -1;
        drawerSelect(position, id);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        runner.stop();
    }

    /**
     * Override the resume method to make sure if the shared preferences have
     * login information in, the navigation drawer will be updated with that
     * information
     */
    @Override
    public void onResume() {
        super.onResume();
        if (sharedPreferences.contains(USERNAME)) {
            loginUser(sharedPreferences.getString(USERNAME, null),
                    sharedPreferences.getString(EMAIL, null),
                    sharedPreferences.getString(TOKEN, null),
                    sharedPreferences.getInt(USERID, 0), 0);
        }
    }

    /**
     * If the screen orientation changes, we need to
     * update UI elements
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Logs in a user
     *
     * @param userName Given username
     * @param userEmail Given user email
     * @param accessToken Given access token
     */
    public void loginUser(String userName, String userEmail, String accessToken,
                          int userID, int toastShow) {
        if (toastShow == 1) {
            Toast.makeText(getApplicationContext(),
                    userName+" logged in",
                    Toast.LENGTH_SHORT)
                    .show();
        }
        usernameText.setText(userName);
        emailText.setText(userEmail);
        this.accessToken = accessToken;
        this.userID = userID;
        showLoginLayout(false);

        // Update the user image if one has been selected
        if (sharedPreferences.contains(USERIMAGE)) {
            String bitmapString = sharedPreferences.getString(USERIMAGE, null);
            Bitmap image = StringToBitMap(bitmapString);
            userImage.setImageBitmap(image);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, userName);
        editor.putString(EMAIL, userEmail);
        editor.putInt(USERID, userID);
        editor.putString(TOKEN, accessToken);
        editor.commit();

        // Update settings fragment with this information if it is currently
        // selected
        if (currFragmentTitle.equals("Settings")) {
            SettingsFragment settingsFragment = (SettingsFragment)
                        fragmentManager.findFragmentById(R.id.main_container);
            settingsFragment.userLoggedIn();
        }
    }

    /**
     * Convert a base-64 string to a bitmap
     *
     * @param encodedString An encoded bitmap string
     * @return Bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0,
                                                         encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    /**
     * If the user is logged in, show this layout in the navigation drawer
     *
     * @param show True to show logged in, false to show logged out
     */
    private void showLoginLayout(boolean show) {
        if (show) {
            loginButtonLayout.setVisibility(View.VISIBLE);
            userDetailsLayout.setVisibility(View.GONE);
        } else {
            loginButtonLayout.setVisibility(View.GONE);
            userDetailsLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Logs out a user and sets the preferences accordingly
     */
    public void logoutUser() {
        LogoutUser task = new LogoutUser(service);
        runner.run(task, taskScope);
        drawerLayout.closeDrawer(drawerParent);
        showLoginLayout(true);
        Toast.makeText(getApplicationContext(),
                usernameText.getText().toString()+" logged out",
                Toast.LENGTH_SHORT)
                .show();
        usernameText.setText("");
        emailText.setText("");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USERNAME);
        editor.remove(EMAIL);
        editor.remove(USERID);
        editor.remove(TOKEN);
        if (sharedPreferences.contains(USERIMAGE)) {
            userImage.setImageDrawable(getResources().getDrawable(
                                                            R.drawable.event));
            editor.remove(USERIMAGE);
        }
        editor.commit();

        // Update settings fragment with this information if it is currently
        // selected
        if (currFragmentTitle.equals("Settings")) {
            SettingsFragment settingsFragment = (SettingsFragment)
                        fragmentManager.findFragmentById(R.id.main_container);
            settingsFragment.userLoggedOut();
        }
    }

    /**
     * Using the user's access token, perform a log out operation using the API
     */
    private class LogoutUser extends ApiTask<Response> {
        public LogoutUser(ServiceInterface service) {
            super(service);
        }

        /**
         * Query the API to log out the user
         * @param service A serviceInterface object to interact with the API
         * @return A response from the API
         */
        @Override
        public Response doQuery(ServiceInterface service) {
            return service.logoutUser(accessToken);
        }

        @Override
        public void done(Response response) {}

        /**
         * Print the appropriate error information to a toast if the log out
         * operation fails
         */
        @Override
        public void failed(Exception e) {
            Toast.makeText(getApplicationContext(),
                    e.toString(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * @return The current user's access token
     */
    public String getAccessToken() {
        return this.accessToken;
    }

    /**
     * @return The ID of the logged in user
     */
    public static int getUserID() {
        return userID;
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {

        // Translates and dispatches item clicks to parent activity
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long rowId) {
            // Delay the closing of the nav drawer
            // Fixes GUI lag glitch
            drawerLayout.closeDrawer(drawerParent);
            int id = drawerIDs[position];
            new Handler().postDelayed(() -> drawerSelect(position, id), 350);
        }
    }
}