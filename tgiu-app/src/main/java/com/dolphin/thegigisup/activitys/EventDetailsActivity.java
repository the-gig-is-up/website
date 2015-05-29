package com.dolphin.thegigisup.activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.models.Artist;
import com.dolphin.thegigisup.models.Event;
import com.shamanland.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Activity that displays an event's information and image details
 *
 * @author Team Dolphin 24/02/2015.
 */
public class EventDetailsActivity extends ActionBarActivity {

    // Initialise the appropriate variables
    private TextView artist;
    private TextView location;
    private TextView dateTime;
    private TextView cost;
    private TextView tickets;
    private TextView description;
    private ImageView artistImage;
    final Context context = this;
    private String strImageURL;
    private Event event;
    private TextView warning;
    private TextView ticketIcon;
    private TextView artists;

    // Initialise shared preferences variables to use the saved user login
    // details
    private SharedPreferences sharedpreferences;
    public static final String MYPREFERENCES = "MyPreferences" ;
    public static final String USERNAME = "Username";
    public static final String TOKEN = "Token";
    public static final String EMAIL = "Email";
    public static final String USERID = "UserID";

    /**
     * On creating the view, use the xml layout file and find the appropriate
     * views for setting the information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_event_details);

        setTextViews();
        setButtons();
        setEvent();
        setActionBar();
        setImage();
        sharedpreferences = getSharedPreferences(
                                MYPREFERENCES,
                                Context.MODE_PRIVATE);
    }

    /**
     * Find and set the buttons' click handlers
     */
    private void setButtons() {
        Button btnOpenMap       = (Button) findViewById(R.id.eve_bt_open_map);
        Button btnAddToCal      = (Button) findViewById(R.id.eve_bt_add_to_cal);
        Button btnShowArtists   = (Button) findViewById(R.id.eve_bt_artists);

        // Connect to Checkout page via buy now button
        FloatingActionButton btnBuy = (FloatingActionButton)
                findViewById(R.id.fea_bt_buy);

        btnBuy.setOnClickListener(new BuyButtonClick());
        btnOpenMap.setOnClickListener(new OpenMapButtonClick());
        btnAddToCal.setOnClickListener(new CalendarButtonClick());
        btnShowArtists.setOnClickListener(new ArtistsButtonClick());
    }

