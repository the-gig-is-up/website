package com.dolphin.thegigisup.activitys;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.dolphin.thegigisup.*;
import com.dolphin.thegigisup.api.ApiTask;
import com.dolphin.thegigisup.api.Runner;
import com.dolphin.thegigisup.api.ServiceFactory;
import com.dolphin.thegigisup.api.ServiceInterface;
import com.dolphin.thegigisup.models.LoginConfirm;
import com.dolphin.thegigisup.models.LoginDetails;
import com.dolphin.thegigisup.models.User;

import java.util.concurrent.Executors;

/**
 * Login activity page that allows the user to log in to their account
 */
public class LoginActivity extends ActionBarActivity
        implements View.OnClickListener {

    // Initialise the necessary variables
    private LoginDetails userLogin;
    private final Runner.Scope taskScope = new Runner.Scope();
    private Runner runner;
    private ServiceInterface service;

    private EditText username;
    private EditText password;

    public static final String USERNAME = "Username";
    public static final String TOKEN = "Token";
    public static final String EMAIL = "Email";
    public static final String USERID = "UserID";

    public static final int REQUEST_LOGIN = 1;

    @Override
    public void onDestroy() {
        super.onDestroy();
        taskScope.cancelAll();
    }

    /**
     * On creation of the activity, find the necessary views
     *
     * @param savedInstanceState A savedInstanceState is used if it exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //Get the username edittext object
        username = (EditText) findViewById(R.id.log_et_username);

        // Find password edittext object and set the transformation to asterisks
        password = (EditText) findViewById(R.id.log_et_password);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(
                                    new AsteriskPasswordTransformationMethod());

        runner = new Runner(Executors.newCachedThreadPool(),
                new Handler());
        service = ServiceFactory.createInstance();

    }

    /**
     * On click method is used to allow the user to log in, the user to go to
     * the sign up page or the user to reset their password
     *
     * @param v The clicked view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.log_tv_forgot):
                // Show toast message to indicate an email should be sent
                Toast.makeText(getApplicationContext(),
                        "Reset password",
                        Toast.LENGTH_SHORT).show();
                break;
            case (R.id.log_tv_signup):
                Intent intent =
                        new Intent(getApplicationContext(),
                                    SignUpActivity.class);
                startActivityForResult(intent, SignUpActivity.REQUEST_SIGNUP);
                break;
            case (R.id.log_bn_login):
                preLogin();
                break;
            default:
                break;
        }
    }

    /**
     * On activity result, gets the information if the user has signed up from
     * the log in page and passes that information back to the MainActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        // If the activity that finished was the
        // Login activity
        if (requestCode == SignUpActivity.REQUEST_SIGNUP) {
            // If the login activity returned a login
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Intent i = new Intent();

                    String accessToken = extras.getString(SignUpActivity.TOKEN);
                    String userEmail = extras.getString(SignUpActivity.EMAIL);
                    String userName = extras.getString(SignUpActivity.USERNAME);
                    int userID = extras.getInt(SignUpActivity.USERID);

                    i.putExtra(TOKEN, accessToken);
                    i.putExtra(EMAIL, userEmail);
                    i.putExtra(USERNAME, userName);
                    i.putExtra(USERID, userID);

                    setResult(RESULT_OK, i);
                    finishActivity(REQUEST_LOGIN);
                    finish();
                }
            }
        }
    }

    /**
     * Pre login method does validation checks on the login information and
     * then logs in the user using the API
     */
    private void preLogin() {
        // If both fields have text in them
        if ((!password.getText().toString().matches("")) &&
                (!username.getText().toString().matches(""))) {

            //Create a new logindetails object and add email/username and the
            // password to it
            userLogin = new LoginDetails();

            if (isValidEmail(username.getText().toString())) {
                userLogin.setEmail(username.getText().toString());
            }
            if (!isValidEmail(username.getText().toString())) {
                userLogin.setUsername(username.getText().toString());
            }

            userLogin.setPassword(password.getText().toString());

            //Run a user login task to log the user in

            UserLogin task = new UserLogin(service);
            runner.run(task, taskScope);

        } else {
            // Show a toast message if username or password isn't entered
            Toast.makeText(getApplicationContext(),
                    "Please fill in all details",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Class to check if inputted email is valid
     */
    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Define password transformation to asterisks
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
     * User login uses the API to login a user with their details
     */
    private class UserLogin extends ApiTask<LoginConfirm> {

        public UserLogin(ServiceInterface service) {super(service);}

        /**
         * Query the API and log in the user with the given details
         *
         * @param service A serviceInterface object to connect with the API
         * @return A login confirmation object
         */
        @Override
        public LoginConfirm doQuery(ServiceInterface service) {
            return service.loginUser(userLogin);
        }

        /**
         * On the end of the API call, show the login is confirmed
         *
         * @param loggedIn A login confirmation object from the API call
         */
        @Override
        public void done(LoginConfirm loggedIn) {
                                            showLoginConfirmation(loggedIn); }

        /**
         * If the API call fails, show a toast with the relevant error
         * information
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
     * Show login confirmation puts the login information into an intent
     * and ends the login activity, passing it back to the MainActivity
     *
     * @param loginConfirm The login confirmation object from the API call
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

        setResult(RESULT_OK, i);
        finishActivity(REQUEST_LOGIN);
        finish();
    }
}
