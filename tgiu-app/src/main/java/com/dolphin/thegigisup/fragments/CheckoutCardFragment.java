package com.dolphin.thegigisup.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dolphin.thegigisup.*;
import com.dolphin.thegigisup.activitys.AddCardActivity;
import com.dolphin.thegigisup.activitys.CheckoutActivity;
import com.dolphin.thegigisup.adapters.CardAdapter;
import com.dolphin.thegigisup.api.ApiTask;
import com.dolphin.thegigisup.api.Runner;
import com.dolphin.thegigisup.api.ServiceFactory;
import com.dolphin.thegigisup.api.ServiceInterface;
import com.dolphin.thegigisup.helpers.CustomLinearLayoutManager;
import com.dolphin.thegigisup.models.Card;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * A fragment that displays a list of cards that the user can choose to pay for
 * their ticket booking
 *
 * @author Team Dolphin 12/04/15.
 */
public class CheckoutCardFragment extends Fragment
                                    implements View.OnClickListener{

    // Initialise the variables used for the API calls
    private Runner runner;
    private ServiceInterface service;
    private final Runner.Scope taskScope = new Runner.Scope();
    private CheckoutActivity checkoutActivity;
    private List<Card> cardList = new ArrayList<>();
    private CardAdapter cardAdapter;
    private Card selectedCard;
    private TextView noCards;

    // Initialise the shared preferences variables used to get the current
    // logged in user ID
    private SharedPreferences sharedpreferences;
    public static final String MYPREFERENCES = "MyPreferences" ;
    public static final String USERID = "UserID";
    private int userID;

    /**
     * On creation, initialise the variables used for the API calls
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create thread pool runner
        runner = new Runner(Executors.newCachedThreadPool(),
                new Handler());
        ServiceFactory serviceFactory = new ServiceFactory();
        service = serviceFactory.createInstance();
    }

    /**
     * On attach, get the attached activity and from that, the shared
     * preferences
     *
     * @param activity The attached activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        checkoutActivity = (CheckoutActivity) activity;
        sharedpreferences = getActivity().getSharedPreferences(MYPREFERENCES,
                                                         Context.MODE_PRIVATE);
    }

    /**
     * On creation of the view, inflate the layout in the container and
     * initialise the appropriate variables
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.select_card_frag, container,
                                     false);

        final RecyclerView recyclerView = (RecyclerView)view.findViewById(
                                                      R.id.card_rv_cardscroll);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cardAdapter =
                new CardAdapter(cardList, R.layout.card_frag_card);
        recyclerView.setAdapter(cardAdapter);

        CustomLinearLayoutManager llm =
                new CustomLinearLayoutManager(getActivity(),
                        LinearLayoutManager.VERTICAL,
                        false);
        recyclerView.setLayoutManager(llm);

        // Using the current user ID, get the current stored user cards to
        // display in the recyclerview
        userID = sharedpreferences.getInt(USERID, 0);
        getUserCards(userID, runner, service);

        cardAdapter.SetOnItemClickListener(new CardAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position, Card card) {
                // When the user selects a card, clear the current selected
                // card hashmap, store the card number and id and pass the
                // information to the checkoutActivity
                TextView cardNumber = (TextView) view.findViewById(
                                                 R.id.card_tv_card_number);
                selectedCard = card;
                checkoutActivity.setSelectedCard(selectedCard);
            }
        });

        noCards = (TextView)view.findViewById(R.id.chec_tv_nocards);
        noCards.setVisibility(View.GONE);

        Button addCard = (Button)view.findViewById(R.id.chec_bn_addcard);
        addCard.setOnClickListener(this);
        Button removeCard = (Button)view.findViewById(R.id.chec_bn_removecard);
        removeCard.setOnClickListener(this);

        return view;
    }

    /**
     * Set the methods for on click for the user to remove a card payment option
     * or add one
     *
     * @param v The clicked view v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.chec_bn_removecard):
                if (selectedCard != null) {
                    // Add an alert making sure the user wants to remove the
                    // card
                    final AlertDialog.Builder removeCheck =
                            new AlertDialog.Builder(getActivity());
                    removeCheck.setTitle("Remove card");
                    removeCheck.setMessage("Are you sure you want to remove" +
                            " the card ending "+
                            selectedCard.getId().substring(12)+" ?");

                    removeCheck.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            RemoveCard task = new RemoveCard(service, userID,
                                    selectedCard.getId());
                            runner.run(task, taskScope);
                        }
                    });

                    removeCheck.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                }
                            });
                    removeCheck.show();
                }
                if (selectedCard == null) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please select a card to be removed",
                            Toast.LENGTH_LONG)
                            .show();
                }
                break;
            // If the user presses the add card button, open up the add card
            // activity and wait for a result
            case R.id.chec_bn_addcard:
                Intent i = new Intent(getActivity(), AddCardActivity.class);
                startActivityForResult(i, AddCardActivity.REQUEST_ADDCARD);
                break;
            default:
                break;
        }
    }

    /**
     * Get the activity result when a card is successfully added so the
     * card list can be refreshed
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        // If the activity that finished was the
        // Add Card activity
        if (requestCode == AddCardActivity.REQUEST_ADDCARD) {
            // If the Add card activity successfully added a card
            if (resultCode == AddCardActivity.RESULT_OK) {
                getUserCards(userID, runner, service);
            }
        }
    }

    /**
     * Get the current stored card information from the API for a given user
     *
     * @param userID The current user ID
     */
    public void getUserCards(int userID, Runner runner,
                             ServiceInterface service) {
        RetrieveCards task = new RetrieveCards(service, userID);
        runner.run(task, taskScope);
    }

    /**
     * Update the cards shown in the recyclerview
     *
     * @param cardList A list of cards retrieved from the API
     */
    public void updateCards(List<Card> cardList) {
        if (cardAdapter != null && cardList.size() > 0) {
            cardAdapter.setCards(cardList);
            if (noCards.getVisibility() == View.VISIBLE) {
                noCards.setVisibility(View.GONE);
            }
        }
        if (cardList.size() == 0) {
            cardAdapter.setCards(cardList);
            noCards.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Retrieve the stored card information for the current user from the API
     */
    private class RetrieveCards extends ApiTask<List<Card>> {

        private int userID;

        public RetrieveCards(ServiceInterface service, int userID) {
            super(service);
            this.userID = userID;
        }

        /**
         * Query the API to get a list of Card objects using the user ID
         */
        @Override
        public List<Card> doQuery(ServiceInterface service) {
            return service.getCardsForUser(userID);
        }

        /**
         * Update the recyclerview with the received card information
         *
         * @param cardList A list of cards retrieved from the API
         */
        @Override
        public void done(List<Card> cardList) { updateCards(cardList); }

        /**
         * If the API call has an error, print the respective error information
         * in a toast message
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
     * Remove the selected card for the current user from the API
     */
    private class RemoveCard extends ApiTask<Response> {

        private int userID;
        private String cardID, accessToken;

        public RemoveCard(ServiceInterface service, int userID, String cardID) {
            super(service);
            this.userID = userID;
            this.cardID = cardID;
        }

        /**
         * Query the API to get a list of Card objects using the user ID
         */
        @Override
        public Response doQuery(ServiceInterface service) {
            return service.removeUserCard(cardID, userID);
        }

        /**
         * Update the recyclerview with the updated cards stored and deselect
         * the removed card
         *
         * @param response A confirmation response from the API
         */
        @Override
        public void done(Response response) {
            cardAdapter.deselectAll();
            checkoutActivity.setSelectedCard(null);
            getUserCards(userID, runner, service);
        }

        /**
         * If the API call has an error, print the respective error information
         * in a toast message
         */
        @Override
        public void failed(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(),
                    e.toString(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
