package com.dolphin.thegigisup.test;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import com.dolphin.thegigisup.MainActivity;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.fragments.FeaturedFragment;

/**
 * Created by Gurpreet Paul - 13/02/2015.
 */
public class MainActivityTest extends
        ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity mainActivity;
    private Fragment featuredFrag;
    private TextView tvHot;
    private TextView tvUpcoming;

    public MainActivityTest() {
        super(MainActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
        featuredFrag = startFragment(new FeaturedFragment());
        tvHot = (TextView) mainActivity
                        .findViewById(R.id.fea_tv_hot);
        tvUpcoming = (TextView) mainActivity
                .findViewById(R.id.fea_tv_upcoming);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

    }

    public void testPreconditions() {
        assertNotNull("mainActivity is null", mainActivity);
        assertNotNull("featuredFragment is null", featuredFrag);

    }

    public void testHotTitle() {
        final String expected = "Hot".toLowerCase();
        final String actual = tvHot.getText().toString().toLowerCase();
        assertEquals(expected, actual);
    }

    public void testUpcomingTitle() {
        final String expected = "Upcoming".toLowerCase();
        final String actual = tvUpcoming.getText().toString().toLowerCase();
        assertEquals(expected, actual);
    }

    private Fragment startFragment(Fragment fragment) {
        FragmentTransaction transaction = mainActivity
                .getFragmentManager()
                .beginTransaction();

        transaction.add(fragment, "ff");
        transaction.commit();
        getInstrumentation().waitForIdleSync();

        Fragment frag = mainActivity
                .getFragmentManager()
                .findFragmentByTag("ff");

        return frag;
    }
}
