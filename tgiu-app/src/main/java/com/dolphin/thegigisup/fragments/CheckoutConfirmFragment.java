package com.dolphin.thegigisup.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dolphin.thegigisup.*;
import com.dolphin.thegigisup.activitys.CheckoutActivity;
import com.dolphin.thegigisup.api.ApiTask;
import com.dolphin.thegigisup.api.Runner;
import com.dolphin.thegigisup.api.ServiceFactory;
import com.dolphin.thegigisup.api.ServiceInterface;
import com.dolphin.thegigisup.models.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import retrofit.client.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * A checkout confirmation fragment that displays the information the user
 * has selected
 *
 * @author Team Dolphin 03/03/2015.
 */
public class CheckoutConfirmFragment extends Fragment {

    // Initialise the variables used for API calls
    private ServiceInterface service;
    private Runner runner;
    private final Runner.Scope taskScope = new Runner.Scope();

    // Initialise variables used to display the information
    private String strStandardTickets;
    private TextView ticketsText, cardDetails, priceText;
    private LinearLayout ticketsLayout, ticketsPrice;
    private CheckoutActivity checkoutActivity;
    private double standingPrice, centerPrice, circlePrice, stallsPrice,
    vipPrice;

    // Initialise shared preferences used to retrieve the current logged in
    // user information
    private SharedPreferences sharedpreferences;
    public static final String MYPREFERENCES = "MyPreferences" ;
    public static final String USERID = "UserID";
    private int userID;

    // Initialise variables to store the current event and selected information
    private HashMap<Integer, Boolean> selectedSeats = new HashMap<>();
    private HashMap<String, String> seatIDs = new HashMap<>();
    private Card selectedCard;
    private Event event;

    public static final String CONFIRMED = "CONFIRMED";
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random rnd = new Random();

    /**
     * Used to return a random string used when a user makes a booking
     *
     * @param len Specify length of the random string
     * @return A random ticket reference string
     */
    String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for( int i = 0; i < len; i++ )
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    /**
     * On attaching to an activity, get the shared preferences and current
     * activity and initialise the API variables
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        checkoutActivity = (CheckoutActivity) activity;
        sharedpreferences = getActivity().getSharedPreferences(MYPREFERENCES,
                                                        Context.MODE_PRIVATE);

        runner = new Runner(Executors.newCachedThreadPool(),
                new Handler());
        ServiceFactory serviceFactory = new ServiceFactory();
        service = serviceFactory.createInstance();
    }

    /**
     * Create the fragment view and initialise the various widgets to display
     * on screen
     *
     * @return The created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                          ViewGroup container,
                          Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.confirm_frag, container, false);

        // Get the selected event and current user ID
        event = getActivity().getIntent().getExtras().getParcelable(
                                              "com.dolphin.thegigisup.Event");
        userID = sharedpreferences.getInt(USERID, 0);

        // Run an API call to get the price modifiers for the ticket types
        GetPriceModifiers task = new GetPriceModifiers(service);
        runner.run(task, taskScope);

        strStandardTickets = "";
        String strVipTickets = "";
        String strCost = "";

        // Get event details strings from passed event
        String strArtists = event.getEventName();
        String strLocation = event.getVenue().getLocation();
        DateTime dateTimeObj = event.getDate();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yy");

        // Set the appropriate text information for the event details from the
        // event object
        TextView eventDetails = (TextView) view.findViewById(
                                           R.id.conf_tv_eventdetails);
        eventDetails.setText(strArtists + ", " +strLocation);

        TextView dateText = (TextView) view.findViewById(R.id.conf_tv_date);
        if(event.getEndTime().equals("00:00")){
            dateText.setText(event.getStartTime() +
                    " on " +
                    fmt.print(dateTimeObj));
        } else {
            dateText.setText(event.getStartTime() +
                    " - " + event.getEndTime() +
                    " on " +
                    fmt.print(dateTimeObj));
        }

        ticketsLayout = (LinearLayout) view.findViewById(
                                            R.id.cout_ll_ticketstext);
        ticketsText = (TextView) view.findViewById(R.id.conf_tv_tickettypes);
        ticketsText.setText("Standard tickets: "+
                        checkoutActivity.getSeatsSelected()+"\nVIP Tickets: ");

        ticketsPrice = (LinearLayout) view.findViewById(
                                           R.id.cout_ll_ticketprice);
        priceText = (TextView) view.findViewById(R.id.conf_tv_price);
        priceText.setText(strCost);

        cardDetails = (TextView) view.findViewById(R.id.conf_tv_carddetails);
        cardDetails.setText("");

        // If the user has selected seats and a payment option, make a new
        // booking object and post it to the API
        Button confirmButton = (Button) view.findViewById(R.id.conf_bn_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCard != null && !selectedSeats.isEmpty()) {
                    Booking booking = new Booking();
                    booking.setUserID(userID);
                    booking.setEventID(event.getId());
                    booking.setCardID(selectedCard.getId());

                    PostBooking task = new PostBooking(service, booking);
                    runner.run(task, taskScope);
                }
                // Print toast messages if the user has not selected the
                // appropriate information
                else if (selectedCard == null && selectedSeats.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please select seats from the seating plan and a "+
                            "payment method",
                            Toast.LENGTH_LONG)
                            .show();
                }
                else if (selectedCard == null) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please select a payment method",
                            Toast.LENGTH_LONG)
                            .show();
                }
                else if (selectedSeats.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please select your seats from the seating plan",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        return view;
    }

    /**
     * On resuming the fragment, get the current selected seat and card
     * information from the parent activity and display the information on the
     * page
     */
    @Override
    public void onResume()
    {
        super.onResume();
        selectedSeats = checkoutActivity.getSeatsSelected();
        selectedCard = checkoutActivity.getSelectedCard();
        seatIDs = checkoutActivity.getSeatIDs();

        // Get the textview string for the chosen user tickets and their prices
        String seatsInfo = getSeatInfoString(seatIDs);
        String priceInfo = getPriceInfoString(seatIDs);
        ticketsText.setText(seatsInfo);
        priceText.setText(priceInfo);

        if (selectedCard != null) {
            cardDetails.setText("XXXX-XXXX-XXXX-"+selectedCard.getId().substring(12));
        }
    }

