package com.dolphin.thegigisup.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import au.com.gridstone.grex.GRexPersister;
import au.com.gridstone.grex.converters.GsonConverter;
import com.dolphin.thegigisup.MainActivity;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.api.ServiceFactory;
import com.dolphin.thegigisup.adapters.BookingsAdapter;
import com.dolphin.thegigisup.helpers.CustomLinearLayoutManager;
import com.dolphin.thegigisup.helpers.DividerItemDecoration;
import com.dolphin.thegigisup.models.Booking;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import java.io.File;
import java.util.ArrayList;

/**
 * Fragment page to display the bookings for a particular user
 *
 * @author Team Dolphin 25/04/2015.
 */
public class BookingsFragment extends Fragment {

    private static final String BOOKING      = "Booking";
    private ArrayList<Booking>  bookingsList = new ArrayList<>();
    public int                  offset       = 0;

    private GRexPersister                   persister;
    private BookingsAdapter                 bookingsAdapter;
    private Observable<ArrayList<Booking>>  bookingObservable;
    private Subscription                    bookingSubscription;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = null;
        if (hasUser()) {
            v = inflater.inflate(R.layout.no_user, container, false);
        } else {
            v = inflater.inflate(R.layout.booking_frag, container, false);
            setBookingsAdapter(v);
            setApiRunners(savedInstanceState);
        }
        return v;
    }

    /**
     * Checks if there is a user
     * @return True if there is a user logged in
     * false if there is not
     */
    private boolean hasUser() {
        return MainActivity.getUserID() == -1;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasUser()) {
            setApiRunners(savedInstanceState);
        }
    }

    /**
     * If we have a valid user then load his/her bookings
     * @param savedInstanceState Saved state if valid
     */
    private void setApiRunners(Bundle savedInstanceState) {
        persister = new GRexPersister(
                new GsonConverter(),
                new File(getActivity().getCacheDir().getAbsolutePath()));

        if (savedInstanceState == null) {
            bookingObservable = ServiceFactory
                    .createInstance()
                    .getBookings(MainActivity.getUserID());
        }
        bookingSubscription =
                bookingObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(bookings -> persister.putList(BOOKING,
                            bookings,
                            Booking.class))
                    .onErrorResumeNext(throwable -> {
                        return persister.getList(BOOKING, Booking.class);
                    })
                    .subscribe(bookingsNew ->
                        bookingsAdapter
                            .setBookings((ArrayList<Booking>) bookingsNew));
    }




    /**
     * Sets the bookings adapter and attaches it to the relevant recycler view
     */
    private void setBookingsAdapter(View v) {
        // Add the appropriate views and adapters to the recycle view
        RecyclerView bookingsRecycler = (RecyclerView)
                v.findViewById(R.id.boo_rv_list_bookings);
        bookingsRecycler.addItemDecoration(new
                        DividerItemDecoration(getActivity(),
                        DividerItemDecoration.VERTICAL_LIST));

        // Attach to adapter
        bookingsRecycler.setItemAnimator(new DefaultItemAnimator());
        bookingsAdapter = new BookingsAdapter(bookingsList,
                                                R.layout.booking_item);
        bookingsRecycler.setAdapter(bookingsAdapter);

        // Attach to layout manager
        CustomLinearLayoutManager llm =
                new CustomLinearLayoutManager(getActivity(),
                                            LinearLayoutManager.VERTICAL,
                                            false);
        bookingsRecycler.setLayoutManager(llm);
    }

    @Override
    public void onDestroy() {
        if (bookingSubscription != null) {
            bookingSubscription.unsubscribe();
        }
        super.onDestroy();
    }

}