    /**
     * Set the colour and title of the action bar
     */
    private void setActionBar() {
        getSupportActionBar()
            .setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.square));
        getSupportActionBar().setTitle(event.getEventName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Set the image using the event image URL and if an error occurs,
     * set the default image
     */
    private void setImage() {
        strImageURL = event.getArtists().get(0).getImageURL();

        try {
            String[] urlSplit = strImageURL.split("64");
            String largeResolutionURL = urlSplit[0] + "252" + urlSplit[1];
            Picasso.with(artistImage.getContext())
                    .load(largeResolutionURL)
                    .fit()
                    .centerCrop()
                    .into(artistImage);
        } catch (ArrayIndexOutOfBoundsException e) {
            artistImage.setBackgroundResource(R.drawable.event);
        }
    }

    /**
     * Set the event information from the bundle
     * @return Event from a Parceable object
     */
    private void setEvent() {
        Bundle b = getIntent().getExtras();
        event = b.getParcelable("com.dolphin.thegigisup.Event");
        setEventInfo(event);
    }

    /**
     * Helper function to set all the text view's that
     * need populating
     */
    private void setTextViews() {
        artist      = (TextView) findViewById(R.id.fea_tv_artistName);
        location    = (TextView) findViewById(R.id.fea_tv_location);
        dateTime    = (TextView) findViewById(R.id.fea_tv_timedate);
        cost        = (TextView) findViewById(R.id.fea_tv_cost);
        tickets     = (TextView) findViewById(R.id.fea_tv_ticketNum);
        artistImage = (ImageView) findViewById(R.id.featured_image);
        description = (TextView) findViewById(R.id.fea_tv_description);
        warning     = (TextView) findViewById(R.id.eve_tv_warning);
        ticketIcon  = (TextView) findViewById(R.id.eve_itv_ticket_icon);
        artists     = (TextView) findViewById(R.id.fea_tv_artists);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to show the location of the event using an intent
     *
     * @param geoLocation The Uri of the geo location of the event
     */
    public void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Method to manage if the user has logged in or signed up from the
     * eventDetailsActivity page
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
                    loginUser(userName, userEmail, accessToken, userID);
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
                    loginUser(userName, userEmail, accessToken, userID);
                }
            }
        }
    }

    /**
     * Method to log in the user and update the shared preferences with the
     * correct information
     *
     * @param userName User's username
     * @param userEmail User's email
     * @param accessToken User's accessToken when logged in
     * @param userID User's ID number
     */
    public void loginUser(String userName, String userEmail, String accessToken,
                          int userID) {
        Toast.makeText(getApplicationContext(),
                userName + " logged in",
                Toast.LENGTH_SHORT)
                .show();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USERNAME, userName);
        editor.putString(EMAIL, userEmail);
        editor.putInt(USERID, userID);
        editor.putString(TOKEN, accessToken);
        editor.commit();
    }

    /**
     * Shows/hides tickets left warning
     *
     * @param show true if you want to show it, false otherwise.
     */
    private void toggleWarning(boolean show) {
        int visible = warning.getVisibility();
        if (show && visible == View.GONE) {
            warning.setVisibility(View.VISIBLE);
            tickets.setTextColor(getResources().getColor(R.color.color_red));
            ticketIcon.setTextColor(getResources().getColor(R.color.color_red));
        } else if (!show && visible == View.VISIBLE) {
            warning.setVisibility(View.GONE);
            ticketIcon.setTextColor(getResources().getColor(
                                    R.color.color_darker_gray));
        }
    }

    /**
     * Add event to calendar by using the event information and adding it to a
     * calendar intent
     */
    public void addToCal(String title, String location, Calendar begin,
                         Calendar end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                          begin.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        end.getTimeInMillis());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Set the various text view's to the right info
     * @param event Event to get data from
     */
    public void setEventInfo(Event event) {
        DateTime dateTimeObj = event.getDate();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yy");

        // Set the appropriate text information
        artist.setText(event.getEventName());
        location.setText(event.getVenue().getName() +
                "\n" +
                event.getVenue().getLocation() +
                "\n" +
                event.getVenue().getAddress());

        cost.setText("£" + event.getPrice());

        if (event.getEndTime().equals("00:00")){
            dateTime.setText(event.getStartTime() +
                    " on " +
                    fmt.print(dateTimeObj));
        } else {
            dateTime.setText(event.getStartTime() +
                    " - " + event.getEndTime() +
                    " on " +
                    fmt.print(dateTimeObj));
        }

        String attending = event.getArtists().size() +
                " %s attending";
        if (event.getArtists().size() > 1) {
            String formatted = String.format(attending, "acts");
            artists.setText(formatted);
        } else {
            String formatted = String.format(attending, "act");
            artists.setText(formatted);
        }

        int ticketsLeft = event.getTicketsLeft();
        tickets.setText(ticketsLeft + " tickets left");
        if (event.isLowTickets()) {
            toggleWarning(true);
        }

        String desc = String.format("The world famous artist %s" +
                        " is hosting their sellout tour for %s" +
                        " in the %s.\n\n%s",
                event.getArtists().get(0).getName(),
                event.getDate().getYear(),
                event.getVenue().getName(),
                event.getDescription()
        );
        description.setText(desc);
    }

    /**
     * Class to handle the buy button's click
     */
    private final class BuyButtonClick implements View.OnClickListener {

        /**
         * What happens on click
         * @param view The View that was clicked
         */
        public void onClick(View view) {
            if (sharedpreferences.contains(USERNAME)) {
                Intent intent = new Intent(context, CheckoutActivity.class);
                intent.putExtra("com.dolphin.thegigisup.Event", event);
                EventDetailsActivity.this.startActivity(intent);
            }
            if (!sharedpreferences.contains(USERNAME)) {
                AlertDialog.Builder alertDialogBuilder =
                        new AlertDialog.Builder(EventDetailsActivity.this);
                alertDialogBuilder
                        .setTitle("Please login/sign up to purchase tickets");
                alertDialogBuilder.setPositiveButton(
                        "Login",
                        (dialog, which) -> {
                            Intent i = new Intent(getBaseContext(),
                                    LoginActivity.class);
                            startActivityForResult(i,
                                    LoginActivity.REQUEST_LOGIN);
                        });
                alertDialogBuilder.setNegativeButton(
                        "Sign up",
                        (dialog, which) -> {
                            Intent i = new Intent(getBaseContext(),
                                    SignUpActivity.class);
                            startActivityForResult(i,
                                    SignUpActivity.REQUEST_SIGNUP);
                        });
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
            }
        }
    }

    /**
     * Class to handle the open map button's click
     */
    private final class OpenMapButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String uriBegin = "geo:0,0?q=";
            String query = Uri.encode(event.getVenue().getName() +
                    " " +
                    event.getVenue().getLocation());
            Uri uri = Uri.parse(uriBegin + query);
            EventDetailsActivity.this.showMap(uri);
        }
    }

    /**
     * Class to handle the calendar button's click
     */
    private final class CalendarButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String title = event.getEventName() + " Gig";
            String date = event.getDate().toString().substring(0, 10);
            String location1 = event.getVenue().getLocation();
            String beginTime = event.getOriginalStartTime();
            String endTime = event.getOriginalEndTime();

            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd HH:mm:ss");
            DateTime beginDate = formatter.parseDateTime(date + " " +
                    beginTime);

            DateTimeFormatter formatter2 = DateTimeFormat
                    .forPattern("yyyy-MM-dd HH:mm:ss");
            DateTime endDate = formatter2.parseDateTime(date + " " +
                    endTime);

            Calendar calBegin = beginDate.toCalendar(Locale.ENGLISH);
            Calendar calEnd = endDate.toCalendar(Locale.ENGLISH);

            EventDetailsActivity.this.addToCal(title, location1, calBegin, calEnd);
        }
    }

    /**
     * Class to handle show more artists button click
     */
    private final class ArtistsButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(),
                    ExpandableArtistActivity.class);
            ArrayList<Artist> temp = new ArrayList<>();
            for (Artist name : event.getArtists()) {
                temp.add(name);
            }
            i.putParcelableArrayListExtra("artists", temp);
            startActivity(i);
        }
    }
}