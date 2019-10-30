package com.example.moviejunkie.Helper;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.GridView;

public abstract class EndlessScrollListener  implements AbsListView.OnScrollListener {
    // The minimum number of items to have below your current scroll position
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


    private BottomNavigationView navView;
    private GridView Grid;


    public EndlessScrollListener() {
    }

    public EndlessScrollListener(BottomNavigationView navView, GridView grid) {
        this.navView = navView;
        Grid = grid;
    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {

        int currentFirstVisPos = view.getFirstVisiblePosition();
        int myLastVisiblePos=Grid.getFirstVisiblePosition();
        if(currentFirstVisPos > myLastVisiblePos) {
            //scroll down
            Log.d("TEST", "onScroll: ScrollDown");
            if (navView.isShown())
                navView.clearAnimation();
            navView.animate().translationY(navView.getHeight()).setDuration(200);
        }

        if(currentFirstVisPos < myLastVisiblePos) {
            //scroll up
            Log.d("TEST", "onScroll: ScrollUp");

            navView.clearAnimation();
            navView.animate().translationY(0).setDuration(200);

        }
        myLastVisiblePos = currentFirstVisPos;


        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.loading = true; }

        }
        // If it's still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;

        }

        // If it isn't currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
            loading = onLoadMore(currentPage + 1, totalItemCount);

        }
    }

    // Defines the process for actually loading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    public abstract boolean onLoadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }
}