    /**
     * Using the seating list, generate a string used to display the
     * information on the confirmation page
     *
     * @param seatList A list of chosen seats from the user
     * @return A string displaying the pricing information for the chosen seats
     */
    public String getPriceInfoString(HashMap<String, String> seatList) {
        String priceInformation = "";
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        int standingNumber = 0;
        int circleNumber = 0;
        int stallsNumber = 0;
        int centerNumber = 0;
        int vipNumber = 0;
        // For each seat, find the seat type and add the seat to a string list
        for (Map.Entry<String, String> seatInfo: seatIDs.entrySet()) {
            if (seatInfo.getValue().equals("Standing")) {
                standingNumber += 1;
            }
            if (seatInfo.getValue().equals("Center")) {
                centerNumber += 1;
            }
            if (seatInfo.getValue().equals("Upper circle")) {
                circleNumber += 1;
            }
            if (seatInfo.getValue().equals("Stalls")) {
                stallsNumber += 1;
            }
            if (seatInfo.getValue() .equals("VIP")) {
                vipNumber += 1;
            }
        }
        double eventPrice = Double.valueOf(event.getPrice());
        double totalPrice = 0;
        // For each area, get if a seat is chosen, add the appropriate summary
        // information to the string and add the price (using price modifiers)
        // to the total price
        if (standingNumber != 0) {
            double price = eventPrice * standingPrice * standingNumber;
            totalPrice = totalPrice + price;
            priceInformation = priceInformation + "Standing x " +
                               String.valueOf(standingNumber) + " = " +
                               fmt.format(price) + "\n";
        }
        if (centerNumber != 0) {
            double price = eventPrice * centerPrice * centerNumber;
            totalPrice = totalPrice + price;
            priceInformation = priceInformation + "Center x " +
                               String.valueOf(centerNumber) + " = " +
                               fmt.format(price) + "\n";
        }
        if (circleNumber != 0) {
            double price = eventPrice * circlePrice * circleNumber;
            totalPrice = totalPrice + price;
            priceInformation = priceInformation + "Circle x " +
                               String.valueOf(circleNumber) + " = " +
                               fmt.format(price) + "\n";
        }
        if (stallsNumber != 0) {
            double price = eventPrice * stallsPrice * stallsNumber;
            totalPrice = totalPrice + price;
            priceInformation = priceInformation + "Stalls x " +
                               String.valueOf(stallsNumber) + " = " +
                               fmt.format(price) + "\n";
        }
        if (vipNumber != 0) {
            double price = eventPrice * vipPrice * vipNumber;
            totalPrice = totalPrice + price;
            priceInformation = priceInformation + "VIP x " +
                               String.valueOf(vipNumber) + " = " +
                               fmt.format(price) + "\n";
        }
        // Display the total price at the end of the string
        if (!priceInformation.equals("")) {
            priceInformation = priceInformation + "\nTotal = "+
                               fmt.format(totalPrice);
        }

        return priceInformation;
    }

