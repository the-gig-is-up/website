package com.dolphin.thegigisup.test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.TextView;
import com.dolphin.thegigisup.MainActivity;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.activitys.LoginActivity;

public class LoginActivityTest extends
        ActivityInstrumentationTestCase2<LoginActivity> {

    LoginActivity loginActivity;

    private TextView username;
    private TextView password;
    private TextView signUp;
    private Button login;


    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        loginActivity = getActivity();

        signUp = (TextView) loginActivity.findViewById(R.id.log_tv_signup);
        login = (Button) loginActivity.findViewById(R.id.log_bn_login);
        username = (TextView) loginActivity.findViewById(R.id.log_et_username);
        password = (TextView) loginActivity.findViewById(R.id.log_et_password);

    }

    public void testPreconditions() {
        assertNotNull("loginActivity is null", loginActivity);
        assertNotNull("Username null", username);
        assertNotNull("Password null", password);
        assertNotNull("Sign up Text null", signUp);
        assertNotNull("Login Button null", login);
    }

    public void testIncorrectLogin() {

        // Set up an ActivityMonitor
        Instrumentation.ActivityMonitor receiverActivityMonitor =
                getInstrumentation().addMonitor(MainActivity.class.getName(),
                        null, false);

        //Test that clicking the login button does not close the current
        //activity.
        TouchUtils.clickView(this, login);

        MainActivity receiverActivity = (MainActivity)
                receiverActivityMonitor.waitForActivityWithTimeout(5);
        assertNull("MainActivity is not null", receiverActivity);
        getInstrumentation().removeMonitor(receiverActivityMonitor);
    }

}
