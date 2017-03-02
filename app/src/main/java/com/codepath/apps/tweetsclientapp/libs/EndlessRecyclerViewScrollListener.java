package com.codepath.apps.tweetsclientapp.libs;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.codepath.apps.tweetsclientapp.adapters.TweetsRecyclerAdapter;

/**
 * Created by keyulun on 2017/2/27.
 */

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
   // The minimum amount of items to have below your current scroll position
   // before loading more.
   private int visibleThreshold = 3;

   protected boolean firstTime = true;
   // The current offset index of data you have loaded
   private Long max_id = null;
   // The total number of items in the dataset after the last load
   private int previousTotalItemCount = 0;
   // True if we are still waiting for the last set of data to load.
   private boolean loading = true;


   RecyclerView.LayoutManager mLayoutManager;

   public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
      this.mLayoutManager = layoutManager;
   }

   public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
      this.mLayoutManager = layoutManager;
      visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
   }

   public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
      this.mLayoutManager = layoutManager;
      visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
   }

   public int getLastVisibleItem(int[] lastVisibleItemPositions) {
      int maxSize = 0;
      for (int i = 0; i < lastVisibleItemPositions.length; i++) {
         if (i == 0) {
            maxSize = lastVisibleItemPositions[i];
         }
         else if (lastVisibleItemPositions[i] > maxSize) {
            maxSize = lastVisibleItemPositions[i];
         }
      }
      return maxSize;
   }

   // This happens many times a second during a scroll, so be wary of the code you place here.
   // We are given a few useful parameters to help us work out if we need to load some more data,
   // but first we check if we are waiting for the previous load to finish.
   @Override
   public void onScrolled(RecyclerView view, int dx, int dy) {
      int lastVisibleItemPosition = 0;
      int totalItemCount = mLayoutManager.getItemCount();

      if (mLayoutManager instanceof StaggeredGridLayoutManager) {
         int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
         // get maximum element within the list
         lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
      } else if (mLayoutManager instanceof GridLayoutManager) {
         lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
      } else if (mLayoutManager instanceof LinearLayoutManager) {
         lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
      }

      // If the total item count is zero and the previous isn't, assume the
      // list is invalidated and should be reset back to initial state
      TweetsRecyclerAdapter adapter = (TweetsRecyclerAdapter) view.getAdapter();
      if (totalItemCount < previousTotalItemCount) {
         this.loading = false;
         this.max_id = adapter.getLastItemId();
         this.previousTotalItemCount = totalItemCount;
         if (totalItemCount == 0) {
            this.loading = true;
         }
      }
      // If it’s still loading, we check to see if the dataset count has
      // changed, if so we conclude it has finished loading and update the current page
      // number and total item count.
      if (loading && (totalItemCount > previousTotalItemCount)) {
         loading = false;
         previousTotalItemCount = totalItemCount;
         this.max_id = adapter.getLastItemId();
         Log.d("onscroll", "set the max_id: " + max_id);
      }

      // If it isn’t currently loading, we check to see if we have breached
      // the visibleThreshold and need to reload more data.
      // If we do need to reload some more data, we execute onLoadMore to fetch the data.
      // threshold should reflect how many total columns there are too
      if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
         if (max_id == null) {
            Log.d("on scroll", "max id is null");
            onLoadMore(null, totalItemCount);
         }
         else {
            Log.d("onScrolled", "the max_id: " + (max_id-1));
            onLoadMore(max_id - 1, totalItemCount);
         }

         loading = true;
      }
   }

   // Call this method whenever performing new searches
   public void resetState() {
      this.max_id = null;
      this.previousTotalItemCount = 0;
      this.loading = true;
   }

   // Defines the process for actually loading more data based on page
   public abstract void onLoadMore(Long maxId, int totalItemsCount);
}
