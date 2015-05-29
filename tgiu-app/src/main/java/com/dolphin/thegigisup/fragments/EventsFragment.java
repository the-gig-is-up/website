package com.dolphin.thegigisup.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dolphin.thegigisup.*;
import com.dolphin.thegigisup.adapters.EventsAdapter;
import com.dolphin.thegigisup.api.ApiTask;
import com.dolphin.thegigisup.api.Refreshable;
import com.dolphin.thegigisup.api.Runner;
import com.dolphin.thegigisup.api.ServiceInterface;
import com.dolphin.thegigisup.helpers.CustomLinearLayoutManager;
import com.dolphin.thegigisup.helpers.DividerItemDecoration;
import com.dolphin.thegigisup.helpers.EndlessScrollListener;
import com.dolphin.thegigisup.models.Event;
import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment page to show the user the events in the app
 *
 * @author Team Dolphin 22/03/2015.
 */
public class EventsFragment extends Fragment
                            implements Refreshable {

    private RecyclerView eventsRecycler;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventsList = new ArrayList<>();
    private final Runner.Scope taskScope = new Runner.Scope();
    private ServiceInterface service;
    private Runner runner;
    public int offset = 0;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_results_activity, container,
                                  false);
        setEventsAdapter(v);

        return v;
    }

    /**
     * Sets the events adapter and attaches it to the relevant recycler view
     */
    private void setEventsAdapter(View v) {
        eventsRecycler =
                (RecyclerView) v.findViewById(R.id.sea_rv_events_list);
        eventsRecycler.addItemDecoration(
                new DividerItemDecoration(
                        getActivity(),
                        DividerItemDecoration.VERTICAL_LIST)
        );
        eventsRecycler.setItemAnimator(new DefaultItemAnimator());
        eventsAdapter = new EventsAdapter(
                        eventsList,
                        R.layout.search_results_activity_event);
        eventsRecycler.setAdapter(eventsAdapter);

        CustomLinearLayoutManager llm =
                        new CustomLinearLayoutManager(
                                getActivity(),
                                LinearLayoutManager.VERTICAL,
                                false);
        eventsRecycler.setLayoutManager(llm);
        EndlessScroller endless = new EndlessScroller(1, llm);
        eventsRecycler.setOnScrollListener(endless);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        taskScope.cancelAll();
    }

    /**
     * On fragment refresh, retrieve the events from the API
     */
    @Override
    public void refresh(Runner runner, ServiceInterface service) {
        this.service = service;
        this.runner = runner;
        RetrieveEvents task = new RetrieveEvents(service);
        runner.run(task, taskScope);
    }

    /**
     * RetrieveEvents gets the list of events from the API
     */
    private class RetrieveEvents extends ApiTask<List<Event>> {
        public RetrieveEvents(ServiceInterface service) {
            super(service);
        }

        /**
         * Query the API using a number limit and the current date to affect
         * which events the call gets
         *
         * @param service A serviceInterface object to communicate with the API
         * @return A list of events from the API
         */
        @Override
        public List<Event> doQuery(ServiceInterface service) {
            DateTime now = DateTime.now();
            return service.getUpcomingEventsWithOffset(
                    now.toString("yyyy-MM-dd"),
                    10,
                    offset);
        }

        /**
         * When the API call is done, update the events adapter with the
         * received API events
         *
         * @param events A list of events received from the API
         */
        @Override
        public void done(List<Event> events) { eventsAdapter.addEvents(events);}

        /**
         * If the call has an error, print a toast with the relevant error
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
     * Endless scroll class that refreshes and adds more events from the API to
     * the eventsAdapter when the listener detects the scrollView has reached
     * the bottom
     */
    private class EndlessScroller extends EndlessScrollListener {

        public EndlessScroller(int bufferItemCount, LinearLayoutManager llm) {
            super(bufferItemCount, llm);
        }

        @Override
        public void loadMore(int page, int totalItemsCount) {
            if (service != null && runner != null) {
                offset += totalItemsCount;
                refresh(runner, service);
            }
        }
    }
}