    /**
     * Get a string to display the currently selected seats by the user
     *
     * @param seatList A current list of user selected seats
     * @return A string displaying the selected seats in their respective areas
     */
    public String getSeatInfoString(HashMap<String, String> seatList) {
        String seatInformation = "";
        ArrayList<String> standingSeats = new ArrayList<>();
        ArrayList<String> centerSeats = new ArrayList<>();
        ArrayList<String> upperSeats = new ArrayList<>();
        ArrayList<String> stallsSeats = new ArrayList<>();
        ArrayList<String> vipSeats = new ArrayList<>();
        // For each seat, find the seat type and add the seat to a string list
        for (Map.Entry<String, String> seatInfo: seatIDs.entrySet()) {
            if (seatInfo.getValue().equals("Standing")) {
                standingSeats.add(seatInfo.getKey().split(",")[0]);
            }
            if (seatInfo.getValue().equals("Center")) {
                centerSeats.add(seatInfo.getKey().split(",")[0]);
            }
            if (seatInfo.getValue().equals("Upper circle")) {
                upperSeats.add(seatInfo.getKey().split(",")[0]);
            }
            if (seatInfo.getValue().equals("Stalls")) {
                stallsSeats.add(seatInfo.getKey().split(",")[0]);
            }
            if (seatInfo.getValue().equals("VIP")) {
                vipSeats.add(seatInfo.getKey().split(",")[0]);
            }
        }
        // If the list is not empty for a seating area, add that area and it's
        // seats to the final string
        if (!standingSeats.isEmpty())
            seatInformation = seatInformation + "Standing: "+
                              standingSeats.toString().substring(
                              1,standingSeats.toString().length()-1)+"\n";
        if (!centerSeats.isEmpty())
            seatInformation = seatInformation + "Center: " +
                              centerSeats.toString().substring(
                              1,centerSeats.toString().length()-1)+"\n";
        if (!upperSeats.isEmpty())
            seatInformation = seatInformation + "Upper circle: " +
                              upperSeats.toString().substring(
                              1,upperSeats.toString().length()-1)+"\n";
        if (!stallsSeats.isEmpty())
            seatInformation = seatInformation + "Stalls: " +
                              stallsSeats.toString().substring(
                              1,stallsSeats.toString().length()-1)+"\n";
        if (!vipSeats.isEmpty())
            seatInformation = seatInformation + "VIP: "+
                              vipSeats.toString().substring(
                              1,vipSeats.toString().length()-1)+"\n";
        if (!seatInformation.equals("")) {
            seatInformation = seatInformation.substring(
                              0,seatInformation.length()-1);
        }
        return seatInformation;
    }

