package com.dolphin.thegigisup.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.dolphin.thegigisup.*;
import com.dolphin.thegigisup.api.ApiTask;
import com.dolphin.thegigisup.api.Runner;
import com.dolphin.thegigisup.api.ServiceFactory;
import com.dolphin.thegigisup.api.ServiceInterface;
import com.dolphin.thegigisup.models.LoginConfirm;
import com.dolphin.thegigisup.models.LoginDetails;
import com.dolphin.thegigisup.models.User;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import java.util.concurrent.Executors;

/**
 * Sign up page to allow the user to create an account
 *
 * @author Team Dolphin 26/02/15.
 */
public class SignUpActivity extends ActionBarActivity {

    private User user;
    private Runner runner;
    private ServiceInterface service;
    private final Runner.Scope taskScope = new Runner.Scope();
    private EditText emailText, usernameText, passwordText;
    private LoginDetails loginDetails;

    public static final String USERNAME = "Username";
    public static final String TOKEN = "Token";
    public static final String EMAIL = "Email";
    public static final String USERID = "UserID";
    public static final int REQUEST_SIGNUP = 2;

    /**
     * On creation, find and set the listeners on the appropriate views
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Add the back button to the top action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Find the edittext objects for getting information from the form
        emailText = (EditText) findViewById(R.id.sig_et_email);
        usernameText = (EditText) findViewById(R.id.sig_et_username);

        //Set default transformation of asterisks
        passwordText = (EditText) findViewById(R.id.sig_et_password);
        passwordText.setTransformationMethod(
                                    new AsteriskPasswordTransformationMethod());

        //Ignore setting location until next sprint
        final EditText locationText = (EditText) findViewById(
                                                        R.id.sig_et_location);
        locationText.setText("Default location");

        // Make the password hidden by asterisks if the box isn't checked,
        // and show the password normally if it is
        final CheckBox checkPassword = (CheckBox) findViewById(
                                                    R.id.sig_cb_checkpassword);
        checkPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = checkPassword.isChecked();
                if (isChecked) {
                    passwordText.setTransformationMethod(null);
                }
                if (!isChecked) {
                    passwordText.setTransformationMethod(
                                    new AsteriskPasswordTransformationMethod());
                }
            }
        });

        // Find the sign up button and do checks on whether the form is
        // complete or not
        Button signUpButton = (Button) findViewById(R.id.sig_bn_signup);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all the fields are filled in and email address is
                // valid
                if (!emailText.getText().toString().matches("") &&
                    !passwordText.getText().toString().matches("") &&
                    !usernameText.getText().toString().matches("") &&
                    isValidEmail(emailText.getText().toString())) {

                    // Create thread pool runner
                    runner = new Runner(Executors.newCachedThreadPool(),
                            new Handler());

                    service = ServiceFactory.createInstance();

                    user = new User();
                    user.setEmail(emailText.getText().toString());
                    user.setUsername(usernameText.getText().toString());
                    user.setPassword(passwordText.getText().toString());
                    user.setIsAdmin(0);
                    user.setIsArtist(0);
                    //Sign in the user using a runner task
                    UserSignUp task = new UserSignUp(service);
                    runner.run(task, taskScope);

                }
                // Show a message if email is not valid
                else if (!isValidEmail(emailText.getText().toString())) {
                    Toast.makeText(getApplicationContext(),
                                   "Invalid email entered",
                                   Toast.LENGTH_SHORT).show();
                }
                // Else tell user to fill in all the fields
                else {
                    Toast.makeText(getApplicationContext(),
                                   "Please complete all details",
                                   Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Make sure the activity finishes on a return action
     *
     * @param item The menu item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back icon in action bar clicked; goto previous activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Class to check if a character string is a valid email address
     *
     * @param target A string representing an email address
     * @return True if the email is valid, false if not
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Password transformation so the password is represented by asterisks
     */
    public class AsteriskPasswordTransformationMethod
                                        extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;
            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }
            public char charAt(int index) {
                return '*'; // This is the important part
            }
            public int length() {
                return mSource.length(); // Return default
            }
            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    }

    /**
     * User sign up sends a new User object to the API to create that new
     * user
     */
    private class UserSignUp extends ApiTask<Response> {
        public UserSignUp(ServiceInterface service) {
            super(service);
        }

        /**
         * Send the given user object to the API
         *
         * @param service A serviceInterface object to interact with the API
         * @return A confirmation response of the user sign up
         */
        @Override
        public Response doQuery(ServiceInterface service) {
                return service.signUpUser(user);
        }

        /**
         * If the user signs up successfully, run the signedUp method
         *
         * @param signedUp A confirmation of the sign up
         */
        @Override
        public void done(Response signedUp) { signedUp();}

        /**
         * If the API call fails, use the error response to find why the
         * signing up failed and print the relevant error information
         */
        @Override
        public void failed(Exception e) {

            RetrofitError error = (RetrofitError) e;
            Response response = error.getResponse();
            if (response != null && response.getStatus() == 422) {
                String json =
                            new String(((TypedByteArray)error.getResponse().
                                                     getBody()).getBytes());
                String[] splitString = json.split(":");
                String[] errorName = splitString[5].split("`");
                String errorType = errorName[1];
                //Log.e("ERROR TYPE", errorType);
                if (errorType.equals("email")) {
                    Toast.makeText(getApplicationContext(),
                            "Email already exists",
                            Toast.LENGTH_LONG)
                            .show();
                }
                if (errorType.equals("username")) {
                    Toast.makeText(getApplicationContext(),
                            "Username already exists",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),
                        e.toString(),
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    /**
     * Confirm the user has signed up with a toast and then log in the user
     */
    public void signedUp() {
        Toast.makeText(getApplicationContext(),
                usernameText.getText().toString() + " has been signed up",
                Toast.LENGTH_LONG)
                .show();

        // If user has signed up successfully, sign them in with the correct
        // login details
        loginDetails = new LoginDetails();
        loginDetails.setEmail(user.getEmail());
        loginDetails.setPassword(user.getPassword());
        UserLogin task = new UserLogin(service);
        runner.run(task, taskScope);
    }

    /**
     * Log in the user that has just signed up
     */
    private class UserLogin extends ApiTask<LoginConfirm> {
        public UserLogin(ServiceInterface service) {
            super(service);
        }

        /**
         * Log in the user given the signed up login details
         *
         * @param service A serviceInterface object used to interact with API
         * @return A login confirmation object
         */
        @Override
        public LoginConfirm doQuery(ServiceInterface service) {
            return service.loginUser(loginDetails);
        }

        /**
         * If the log in works successfully, show the user has logged in
         *
         * @param loggedIn A login confirmation
         */
        @Override
        public void done(LoginConfirm loggedIn) {
            showLoginConfirmation(loggedIn);
        }

        /**
         * If the login fails, print a toast with the relevant error information
         */
        @Override
        public void failed(Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Login error: invalid details provided",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * If the log in is successful, finish the sign up activity and return
     * to the main activity
     *
     * @param loginConfirm A login confirmation object
     */
    public void showLoginConfirmation(LoginConfirm loginConfirm) {

        Intent i = new Intent();
        User user = loginConfirm.getUser();

        String accessToken = loginConfirm.getId();
        String userEmail = user.getEmail();
        String userName = user.getUsername();
        int userID = user.getId();

        i.putExtra(TOKEN, accessToken);
        i.putExtra(EMAIL, userEmail);
        i.putExtra(USERNAME, userName);
        i.putExtra(USERID, userID);

        setResult(this.RESULT_OK, i);
        finishActivity(REQUEST_SIGNUP);
        finish();
    }
}
