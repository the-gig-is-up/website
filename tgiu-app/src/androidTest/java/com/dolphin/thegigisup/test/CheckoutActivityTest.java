package com.dolphin.thegigisup.test;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.activitys.CheckoutActivity;
import com.dolphin.thegigisup.fragments.CheckoutConfirmFragment;
import com.dolphin.thegigisup.fragments.CheckoutSeatFragment;
import com.dolphin.thegigisup.helpers.GridViewScrollable;
import com.dolphin.thegigisup.models.Artist;
import com.dolphin.thegigisup.models.Event;
import com.dolphin.thegigisup.models.Venue;

import java.util.ArrayList;
import java.util.List;


public class CheckoutActivityTest extends
        ActivityInstrumentationTestCase2<CheckoutActivity> {

    CheckoutActivity checkoutActivity;
    private ViewPager viewPager;
    private Event event;

    public CheckoutActivityTest() {super(CheckoutActivity.class);}

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Create a mock event object for Intent.
        event = createEvent();

        // Setup mock intent for Checkout so it has Event Object.
        Intent intent = new Intent();
        intent.putExtra("com.dolphin.thegigisup.Event", event);
        setActivityIntent(intent);

        checkoutActivity = getActivity();
        viewPager =(ViewPager) getActivity().findViewById(R.id.PAGER);
        startFragment(new CheckoutSeatFragment());


    }

    public void testPreconditions() {
        assertNotNull("Checkout Activity is null", checkoutActivity);
        assertNotNull("View pager null", viewPager);
    }

    private Fragment startFragment(Fragment fragment) {
        FragmentTransaction transaction = checkoutActivity
                .getFragmentManager()
                .beginTransaction();

        transaction.add(fragment, "ff");
        transaction.commit();
        getInstrumentation().waitForIdleSync();

        Fragment frag = checkoutActivity
                .getFragmentManager()
                .findFragmentByTag("ff");

        return frag;

    }

    public void testSeatFrag() {
        GridViewScrollable seats = (GridViewScrollable)
                checkoutActivity.findViewById(R.id.seats_table);

        Spinner seatTypes = (Spinner)
                checkoutActivity.findViewById(R.id.ind_sp_seat_selection);

        assertNotNull("Seat grid null", seats.getAdapter());
        assertNotNull("Spinner Empty",seatTypes.getAdapter() );
    }

    public void testCardFrag() {
        viewPager.setCurrentItem(1, true);

        Button add = (Button) checkoutActivity.
                findViewById(R.id.chec_bn_addcard);
        Button rm = (Button) checkoutActivity.
                findViewById(R.id.chec_bn_removecard);
        RecyclerView recyclerView = (RecyclerView) checkoutActivity.
                findViewById(R.id.card_rv_cardscroll);

        assertNotNull("No Recycler view present ", recyclerView);
        assertNotNull("No add button", add);
        assertNotNull("No add button", rm);
    }

    public void testCheckoutFrag() {
        startFragment(new CheckoutConfirmFragment());

        TextView eventDetails = (TextView) checkoutActivity.
                findViewById(R.id.conf_tv_eventdetails);
        TextView date = (TextView) checkoutActivity.
                findViewById(R.id.conf_tv_date);

        // Assert that the 2 TextViews get correct text from Event obj.
        assertEquals(eventDetails.getText(), "Test Event, Test Location");
        assertEquals(date.getText(), "18:00 on 04 Sep 15");
    }

    private Event createEvent() {

        Venue venue = new Venue();
        venue.setName("Test Venue");
        venue.setLocation("Test Location");
        venue.setId(1001);

        Artist artist = new Artist();
        artist.setName("Test Artist");
        List artists = new ArrayList<>();
        artists.add(artist);

        Event event = new Event();

        event.setArtists(artists);
        event.setVenue(venue);

        event.setId(0);
        event.setPrice("1");
        event.setEventName("Test Event");
        event.setDate("2015-09-04T00:00:00.000Z");
        event.setStartTime("18:00:00");
        event.setEndTime("00:00:00");

        return event;
    }




}
