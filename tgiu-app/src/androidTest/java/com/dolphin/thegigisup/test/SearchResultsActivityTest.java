package com.dolphin.thegigisup.test;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.activitys.SearchResultsActivity;

public class SearchResultsActivityTest extends
        ActivityInstrumentationTestCase2<SearchResultsActivity> {

    private SearchResultsActivity searchResultsActivity;

    public SearchResultsActivityTest() {super(SearchResultsActivity.class);}

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Create a mock query for Intent.
        String query = "Fake Artist".toLowerCase();

        // Setup mock intent for SearchManager.query to search
        // for mock query.
        Intent intent = new Intent();
        intent.putExtra(SearchManager.QUERY, query);
        setActivityIntent(intent);

        searchResultsActivity = getActivity();
    }

    public void testPreconditions(){
        assertNotNull("searchResultsActivity is null", searchResultsActivity);
    }

    public void testSearchResults() {
        RecyclerView recyclerView = (RecyclerView) searchResultsActivity.
                findViewById(R.id.sea_rv_events_list);

        final int expectedResultNumber = 0;
        final int actualResultNumber = recyclerView.getChildCount();

        assertEquals(expectedResultNumber, actualResultNumber);
    }
}