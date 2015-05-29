package com.dolphin.thegigisup.test;


import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.activitys.SignUpActivity;

public class SignUpActivityTest  extends
        ActivityInstrumentationTestCase2<SignUpActivity> {

    SignUpActivity signUpActivity;

    private TextView username;
    private TextView email;
    private TextView password;
    private TextView location;
    private Button signUp;

    public SignUpActivityTest() {super(SignUpActivity.class);}

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        signUpActivity = getActivity();

        username = (TextView) signUpActivity.findViewById(R.id.sig_et_username);
        email = (TextView) signUpActivity.findViewById(R.id.sig_et_email);
        password = (TextView) signUpActivity.findViewById(R.id.sig_et_password);
        location = (TextView) signUpActivity.findViewById(R.id.sig_et_location);
        signUp = (Button) signUpActivity.findViewById(R.id.sig_bn_signup);

    }

    public void testPreconditions() {
        assertNotNull("signUpActivity is null", signUpActivity);
        assertNotNull("Username null", username);
        assertNotNull("E-mail null", email);
        assertNotNull("Password null", password);
        assertNotNull("Location null", location);
        assertNotNull("SignUp Button null", signUp);
    }
}
