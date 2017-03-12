package com.codepath.apps.tweetsclientapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.applications.TwitterApplication;
import com.codepath.apps.tweetsclientapp.fragments.AddTweetFragment;
import com.codepath.apps.tweetsclientapp.fragments.HomeTimelineFragment;
import com.codepath.apps.tweetsclientapp.fragments.MentionsFragment;
import com.codepath.apps.tweetsclientapp.fragments.ReplyTweetFragment;
import com.codepath.apps.tweetsclientapp.fragments.TweetListFragment;
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.codepath.apps.tweetsclientapp.models.User;
import com.codepath.apps.tweetsclientapp.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements AddTweetFragment.PostTweetDialogListener, TweetListFragment.ReplyTweetDialogListener {

   private Toolbar toolbar;
   private ViewPager vpPager;
   private PagerSlidingTabStrip tabStrip;
   private FloatingActionButton floatingActionButton;

   private TwitterClient client;
   private FragmentManager fm;
   private AddTweetFragment addTweetFragment;
   private ReplyTweetFragment replyTweetFragment;

   private String name = null;
   private String screen_name = null;
   private String image_profile_url = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_timeline);
      toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      vpPager = (ViewPager) findViewById(R.id.viewpager);

      vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));

      tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

      tabStrip.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

      tabStrip.setIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
      tabStrip.setBackgroundColor(Color.WHITE);

      tabStrip.setViewPager(vpPager);

      floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

      fm = getSupportFragmentManager();

      if (isNetworkAvailable()) {
         client = TwitterApplication.getRestClient();
         getUserInfo();
         setupFABListener();
      }
      else {
         Toast.makeText(getApplicationContext(), "There is no network. Please check it", Toast.LENGTH_LONG).show();
      }
   }

   private void setupFABListener() {
      floatingActionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            addTweetFragment.show(fm, "fragment_add_tweet");
         }
      });
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_timeline, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      return super.onOptionsItemSelected(item);
   }

   public void onProfileView(MenuItem mi) {
      Intent i = new Intent(this, ProfileActivity.class);
      startActivity(i);
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

   @Override
   public void onPostTweet(User user, String createAt, String tweetContent) {
      int index = vpPager.getCurrentItem();
      TweetsPagerAdapter adapter = (TweetsPagerAdapter) vpPager.getAdapter();
      Fragment fragment = adapter.getFragment(index);
      if (index == 0) {
         HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) fragment;
         Tweet tweet = new Tweet();
         tweet.setCreateAt(createAt);
         tweet.setBody(tweetContent);
         tweet.setUser(user);
         Log.d("tweet content", tweet.getBody());
         homeTimelineFragment.addNewTweet(tweet);
      }
      else if (index == 1) {
         MentionsFragment mentionsFragment = (MentionsFragment) fragment;
         Tweet tweet = new Tweet();
         tweet.setCreateAt(createAt);
         tweet.setBody(tweetContent);
         tweet.setUser(user);
         mentionsFragment.addNewTweet(tweet);
      }
   }

   @Override
   public void onReplyTweet(Tweet tweet) {
      replyTweetFragment = ReplyTweetFragment.newInstance(name, screen_name, image_profile_url, tweet);
      replyTweetFragment.show(fm, "reply_fragment");
   }

   public class TweetsPagerAdapter extends FragmentPagerAdapter {
      public String tabTitle[] = new String[] {"Home", "Mentions"};
      private ArrayList<Fragment> pageReferenceMap;

      public TweetsPagerAdapter(FragmentManager fm) {
         super(fm);
         pageReferenceMap = new ArrayList<>();
      }

      @Override
      public Fragment getItem(int position) {
         if (position == 0) {
            HomeTimelineFragment homeTimelineFragment = new HomeTimelineFragment();
            pageReferenceMap.add(position, homeTimelineFragment);
            return homeTimelineFragment;
         }
         else if (position == 1) {
            MentionsFragment mentionsFragment = new MentionsFragment();
            pageReferenceMap.add(position, mentionsFragment);
            return mentionsFragment;
         }
         else
            return null;
      }

      public Fragment getFragment(int key) {
         return pageReferenceMap.get(key);
      }

      @Override
      public CharSequence getPageTitle(int position) {
         return tabTitle[position];
      }

      @Override
      public int getCount() {
         return tabTitle.length;
      }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
         super.destroyItem(container, position, object);
         pageReferenceMap.remove(position);
      }
   }

   public Boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager
              = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
   }
}