    /**
     * Post seat information to the API, using the selected seats and the
     * current posted booking ID
     *
     * @param bookingID Booking ID that has been posted to the API
     */
    public void postSeats(int bookingID) {
        // For each selected seat, create a sitting object, add the appropriate
        // information and make an API call to pose the sitting object to the
        // API
        for (Map.Entry<Integer, Boolean> seat: selectedSeats.entrySet()) {
            int seatID = seat.getKey();

            Event event = getActivity().getIntent().getExtras().getParcelable(
                                               "com.dolphin.thegigisup.Event");

            Sitting sitting = new Sitting();
            sitting.setUserId(userID);
            sitting.setSeatId(seatID);
            sitting.setBookingId(bookingID);
            sitting.setEventId(event.getId());

            PostSitting task = new PostSitting(service, sitting);
            runner.run(task, taskScope);
        }
        // Print a successful toast message and return to the booking page to
        // see the booking confirmation
        Toast.makeText(getActivity().getApplicationContext(),
                "Purchase successful",
                Toast.LENGTH_LONG)
                .show();
        Intent i = new Intent(getActivity(), MainActivity.class);
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(CONFIRMED, true);
        startActivity(i);
    }

    /**
     * Post a booking object to the API
     */
    private class PostBooking extends ApiTask<Response> {

        private Booking booking;

        public PostBooking(ServiceInterface service, Booking booking) {
            super(service);
            this.booking = booking;
        }

        /**
         * Add a random ticket reference number to the booking then post the
         * booking object to the API
         *
         * @param service A serviceInterface object used to interact with the
         * API
         * @return A confirmation response after the booking is posted
         */
        @Override
        public Response doQuery(ServiceInterface service) {
            booking.setTicketRef(randomString(10));
            return service.postBooking(booking);
        }

        /**
         * After posting the booking, get the booking ID from the response
         * object
         *
         * @param response Confirmation API response
         */
        @Override
        public void done(Response response) {
            // Read the response body into a string
            BufferedReader reader;
            StringBuilder sb = new StringBuilder();
            try {

                reader = new BufferedReader(
                         new InputStreamReader(response.getBody().in()));

                String line;

                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Pull the booking ID from the response string
            String result = sb.toString();
            String[] split1 = result.split("\"id\":");
            String[] split2 = split1[1].split(",");
            int bookingID = Integer.valueOf(split2[0]);
            // After the booking ID is retrieved, post the seats associated
            // with that booking
            postSeats(bookingID);
        }

        /**
         * If the posting fails, print an appropriate toast error message
         */
        @Override
        public void failed(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(),
                    e.toString(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Post a sitting object to the API
     */
    private class PostSitting extends ApiTask<Response> {

        private Sitting sitting;

        public PostSitting(ServiceInterface service, Sitting sitting) {
            super(service);
            this.sitting = sitting;
        }

        /**
         * Post a given sitting object to the API
         * @return A confirmation response object
         */
        @Override
        public Response doQuery(ServiceInterface service) {
            return service.postSitting(sitting);
        }

        @Override
        public void done(Response response) {}

        /**
         * If the sitting post fails, display a toast with the appropriate
         * error information
         */
        @Override
        public void failed(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(),
                    e.toString(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Get the price modifiers for the different seat types from the API
     */
    private class GetPriceModifiers extends ApiTask<List<SeatType>> {

        public GetPriceModifiers(ServiceInterface service) {super(service);}

        /**
         * Query the API to get a list of SeatType object
         *
         * @return A list of seat type objects from the API
         */
        @Override
        public List<SeatType> doQuery(ServiceInterface service) {
            return service.getPrices();
        }

        /**
         * Get the seat type prices using the returned list
         *
         * @param seatTypes The seat type objects from the API call
         */
        @Override
        public void done(List<SeatType> seatTypes) {getPrices(seatTypes);}

        /**
         * If the API call fails, display a toast with the appropriate error
         * information
         */
        @Override
        public void failed(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(),
                    e.toString(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Using the seat type objects obtained from the API, retrieve the prices
     * of each seat type and set them in the confirmation fragment
     *
     * @param seatTypes A list of seat types obtained from the API
     */
    public void getPrices(List<SeatType> seatTypes) {
        for (SeatType seat: seatTypes) {
            if (seat.getId() == 1) {
                standingPrice = seat.getPriceModifier();
            }
            if (seat.getId() == 2) {
                circlePrice = seat.getPriceModifier();
            }
            if (seat.getId() == 3) {
                centerPrice = seat.getPriceModifier();
            }
            if (seat.getId() == 4) {
                stallsPrice = seat.getPriceModifier();
            }
            if (seat.getId() == 5) {
                vipPrice = seat.getPriceModifier();
            }
        }
    }
}
