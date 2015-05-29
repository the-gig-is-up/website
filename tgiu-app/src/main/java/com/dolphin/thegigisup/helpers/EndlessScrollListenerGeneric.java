package com.dolphin.thegigisup.helpers;

import android.widget.AbsListView;

/**
 * This class should be used for any type of listview
 * (gridview inclusive). If the seats view is slow
 * try lazy loading with this scroll listener
 *
 * @author Team Dolphin 24/04/201
 */
public abstract class EndlessScrollListenerGeneric
        implements AbsListView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    /**
     * Implements the recycler view version of
     * an endless scroller
     *
     * @param bufferItemCount If 0 the user will have to go down
     *                        to the end of the current list
     *                        to get more items. If equals the size
     *                        of the current list then it will load more events
     *                        straight away
     */
    public EndlessScrollListenerGeneric(int bufferItemCount) {
        this.visibleThreshold = bufferItemCount;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Do Nothing
    }

    @Override
    public void onScroll(AbsListView listView,
                         int firstVisibleItem,
                         int visibleItemCount,
                         int totalItemCount) {


        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.loading = true; }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the
        // current page number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to
        // fetch the data.
        if (!loading && (totalItemCount - visibleItemCount)<=(firstVisibleItem
                                                        + visibleThreshold)) {
            onLoadMore(currentPage + 1, totalItemCount);
            loading = true;
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);
}

