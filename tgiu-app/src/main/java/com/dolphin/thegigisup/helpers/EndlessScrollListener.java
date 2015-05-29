package com.dolphin.thegigisup.helpers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Modified from http://www.avocarrot.com/blog
 * /implement-infinitely-scrolling-list-android/
 *
 * @author Team Dolphin
 */
public abstract class EndlessScrollListener
        extends RecyclerView.OnScrollListener {

    private final LinearLayoutManager layoutManager;
    private int bufferItemCount = 10;
    private int currentPage = 0;
    private int itemCount = 0;
    private boolean isLoading = true;
    private int visibleItemCount;
    private int totalItemCount;
    private int firstVisibleItem;

    /**
     * Implements the recycler view version of
     * an endless scroller
     *
     * @param bufferItemCount If 0 the user will have to go down
     *                        to the end of the current list
     *                        to get more items. If equals the size
     *                        of the current list then it will load more events
     *                        straight away
     * @param llm Useful for getting item counts
     */
    public EndlessScrollListener(int bufferItemCount, LinearLayoutManager llm) {
        this.bufferItemCount = bufferItemCount;
        this.layoutManager = llm;
    }

    public abstract void loadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(RecyclerView view, int scrollState) {
        // Do Nothing
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // Get various counts
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();


        // If the total items is less than what is
        // currently shown then we need to change it
        if (totalItemCount < itemCount) {
            itemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.isLoading = true;
            }
        }

        // If we got the items
        if (isLoading && (totalItemCount > itemCount)) {
            isLoading = false;
            itemCount = totalItemCount;
            currentPage++;
        }

        // If we need more items
        if (!isLoading &&
                (totalItemCount - visibleItemCount) <=
                        (firstVisibleItem + bufferItemCount)) {
            loadMore(currentPage + 1, totalItemCount);
            isLoading = true;
        }
    }
}
