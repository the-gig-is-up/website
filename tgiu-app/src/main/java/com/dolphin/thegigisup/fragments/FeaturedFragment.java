package com.dolphin.thegigisup.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import au.com.gridstone.grex.GRexPersister;
import au.com.gridstone.grex.converters.GsonConverter;
import com.dolphin.thegigisup.*;
import com.dolphin.thegigisup.adapters.EventsAdapter;
import com.dolphin.thegigisup.api.Runner;
import com.dolphin.thegigisup.api.ServiceFactory;
import com.dolphin.thegigisup.helpers.CustomLinearLayoutManager;
import com.dolphin.thegigisup.models.Event;
import com.google.gson.annotations.Expose;
import org.joda.time.DateTime;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Featured page which shows the user upcoming and hot events
 *
 * @author Team Dolphin
 */
public class FeaturedFragment
        extends Fragment {

    private static final String EVENTS = "FeaturedEvents";
    private static final String HOTEVENTS = "FeaturedHot";
    // Initialise variables
    private final Runner.Scope taskScope = new Runner.Scope();
    private RecyclerView vertScrollView, horiScrollView;
    private List<Event> eventList = new ArrayList<>();
    private List<Event> scrollEventList = new ArrayList<>();
    private EventsAdapter eventsAdapter, hotEventsAdapter;
    private Button viewAllButton;
    private GRexPersister persister;
    private Observable<ArrayList<Event>> eventsObservable;
    private Subscription subscription;
    private Observable<Hot> hotEventsObservable;
    private Subscription subscription2;
    private RelativeLayout hotEventsLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    /**
     * On creation of the view, inflate the featured xml layout and initialise
     * the needed values
     *
     * @return The created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.featured_frag, container, false);

        vertScrollView =
                (RecyclerView) v.findViewById(R.id.fea_rv_events_list);
        vertScrollView.setItemAnimator(new DefaultItemAnimator());

        viewAllButton =
                (Button) v.findViewById(R.id.fea_bt_view_all);
        hotEventsLayout =
                (RelativeLayout) v.findViewById(R.id.fea_rl_hot_events);


        eventsAdapter =
                new EventsAdapter(eventList, R.layout.featured_frag_event);
        vertScrollView.setAdapter(eventsAdapter);

        CustomLinearLayoutManager llm =
                new CustomLinearLayoutManager(getActivity(),
                                              LinearLayoutManager.VERTICAL,
                                              false);
        vertScrollView.setLayoutManager(llm);

        // Overwrite scroll method of vertScrollView to allow
        // main ScrollView to scroll over the top
        vertScrollView.setOnTouchListener(new ListViewTouchListener());

        //Find the relativelayout to hold the horizontal scroll view
        horiScrollView = (RecyclerView) v.findViewById(R.id.fea_rv_eventscroll);
        horiScrollView.setItemAnimator(new DefaultItemAnimator());

        hotEventsAdapter = new EventsAdapter(scrollEventList,
                R.layout.featured_frag_scrollevent);
        horiScrollView.setAdapter(hotEventsAdapter);


        LinearLayoutManager llm2 =
                new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.HORIZONTAL,
                        false);

        horiScrollView.setLayoutManager(llm2);

        // Overwrite scroll method of vertScrollView to allow
        // main ScrollView to scroll over the top
        horiScrollView.setOnTouchListener(new ListViewTouchListener());

        setApiRunners(savedInstanceState);

        return v;
    }


    /**
     * Allows us to contact the API
     * @param savedInstanceState Saved state if valid
     */
    private void setApiRunners(Bundle savedInstanceState) {
        persister = new GRexPersister(
                new GsonConverter(),
                new File(getActivity().getCacheDir().getAbsolutePath()));

        if (savedInstanceState == null) {
            DateTime now = DateTime.now();
            eventsObservable = ServiceFactory
                    .createInstance()
                    .getUpcomingEvents(now.toString("yyyy-MM-dd"), 10)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
            hotEventsObservable = ServiceFactory
                    .createInstance()
                    .getHotEvents()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
        }

        // Query the API for featured events using the current date and a
        // featured events number limit of 10
        subscription =
                eventsObservable
                    .flatMap(events -> persister.putList(EVENTS,
                            events,
                            Event.class))
                    .onErrorResumeNext(throwable -> {
                        return persister.getList(EVENTS, Event.class);
                    })
                    .subscribe(this::updateFeaturedEvents);

        subscription2 =
                hotEventsObservable
                    .flatMap(hot -> persister.putList(HOTEVENTS,
                            hot.events,
                            Event.class))
                    .onErrorResumeNext(throwable -> {
                        return persister.getList(HOTEVENTS, Event.class);
                    })
                    .subscribe(this::updateHotEvents);
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        subscription2.unsubscribe();
        super.onDestroy();
    }

    /**
     * Update the events for the featured events adapter and the hot events
     * if the hot events list is null
     *
     * @param events A list of featured events from the API
     */
    private void updateFeaturedEvents(List<Event> events) {
        if (eventsAdapter != null && events.size() > 0) {
            toggleView(viewAllButton, true);
            eventsAdapter.setEvents(events);
        } else {
            toggleView(viewAllButton, false);
        }
    }

    /**
     * Update the hot events adapter with a list of events
     *
     * @param events A list of hot events from the API
     */
    private void updateHotEvents(List<Event> events) {
        if (hotEventsAdapter != null && events.size() > 0) {
            toggleView(hotEventsLayout, true);
            hotEventsAdapter.setEvents(events);
        } else {
            toggleView(hotEventsLayout, false);
        }

    }

    /**
     * Shows/hides a view
     *
     * @param show true if you want to show it, false otherwise.
     */
    private void toggleView(View view, boolean show) {
        int visible = view.getVisibility();
        if (show && visible == View.GONE) {
            view.setVisibility(View.VISIBLE);
        } else if (!show && visible == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * Hot class is needed due to the API returning a key called "events"
     * with the key mapping to a list of events.
     */
    public static class Hot {
        Hot() {}

        @Expose
        List<Event> events;
    }


    private final class ListViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:

                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        }
    }

}
