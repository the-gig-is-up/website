package com.dolphin.thegigisup.test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.activitys.AddCardActivity;
import com.dolphin.thegigisup.activitys.CheckoutActivity;

public class AddCardActivityTest extends
        ActivityInstrumentationTestCase2<AddCardActivity> {

    AddCardActivity addCardActivity;

    private TextView name,
            number,
            date,
            security,
            address;
    private Button add;
    private Spinner month, year;

    public AddCardActivityTest() {super(AddCardActivity.class);}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addCardActivity = getActivity();
        getViews();
    }

    public void testPreconditions() {
        assertNotNull("Add Card Activity is null", addCardActivity);
        assertNotNull("Name TV null", name);
        assertNotNull("Number TV null", number);
        assertNotNull("Date TV null", date);
        assertNotNull("Security TV null", security);
        assertNotNull("Address TV null", address);
        assertNotNull("Add Button null", add);
        assertNotNull("Month Spinner null", month);
        assertNotNull("Year Spinner null", year);
    }

    public void testEmpty() {
        // Set up an ActivityMonitor
        Instrumentation.ActivityMonitor receiverActivityMonitor =
                getInstrumentation().addMonitor(CheckoutActivity.class.getName(),
                        null, false);

        //Test that clicking the login button does not close the current
        //activity.
        TouchUtils.clickView(this, add);

        CheckoutActivity receiverActivity = (CheckoutActivity)
                receiverActivityMonitor.waitForActivityWithTimeout(5);

        assertNull("CheckoutActivity is not null", receiverActivity);

        getInstrumentation().removeMonitor(receiverActivityMonitor);
    }

    public void testSpinnerContents() {
        assertNotSame(0, month.getChildCount());
        assertNotSame(0, year.getChildCount());
    }

    private void getViews() {

        name = (TextView) addCardActivity.
                findViewById(R.id.card_tv_name);
        number = (TextView) addCardActivity.
                findViewById(R.id.card_tv_number);
        date = (TextView) addCardActivity.
                findViewById(R.id.card_tv_exp);
        security = (TextView) addCardActivity.
                findViewById(R.id.card_tv_sec);
        address = (TextView) addCardActivity.
                findViewById(R.id.card_tv_addr);

        add = (Button) addCardActivity.
                findViewById(R.id.card_but_add);

        month = (Spinner) addCardActivity.
                findViewById(R.id.card_sp_month);
        year = (Spinner) addCardActivity.
                findViewById(R.id.card_sp_year);
    }
}
