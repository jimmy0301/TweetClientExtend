package com.codepath.apps.tweetsclientapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.applications.TwitterApplication;
import com.codepath.apps.tweetsclientapp.networks.TwitterClient;
import com.codepath.apps.tweetsclientapp.adapters.TweetsRecyclerAdapter;
import com.codepath.apps.tweetsclientapp.fragments.AddTweetFragment;
import com.codepath.apps.tweetsclientapp.libs.DividerItemDecoration;
import com.codepath.apps.tweetsclientapp.libs.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.codepath.apps.tweetsclientapp.models.TweetList;
import com.codepath.apps.tweetsclientapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements AddTweetFragment.PostTweetDialogListener {

   private EndlessRecyclerViewScrollListener scrollListener;
   
   private TwitterClient client;
   private ArrayList<Tweet> tweets;
   private TweetsRecyclerAdapter adapter;
   FloatingActionButton fab;
   FragmentManager fm;
   AddTweetFragment addTweetFragment;

   private RecyclerView rvTweets;
   private Toolbar toolbar;
   private SwipeRefreshLayout swipeContainer;

   private String name = null;
   private String screen_name = null;
   private String image_profile_url = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_timeline);
      toolbar = (Toolbar) findViewById(R.id.toolbar);
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

      fab = (FloatingActionButton) findViewById(R.id.fab);
      fm = getSupportFragmentManager();

      if (isNetworkAvailable()) {
         populateTimeline();
         getUserInfo();
      }
      else {
         Toast.makeText(getApplicationContext(), "There is no available network", Toast.LENGTH_LONG).show();
         populateTimelineFromDB();
      }

      setupAdapterListener();
      setupSWlistener();
      setupRVlistener(linearLayoutManager);
      setupFABlistener();
   }

   private void setupAdapterListener() {
      adapter.setOnItemClickListener(new TweetsRecyclerAdapter.OnRecyclerViewItemClickListener() {
         @Override
         public void onItemClick(View view, Tweet tweet) {
            Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
            Bundle b = new Bundle();
            b.putParcelable("tweet", tweet);
            b.putString("login_user_name", name);
            b.putString("login_screen_name", screen_name);

            i.putExtra("itemInfo", b);

            startActivity(i);
         }
      });
   }

   //Fill in list view
   private void populateTimeline() {
      client.getHomeTimelinelist(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            int curSize = adapter.getItemCount();

            ArrayList<Tweet> items = Tweet.fromJSONArray(response);

            tweets.addAll(items);
            adapter.notifyItemRangeInserted(curSize, items.size());

            ArrayList<TweetList> tweetLists = genDBTableList(items);
            TweetList.addTimeLineList(tweetLists);
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.d("DEBUG", errorResponse.toString());
         }
      });
   }

   private void populateTimelineFromDB() {
      List <TweetList> list = TweetList.recentItems();

      int curSize = adapter.getItemCount();

      for (TweetList item: list) {
         Tweet tweet = new Tweet();
         User user = new User();
         user.setName(item.getName());
         user.setScreenName(item.getScreen_name());
         user.setProfileImageUrl(item.getImage_url());
         tweet.setCreateAt(item.getCreateAt());
         tweet.setBody(item.getContent());
         tweet.setUid(item.getId());
         tweet.setUser(user);
         tweet.setFavorite_cnt(item.getFavoriteCnt());
         tweet.setRetweet_cnt(item.getRetweetCnt());
         tweets.add(tweet);
      }
      adapter.notifyItemRangeInserted(curSize, list.size());
   }

   private void setupFABlistener() {
      fab.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            addTweetFragment.show(fm, "fragment_add_tweet");
         }
      });
   }

   private void setupSWlistener() {
      swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
            if (isNetworkAvailable()) {
               fetchTimelineAsync();
            }
            else {
               Toast.makeText(getApplicationContext(), "There is no available network", Toast.LENGTH_LONG).show();
               fetchTimelineFromDB();
            }
         }
      });
   }

   private void setupRVlistener(LinearLayoutManager linearLayoutManager) {
      scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
         @Override
         public void onLoadMore(Long maxId, int totalItemsCount) {
            if (isNetworkAvailable()) {
               loadNextDataFromApi(maxId);
            }
            else {
               Toast.makeText(getApplicationContext(), "There is no available network", Toast.LENGTH_LONG).show();
               loadNextDataFromDB(maxId);
            }
         }
      };
      rvTweets.addOnScrollListener(scrollListener);
   }

   private void getUserInfo() {
      client.getUserProfile(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
               name = response.getString("name");
               screen_name = response.getString("screen_name");
               image_profile_url = response.getString("profile_image_url");
               addTweetFragment = AddTweetFragment.newInstance(name, screen_name, image_profile_url);

               toolbar.setTitle(name);

            } catch (JSONException e) {
               e.printStackTrace();
            }
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
         }
      });

   }

   private void loadNextDataFromDB(Long max_id) {
      List <TweetList> list = TweetList.loadMoreTweetbyId(max_id);

      int curSize = adapter.getItemCount();
      int i = 0;

      for (TweetList item: list) {
         Tweet tweet = new Tweet();
         User user = new User();
         user.setName(item.getName());
         user.setScreenName(item.getScreen_name());
         user.setProfileImageUrl(item.getImage_url());
         tweet.setCreateAt(item.getCreateAt());
         tweet.setBody(item.getContent());
         tweet.setUid(item.getId());
         tweet.setUser(user);
         tweet.setFavorite_cnt(item.getFavoriteCnt());
         tweet.setRetweet_cnt(item.getRetweetCnt());
         tweets.add(curSize + i, tweet);
         i++;
      }

      adapter.notifyItemRangeInserted(curSize, tweets.size() - 1);
   }

   private void loadNextDataFromApi(Long max_id) {
      client.getMoreHomeTimelinelist(max_id, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            int curSize = adapter.getItemCount();

            ArrayList <Tweet> items = Tweet.fromJSONArray(response);
            tweets.addAll(curSize, items);
            adapter.notifyItemRangeInserted(curSize, tweets.size() - 1);

            ArrayList <TweetList> tweetLists = genDBTableList(items);
            TweetList.addTimeLineList(tweetLists);
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
     return true;
   }

   public void fetchTimelineFromDB() {
      List <TweetList> list = TweetList.recentItems();
      tweets.clear();

      for (TweetList item: list) {
         Tweet tweet = new Tweet();
         User user = new User();
         user.setName(item.getName());
         user.setScreenName(item.getScreen_name());
         user.setProfileImageUrl(item.getImage_url());
         tweet.setCreateAt(item.getCreateAt());
         tweet.setBody(item.getContent());
         tweet.setUid(item.getId());
         tweet.setUser(user);
         tweet.setFavorite_cnt(item.getFavoriteCnt());
         tweet.setRetweet_cnt(item.getRetweetCnt());
         tweets.add(tweet);
      }

      adapter.notifyDataSetChanged();
      swipeContainer.setRefreshing(false);
      scrollListener.resetState();

   }

   public void fetchTimelineAsync() {
      // Send the network request to fetch the updated data
      // `client` here is an instance of Android Async HTTP
      // getHomeTimeline is an example endpoint.

      client.getHomeTimelinelist(new JsonHttpResponseHandler() {
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            // Remember to CLEAR OUT old items before appending in the new ones
            tweets.clear();
            // ...the data has come back, add new items to your adapter...
            tweets.addAll(Tweet.fromJSONArray(response));

            // Now we call setRefreshing(false) to signal refresh has finished
            adapter.notifyDataSetChanged();
            swipeContainer.setRefreshing(false);
            scrollListener.resetState();

            ArrayList <TweetList> tweetLists = genDBTableList(tweets);
            TweetList.addTimeLineList(tweetLists);
         }

         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("DEBUG", errorResponse.toString());
            swipeContainer.setRefreshing(false);
         }
      });
   }

   private ArrayList<TweetList> genDBTableList(ArrayList<Tweet> arrayList) {
      ArrayList <TweetList> tweetLists = new ArrayList<TweetList>();

      for (Tweet item: arrayList) {
         TweetList tweetListItem = new TweetList();
         tweetListItem.setName(item.getUser().getName());
         tweetListItem.setScreen_name(item.getUser().getScreenName());
         tweetListItem.setCreateAt(item.getCreateAt());
         tweetListItem.setContent(item.getBody());
         tweetListItem.setFavoriteCnt(item.getFavorite_cnt());
         tweetListItem.setRetweetCnt(item.getRetweet_cnt());
         tweetListItem.setImage_url(item.getUser().getProfileImageUrl());
         tweetListItem.setId(item.getUid());
         tweetLists.add(tweetListItem);
      }

      return tweetLists;
   }

   private Boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
   }

   @Override
   public void onPostTweet(User user, String createAt, String tweetContent) {

      Tweet tweet = new Tweet();

      tweet.setCreateAt(createAt);
      tweet.setBody(tweetContent);
      tweet.setUser(user);
      tweet.setFavorite_cnt((long) 0);
      tweet.setRetweet_cnt((long) 0);
      tweets.add(0, tweet);

      adapter.notifyDataSetChanged();
   }
}
