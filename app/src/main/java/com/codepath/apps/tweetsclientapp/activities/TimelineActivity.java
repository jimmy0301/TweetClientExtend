package com.codepath.apps.tweetsclientapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.TwitterApplication;
import com.codepath.apps.tweetsclientapp.TwitterClient;
import com.codepath.apps.tweetsclientapp.adapters.TweetsRecyclerAdapter;
import com.codepath.apps.tweetsclientapp.fragments.AddTweetFragment;
import com.codepath.apps.tweetsclientapp.libs.DividerItemDecoration;
import com.codepath.apps.tweetsclientapp.libs.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.max;

public class TimelineActivity extends AppCompatActivity {

   private EndlessRecyclerViewScrollListener scrollListener;
   
   private TwitterClient client;
   private ArrayList<Tweet> tweets;
   private TweetsRecyclerAdapter adapter;
   private RecyclerView rvTweets;
   private SwipeRefreshLayout swipeContainer;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_timeline);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
      rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
      tweets = new ArrayList<>();

      adapter = new TweetsRecyclerAdapter(this, tweets);

      rvTweets.setAdapter(adapter);

      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
      linearLayoutManager.scrollToPosition(0);
      rvTweets.setLayoutManager(linearLayoutManager);

      rvTweets.setHasFixedSize(true);

      RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
      rvTweets.addItemDecoration(itemDecoration);
      swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
              android.R.color.holo_green_light,
              android.R.color.holo_orange_light,
              android.R.color.holo_red_light);

      client = TwitterApplication.getRestClient();
      populateTimeline();
      setupSWlistener();
      setupRVlistener(linearLayoutManager);

   }

   private void setupSWlistener() {
      swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
            fetchTimelineAsync();
         }
      });
   }

   private void setupRVlistener(LinearLayoutManager linearLayoutManager) {
      scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
         @Override
         public void onLoadMore(Long maxId, int totalItemsCount) {
            Log.d("onLoadMore", "the max id: " + maxId);
            loadNextDataFromApi(maxId);
         }
      };
      rvTweets.addOnScrollListener(scrollListener);
   }

   private void loadNextDataFromApi(Long max_id) {
      client.getMoreHomeTimelinelist(max_id, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            //Log.d("DEBUG", response.toString());
            int curSize = adapter.getItemCount();
            Log.d("LoadMore", "cur_size:" + curSize);
            ArrayList <Tweet> items = Tweet.fromJSONArray(response);
            tweets.addAll(curSize, items);
            adapter.notifyItemRangeInserted(curSize, tweets.size() - 1);
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.d("DEBUG", errorResponse.toString());
         }
      });
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_timeline, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_add:
            FragmentManager fm = getSupportFragmentManager();
            AddTweetFragment addTweetFragment = AddTweetFragment.newInstance();
            addTweetFragment.show(fm, "fragment_add_tweet");
            return true;
         default:
            return false;
      }
   }

   //Fill in list view
   private void populateTimeline() {
      client.getHomeTimelinelist(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            //Log.d("DEBUG", response.toString());
            int curSize = adapter.getItemCount();

            ArrayList <Tweet> items = Tweet.fromJSONArray(response);
            tweets.addAll(items);
            adapter.notifyItemRangeInserted(curSize, items.size());
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.d("DEBUG", errorResponse.toString());
            swipeContainer.setRefreshing(false);
         }
      });
   }

   public void fetchTimelineAsync() {
      // Send the network request to fetch the updated data
      // `client` here is an instance of Android Async HTTP
      // getHomeTimeline is an example endpoint.

      client.getHomeTimelinelist(new JsonHttpResponseHandler() {
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            // Remember to CLEAR OUT old items before appending in the new ones
            adapter.clear();
            tweets.clear();
            Log.d("fetchTimelineAsync", "list size: " + tweets.size());
            // ...the data has come back, add new items to your adapter...
            ArrayList <Tweet> items = Tweet.fromJSONArray(response);

            Log.d("fetchTimelineAsync", "item size: " + items.size());

            tweets.addAll(items);
            adapter.addAll(tweets);
            // Now we call setRefreshing(false) to signal refresh has finished

            swipeContainer.setRefreshing(false);
            adapter.notifyItemRangeInserted(0, items.size());
            scrollListener.resetState();
         }

         public void onFailure(Throwable e) {
            Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            swipeContainer.setRefreshing(false);
         }
      });


   }
}
