package com.dolphin.thegigisup.activitys;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.dolphin.thegigisup.*;
import com.dolphin.thegigisup.adapters.EventsAdapter;
import com.dolphin.thegigisup.api.ApiTask;
import com.dolphin.thegigisup.api.Runner;
import com.dolphin.thegigisup.api.ServiceFactory;
import com.dolphin.thegigisup.api.ServiceInterface;
import com.dolphin.thegigisup.helpers.CustomLinearLayoutManager;
import com.dolphin.thegigisup.helpers.DividerItemDecoration;
import com.dolphin.thegigisup.models.Event;
import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Activity that allows a user to search for events for a specified artist name
 *
 * @author Team Dolphin 21/03/2015.
 */
public class SearchResultsActivity extends ActionBarActivity {

    // Initialise variables
    private RecyclerView eventsRecycler;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventsList = new ArrayList<>();
    private final Runner.Scope taskScope = new Runner.Scope();
    private ServiceInterface serviceInterface;
    private Runner runner;
    private String query;

    /**
     * On create, set the necessary endpoints, adapters and intents
     *
     * @param savedInstanceState Use a saved instance if it exists
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setApiEndpoint();
        setEventsAdapter();
        handleIntent(getIntent());
    }

    /**
     * Sets up the API stuff
     */
    private void setApiEndpoint() {
        serviceInterface = ServiceFactory.createInstance();
        runner = new Runner(Executors.newCachedThreadPool(),
                new Handler());
    }

    /**
     * Sets the events adapter and attaches it to
     * the relevant recycler view
     */
    private void setEventsAdapter() {
        eventsRecycler = (RecyclerView) findViewById(R.id.sea_rv_events_list);
        eventsRecycler.addItemDecoration(
                new DividerItemDecoration(this,
                                        DividerItemDecoration.VERTICAL_LIST));
        eventsRecycler.setItemAnimator(new DefaultItemAnimator());
        eventsAdapter =
                new EventsAdapter(eventsList,
                                      R.layout.search_results_activity_event);
        eventsRecycler.setAdapter(eventsAdapter);

        CustomLinearLayoutManager llm =
                new CustomLinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false);
        eventsRecycler.setLayoutManager(llm);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    /**
     * We need a place to handle the search query
     * that is passed from MainActivity.
     *
     * @param intent Intent that is passed.
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            getSupportActionBar().setTitle("\"" + query + "\"" + " results");

            RetrieveSearchedEvents task =
                    new RetrieveSearchedEvents(serviceInterface);
            runner.run(task, taskScope);

        } else {
            getSupportActionBar().setTitle(getString(R.string.search_results));
        }
    }



    /**
     * Creates the options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_results, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        taskScope.cancelAll();
    }

    /**
     * Retrieve the searched artist events from the API
     */
    private class RetrieveSearchedEvents extends ApiTask<List<Event>> {
        public RetrieveSearchedEvents(ServiceInterface service) {
            super(service);
        }

        /**
         * Get the list of searched events from the API using the current date
         * and the query
         *
         * @param service A serviceInterface object to interact with the API
         * @return A list of searched event objects
         */
        @Override
        public List<Event> doQuery(ServiceInterface service) {
            DateTime now = DateTime.now();
            return service.searchForEvents(
                    now.toString("yyyy-MM-dd"),
                    prepareQuery(query),
                    30);
        }

        /**
         * Replace spaces with % for sending
         * and searching with MySQL regex
         */
        private String prepareQuery(String query) {
            String percent = "%%";
            String spacesReplaced = query.replace(" ", percent);
            // Need to wrap the resultant string in further
            // %'s
            return percent + spacesReplaced + percent;
        }

        /**
         * When the API call is done, set the new search events in the events
         * adapter
         *
         * @param events A list of events returned from the API search
         */
        @Override
        public void done(List<Event> events) { eventsAdapter.setEvents(events);}

        /**
         * If the API call throws an error, print that error information in
         * a toast
         */
        @Override
        public void failed(Exception e) {
            Toast.makeText(getApplicationContext(),
                    e.toString(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

}
