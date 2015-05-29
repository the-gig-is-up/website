package com.dolphin.thegigisup.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.dolphin.thegigisup.*;
import com.dolphin.thegigisup.activitys.CheckoutActivity;
import com.dolphin.thegigisup.adapters.SeatingAdapter;
import com.dolphin.thegigisup.api.ServiceFactory;
import com.dolphin.thegigisup.helpers.GridViewScrollable;
import com.dolphin.thegigisup.models.*;
import rx.*;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import java.util.*;

/**
 * A fragment that displays a seating plan, from which the user can select
 * their chosen seats
 *
 * @author Team Dolphin 07/04/15.
 */
public class CheckoutSeatFragment extends Fragment
                implements View.OnClickListener{

    // Initialise variable to display and keep track of selected seats
    private SeatingAdapter seatsAdapter;
    private Map<String, Integer> seatTypes;
    private HashMap<Integer, Boolean> seatsSelected = new HashMap<>();
    private HashMap<String, String> seatIDs = new HashMap<>();

    private ArrayList<String> seatTypeList;
    private Spinner seatTypeSpinner;

    // Initialise strings used in the select area spinner
    private final String    STANDING = "Standing",
                            UPPER_CIRCLE = "Upper circle",
                            CENTER = "Center",
                            STALLS = "Stalls",
                            VIP = "VIP";

    // Set maxtickets value
    private static final int MAX_TICKETS = 9;

    public int offset = 0;

    private CheckoutActivity checkoutActivity;
    private Observable<ArrayList<Sitting>> takenSeatsObservable;
    private Subscription seatsSub;
    private Event event;

    /**
     * On create, initialise API variables and create data to use for the
     * seat area spinner
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        seatTypes = new HashMap<>();
        seatTypes.put(STANDING, 1);
        seatTypes.put(CENTER, 2);
        seatTypes.put(UPPER_CIRCLE, 3);
        seatTypes.put(STALLS, 4);
        seatTypes.put(VIP, 5);

        // Use the hashmap's keys as a data source
        seatTypeList =
                new ArrayList<>();
        seatTypeList.add(STANDING);
        seatTypeList.add(CENTER);
        seatTypeList.add(UPPER_CIRCLE);
        seatTypeList.add(STALLS);
        seatTypeList.add(VIP);
    }

    /**
     * Override the on attach method to find the activity attached to the
     * fragment
     *
     * @param activity The attached activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        checkoutActivity = (CheckoutActivity) activity;
    }

    /**
     * On creation, inflate the seating view and initialise the appropriate
     * variables to display
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.individual_seats_layout,
                                     container, false);

        // Get the current event from the parent activity
        event = getActivity()
                .getIntent()
                .getExtras()
                .getParcelable("com.dolphin.thegigisup.Event");

        // Initialise things
        GridViewScrollable gridView =
                (GridViewScrollable) view.findViewById(R.id.seats_table);

        seatTypeSpinner =
                (Spinner) view.findViewById(R.id.ind_sp_seat_selection);
        LinkedHashMap<Integer, Seat> seats = new LinkedHashMap<>();

        // Create seating adapter
        seatsAdapter =
                new SeatingAdapter(getActivity(),
                        R.layout.seat_grid_item,
                        seats);

        gridView.setAdapter(seatsAdapter);
        getSeats(event);

        gridView.setOnItemClickListener(new GridViewClick());


        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<>(getActivity(),
                        R.layout.spinner_dropdown,
                        seatTypeList);

        seatTypeSpinner.setAdapter(dataAdapter);

        seatTypeSpinner.setOnItemSelectedListener(new SeatSelectedListener());

        Button clearButton = (Button)view.findViewById(R.id.seat_bn_clear);
        clearButton.setOnClickListener(this);

        return view;
    }

    /**
     * Get the seat type from the current selected spinner item
     *
     * @return The correct integer representing the seat type
     */
    private int getTypeIDFromSpinner() {
        String selected = String.valueOf(seatTypeSpinner.getSelectedItem());
        // Use the hashmap to find the right ID
        if (selected != null) {
            Integer index = seatTypes.get(selected);
            if (index != null) {
                return index;
            }
        }
        return 1;
    }

    /**
     * Get all the seats for the current event from the API
     *
     * @param event The event that the user wants to book seats for
     */
    private void getSeats(Event event) {
        int typeID = getTypeIDFromSpinner();
        Observable<ArrayList<Seat>> seatsObservable = ServiceFactory
                .createInstance()
                .getSeatsAtVenue(event.getVenue().getId(), typeID)
                .observeOn(AndroidSchedulers.mainThread());

        takenSeatsObservable = ServiceFactory
            .createInstance()
            .getTakenSeats(event.getId())
            .observeOn(AndroidSchedulers.mainThread());

        seatsSub = seatsObservable
                .doOnCompleted(() -> takenSeatsObservable
                        .subscribe(sittings -> setTakenSeats(sittings)))
                .subscribe(this::setSeats);
    }


    @Override
    public void onDestroy() {
        unsub();
        super.onDestroy();
    }

    /**
     * If the user clears the seat selection, clear all the seat's selected
     * variables and clear from the adapter and parent activity
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.seat_bn_clear):
                // Clear the selected seats in the adapter and clear the
                // variables keeping track of selected seats
                seatsAdapter.clearSelectedSeats();
                seatsSelected.clear();
                seatIDs.clear();
                // Notify the parent activity
                checkoutActivity.setSeatsSelected(seatsSelected);
                checkoutActivity.setSeatIDs(seatIDs);
            default:
                break;
        }
    }

    /**
     * Allows for unsubscribing
     */
    private void unsub() {
        if (seatsSub != null)
            seatsSub.unsubscribe();
    }

    /**
     * Set a list of all the seats
     * @param seats List of seats to give to the Seats adapter
     */
    private void setSeats(ArrayList<Seat> seats) {
        if (seats == null || seats.size() < 1) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Unable to load seats.",
                    Toast.LENGTH_LONG)
                    .show();

        } else {
            // Add real seats and check if they've
            // been selected
            for (int i = 0; i < seats.size(); i++) {
                Seat seat = seats.get(i);
                if ((seatsSelected.get(seat.getId()) != null) &&
                        seatsSelected.get(seat.getId())) {
                    seat.setSelected(true);
                }
                seats.set(i, seat);
            }
            seatsAdapter.addSeats(seats);
        }
    }

    /**
     * After the venue's seat has been set,
     * we can set all the taken seats with this method
     * @param seatingList A list of all the seated positions
     */
    private void setTakenSeats(ArrayList<Sitting> seatingList) {
        if (seatingList != null) {
            seatsAdapter.addTakenSeats(seatingList);
        }
    }


    /**
     * Class to handle clicks on the seats view
     */
    private final class GridViewClick implements
            AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {
            Seat seat = seatsAdapter.getItem(position);
            if (seat.isSelected()) {
                selectSeat(seat, false);
            }
            else if (seat.isTaken()) {
                showMessage("Seat already taken. Please select another.");

            }
            // Perform the check to max tickets before
            // allowing user to select a seat
            else if (seatsSelected.size() > MAX_TICKETS) {
                showMessage("Max tickets selected.");
            }
            else if (!seat.isSelected()) {
                selectSeat(seat, true);
            }

        }

        /**
         * Show a message as a toast
         * @param message Message to show
         */
        private void showMessage(String message) {
            Toast.makeText(getActivity(),
                            message,
                            Toast.LENGTH_SHORT)
                    .show();
        }

        /**
         * Fired when a seat is selected
         * @param seat The seat that was selected
         * @param selectSeat True or false if selected
         */
        private void selectSeat(Seat seat, boolean selectSeat) {
            seat.setSelected(selectSeat);
            if (selectSeat) {
                seatsSelected.put(seat.getId(), true);
                seatIDs.put(seat.toString() + "," + seat.getTypeID(),
                        seatTypeSpinner.getSelectedItem().toString());
            }
            else {
                seatsSelected.remove(seat.getId());
                seatIDs.remove(seat.toString() + "," + seat.getTypeID());
            }
            seatsAdapter.notifyDataSetChanged();
            checkoutActivity.setSeatsSelected(seatsSelected);
            checkoutActivity.setSeatIDs(seatIDs);
        }
    }

    private final class SeatSelectedListener
            implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent,
                                   View view,
                                   int position,
                                   long id) {
            getSeats(event);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
