package com.dolphin.thegigisup.activitys;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.dolphin.thegigisup.models.Card;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.fragments.CheckoutCardFragment;
import com.dolphin.thegigisup.fragments.CheckoutConfirmFragment;
import com.dolphin.thegigisup.fragments.CheckoutSeatFragment;

import java.util.HashMap;

/**
 * Acts as an activity to manage and display checkout page fragments within a
 * sliding tab view
 *
 * @author Team Dolphin
 */
public class CheckoutActivity extends ActionBarActivity
            implements View.OnClickListener{

    // Initialise fragment variables
    FragmentTransaction fragmentTransaction =
                                     getFragmentManager().beginTransaction();
    Fragment currentFragment;
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;

    // Initialise shared preferences variables (for storing user ID)
    private SharedPreferences sharedPreferences;
    public static final String MYPREFERENCES = "MyPreferences" ;
    public static final String USERID = "UserID";

    // Initialise variables used to store selected user information
    private HashMap<Integer, Boolean> seatsSelected = new HashMap<>();
    private HashMap<String, String> seatIDs = new HashMap<>();
    private Card selectedCard;

    /**
     * Change fragment method used to change the current fragment in the
     * activity
     *
     * @param fragment Fragment you want to change to
     * @param bundle Bundled information with the new fragment
     */
    private void changeFragment(Fragment fragment, Bundle bundle) {
        // Switch to the new fragment
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.payment_container, fragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    /**
     * Create the checkout activity and initialise views and items within the
     * activity
     *
     * @param savedInstanceState - Uses the previous saved instance of this
     * activity if needed
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_container);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buy Now");

        // Find the viewpager and set it's variables
        viewPager = (ViewPager) findViewById(R.id.PAGER);
        MyPagerAdapter adapter = new MyPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        final int pageMargin =
        (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                                       getResources().getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);

        // Set the current first fragment of the checkoutActivity
        changeFragment(new CheckoutSeatFragment(), new Bundle());

        // Set the viewpagers page change listener to call an onResume method
        // every time the confirmation page is
        // visited
        ViewPager.OnPageChangeListener pageChangeListener =
                                        new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                Fragment fragment =
                 ((MyPagerAdapter)viewPager.getAdapter()).getFragment(position);
                // If the viewpager is showing the checkoutconfirmfragment,
                // force the viewpager to call onResume method
                if (position ==2 && fragment != null)
                {
                    fragment.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        };
        viewPager.setOnPageChangeListener(pageChangeListener);

        // Get the tabs above the viewpager (linked to the fragment pages) and
        // link it to the viewpager
        tabs = (PagerSlidingTabStrip) findViewById(R.id.TABS);
        tabs.setShouldExpand(true);
        tabs.setViewPager(viewPager);
        tabs.setIndicatorColor(getResources().getColor(R.color.color_primary));
        tabs.setTextColor(getResources().getColor(R.color.color_primary));
        tabs.setOnPageChangeListener(pageChangeListener);
    }

    /**
     * Set the current user selected seats for use in later API posts
     *
     * @param seatsSelected A list of seat IDs of selected seats
     */
    public void setSeatsSelected(HashMap<Integer, Boolean> seatsSelected) {
        this.seatsSelected = seatsSelected;
    }

    /**
     * Get the current seat IDs selected
     *
     * @return Selected seat IDs
     */
    public HashMap<Integer, Boolean> getSeatsSelected() {
        return this.seatsSelected;
    }

    /**
     * Set the seat row, columns and seat area selected for use in printing
     * seat information
     *
     * @param seatIDs A list of seat row/column with a seating area string
     */
    public void setSeatIDs(HashMap<String, String> seatIDs) {
        this.seatIDs = seatIDs;
    }

    /**
     * Get the currently selected seat information for printing
     *
     * @return A list of row/columns with a seating area string for each
     * selected seat
     */
    public HashMap<String, String> getSeatIDs() {return this.seatIDs;}

    /**
     * Set the card the user has currently selected
     *
     * @param selectedCard A user selected card object
     */
    public void setSelectedCard(Card selectedCard) {
        this.selectedCard = selectedCard;
    }

    /**
     * Get the currently selected card
     *
     * @return The card object the user has selected
     */
    public Card getSelectedCard() {return this.selectedCard;}

    /**
     * Override method to manage clicking back on the checkout activity
     *
     * @param item Selected menu item
     * @return True
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // If back is selected, close the checkout activity
                FragmentManager fm = this.getFragmentManager();
                int backStackEntryCount =
                                getFragmentManager().getBackStackEntryCount();
                if(backStackEntryCount >= 1){
                    this.finish();
                }
        }
        return true;
    }

    /**
     * Override onClick method to manage when users click buttons on the
     * checkout pages
     *
     * @param v Clicked view
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            // Move the viewpager to the next page if the user selects a
            // 'next page' button
            case R.id.seat_bn_nextpage:
                viewPager.setCurrentItem(1, true);
                break;
            case R.id.card_bn_nextpage:
                viewPager.setCurrentItem(2, true);
                break;
            default:
                break;
        }
    }

    /**
     * Custom FragmentPagerAdapter that manages which pages to create in the
     * viewpager
     */
    public class MyPagerAdapter extends FragmentPagerAdapter {
        private HashMap<Integer, String> fragmentTags = new HashMap<>();
        private final String[] TITLES = { "Seats", "Payment", "Confirm" };

        /**
         * Default FragmentPagerAdapter constructor
         *
         * @param fm
         */
        public MyPagerAdapter(FragmentManager fm) {super(fm);}

        /**
         * Get the page title with respect to the selected position
         *
         * @param position
         * @return The correct title from the list of page titles
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        /**
         * Get the amount of pages in the viewpager
         * @return The number of titles given in the adapter
         */
        @Override
        public int getCount() {
            return TITLES.length;
        }

        /**
         * Get the current item in the viewpager according to position. In each
         * position instantiate the appropriate fragment page
         *
         * @param position The current position of the viewpager
         * @return The correct selected fragment
         */
        @Override
        public Fragment getItem(int position) {
            Fragment fragment =null;
            switch (position) {
                case 0:
                    fragment = Fragment.instantiate(getApplicationContext(),
                                        CheckoutSeatFragment.class.getName());
                    break;
                case 1:
                    fragment = Fragment.instantiate(getApplicationContext(),
                                        CheckoutCardFragment.class.getName());
                    break;
                case 2:
                    fragment = Fragment.instantiate(getApplicationContext(),
                                       CheckoutConfirmFragment.class.getName());
                    break;
            }
            return fragment;
        }

        /**
         * Override the instantiateItem so the adapter adds a tag when a new
         * fragment is instantiated
         *
         * @param container The viewpager container itself
         * @param position Position in the container
         * @return An instantiated object with a tag
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            if (object instanceof Fragment) {
                Fragment f = (Fragment) object;
                String tag = f.getTag();
                fragmentTags.put(position, tag);
            }
            return object;
        }

        /**
         * Gets the current fragment displayed in the adapter so the viewpager
         * can access the fragments directly
         *
         * @param position
         * @return The fragment in the current position
         */
        public Fragment getFragment(int position) {
            String tag = fragmentTags.get(position);
            if (tag == null) {
                return null;
            }
            return getFragmentManager().findFragmentByTag(tag);
        }
    }
}