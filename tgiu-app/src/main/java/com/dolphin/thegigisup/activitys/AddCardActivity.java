package com.dolphin.thegigisup.activitys;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.widget.*;
import com.dolphin.thegigisup.*;
import com.dolphin.thegigisup.api.ApiTask;
import com.dolphin.thegigisup.api.Runner;
import com.dolphin.thegigisup.api.ServiceFactory;
import com.dolphin.thegigisup.api.ServiceInterface;
import com.dolphin.thegigisup.models.Card;
import retrofit.client.Response;
import java.util.Calendar;
import java.util.concurrent.Executors;

/**
 * Activity page used to let the user create and add new card information to
 * their payment options
 *
 * @author Team Dolphin
 */
public class AddCardActivity extends ActionBarActivity
                             implements View.OnClickListener{

    // Initialise strings used in spinners
    String[] months = { "01", "02", "03", "04", "05",
            "06","07","08","09","10","11","12"};
    String[] years = {"2015", "2016", "2017", "2018",
            "2019","2020","2021","2022","2023","2024"};
    Calendar now = Calendar.getInstance();

    // Initialise fragment and API call variables
    FragmentTransaction fragmentTransaction;
    Fragment currentFragment;
    private final Runner.Scope taskScope = new Runner.Scope();
    private Runner runner;
    private ServiceInterface service;
    private int userID;

    // Initialise view variables to be used in create view
    private TextView spinnerError;
    private EditText cardName, cardText, cardSecurity, cardAddress;
    private Spinner expMonth, expYear;
    private String expDate;

    // Initialise the shared preferences variables used to get the current
    // logged in user ID
    private SharedPreferences sharedpreferences;
    public static final String MYPREFERENCES = "MyPreferences" ;
    public static final String USERID = "UserID";

    public static final int REQUEST_ADDCARD = 1;

    /**
     * On creation of the view, initialise and set the appropriate variables
     *
     * @param savedInstanceState Use a savedInstanceState if one exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.card_fragment);
        getSupportActionBar().setTitle("Add card");

        // Get the user ID using the shared preferences
        sharedpreferences = this.getSharedPreferences(MYPREFERENCES,
                                                      Context.MODE_PRIVATE);
        userID = sharedpreferences.getInt(USERID, 0);

        runner = new Runner(Executors.newCachedThreadPool(),
                new Handler());
        service = ServiceFactory.createInstance();

        // Find the relevant views on the page
        cardName = (EditText) findViewById(R.id.card_et_name);
        cardText = (EditText) findViewById(R.id.card_et_number);
        cardSecurity = (EditText) findViewById(R.id.card_et_sec);
        cardAddress = (EditText) findViewById(R.id.card_et_add);
        spinnerError = (TextView) findViewById(R.id.card_tv_exper);
        expMonth = (Spinner) findViewById(R.id.card_sp_month);
        expYear = (Spinner) findViewById(R.id.card_sp_year);

        // Create drop downs for expiry date
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_dropdown, months);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_dropdown, years);

        expMonth.setAdapter(monthAdapter);
        expYear.setAdapter(yearAdapter);

        // Connect buy now button
        Button buyNow = (Button) findViewById(R.id.card_but_add);
        buyNow.setOnClickListener(this);
    }

    /**
     * Set the validations when the add card button is clicked to check all
     * the values are valid
     *
     * @param v The clicked button view
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_but_add:
                boolean valid = true;

                expDate = expYear.getSelectedItem().toString() +
                        "-" + expMonth.getSelectedItem().toString() + "-01";
                int selMonth = Integer.parseInt(expMonth.getSelectedItem().
                        toString());
                int selYear = Integer.parseInt(expYear.getSelectedItem().
                        toString());

                // If selected month is less than current month and selected
                // year is equal to selected year OR if selected year is less
                // than current year set expiry date error message
                if((selMonth < (now.get(Calendar.MONTH) + 1) &&
                        selYear == (now.get(Calendar.YEAR))) ||
                        selYear < (now.get(Calendar.YEAR)))
                {
                    spinnerError.setError(Html.
                            fromHtml("Please enter a valid expiry date"));
                    valid = false;
                }
                // If card name hasn't been inputted set name error message.
                if(cardName.getText().toString().length() == 0)
                {
                    cardName.setError(Html.
                            fromHtml("Please enter a valid name"));
                    valid = false;
                }
                // If card address hasn't been inputted set address error
                // message.
                if(cardAddress.getText().toString().length() == 0)
                {
                    cardAddress.setError(Html.
                            fromHtml("Please enter a valid address"));
                    valid = false;
                }
                // If card security length isn't 3 digits set security num
                // error message.
                if(cardSecurity.getText().toString().length() != 3)
                {
                    cardSecurity.setError(Html.
                            fromHtml("Please enter a valid security number"));
                    valid = false;
                }
                // If card number length isn't 16 digits set card num error
                // message.
                if (cardText.getText().toString().length() != 16) {
                    cardText.setError(Html.
                            fromHtml("Please enter a valid card number"));
                    valid = false;
                }
                if(valid)
                {
                    // If the input is valid, create a new card object and pass
                    // it through to an API task
                    Card card = new Card();
                    card.setId(cardText.getText().toString());
                    card.setUserID(userID);
                    card.setCardExpiry(expDate);
                    card.setCardName(cardName.getText().toString());

                    AddUserCard task = new AddUserCard(service, card);
                    runner.run(task, taskScope);
                }

                break;
        }
    }

    /**
     * Add a user card to the API
     */
    private class AddUserCard extends ApiTask<Response> {

        private Card cardDetails;

        public AddUserCard(ServiceInterface service, Card cardDetails) {
            super(service);
            this.cardDetails = cardDetails;
        }

        /**
         * Do a query to the API to add the card object to the user's cards
         *
         * @param service A serviceInterface object to interact with the API
         * @return A confirmation of a response when the card is added
         */
        @Override
        public Response doQuery(ServiceInterface service) {
            return service.addUserCard(cardDetails);
        }

        /**
         * On done, call a method to close the page and print out a
         * confirmation of the card being added
         */
        @Override
        public void done(Response cardAdded) { finishedAddingCard(); }

        /**
         * If there is an API call error, print the respective details to a
         * toast
         */
        @Override
        public void failed(Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Add card error: invalid card details provided",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Method that shows a toast saying the card is added and then finishes
     * the current activity, notifying the checkout card fragment it has added
     * a card
     */
    public void finishedAddingCard() {
        Toast.makeText(getApplicationContext(),
                "Added card details",
                Toast.LENGTH_LONG)
                .show();
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finishActivity(REQUEST_ADDCARD);
        finish();
    }
}
