package com.codepath.apps.tweetsclientapp.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.applications.TwitterApplication;
import com.codepath.apps.tweetsclientapp.fragments.ReplyTweetFragment;
import com.codepath.apps.tweetsclientapp.fragments.TweetListFragment;
import com.codepath.apps.tweetsclientapp.fragments.UserTimelineFragment;
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.codepath.apps.tweetsclientapp.models.User;
import com.codepath.apps.tweetsclientapp.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity implements TweetListFragment.ReplyTweetDialogListener {

   private FragmentManager fm;
   private ReplyTweetFragment replyTweetFragment;
   private CollapsingToolbarLayout collapsingToolbar;
   private ImageView imageView;


   TwitterClient client;
   User user;

   private String name = null;
   private String screen_name = null;
   private String image_profile_url = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_profile);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
      imageView = (ImageView) findViewById(R.id.ivHeader);

      String screen_name = null;
      Tweet tweet = getIntent().getParcelableExtra("user_info");

      client = TwitterApplication.getRestClient();

      if (tweet == null) {
         if (isNetworkAvailable()) {
            screen_name = getIntent().getStringExtra("screen_name");

            client.getUserProfile(new JsonHttpResponseHandler() {
               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  user = User.fromJSON(response);
                  populateProfileHeader(user);
                  Log.d("screen_name", user.getScreenName());
                  getSupportActionBar().setTitle(user.getName());
               }
            });
         }
         else {
            Toast.makeText(getApplicationContext(), "There is no network. Please check it", Toast.LENGTH_LONG).show();
         }
      }
      else {
         screen_name = tweet.getUser().getScreenName();
         populateProfileHeader(tweet.getUser());
      }

      if (savedInstanceState == null) {
         UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screen_name);

         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

         ft.replace(R.id.flcontainer, userTimelineFragment);
         ft.commit();
      }

      fm = getSupportFragmentManager();
      getUserInfo();
   }

   private void populateProfileHeader(User user) {
      collapsingToolbar.setTitle(user.getName());
      TextView tvTagLine = (TextView) findViewById(R.id.tvTagLine);
      TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
      TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
      ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

      if (user.getTagLine() == null) {
         tvTagLine.setVisibility(View.GONE);
      }
      else {
         tvTagLine.setText(user.getTagLine());
      }
      tvFollowers.setText(user.getFollowersCount() + " followers");
      tvFollowing.setText(user.getFollowingsCount() + " following");
      Glide.with(this).load(user.getProfileBGImageUrl()).into(imageView);
      Glide.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
   }

   private void getUserInfo() {
      client.getUserProfile(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
               name = response.getString("name");
               screen_name = response.getString("screen_name");
               image_profile_url = response.getString("profile_image_url");
               //addTweetFragment = AddTweetFragment.newInstance(name, screen_name, image_profile_url);

               //getSupportActionBar().setTitle(name);

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
   public void onReplyTweet(Tweet tweet) {
      replyTweetFragment = ReplyTweetFragment.newInstance(name, screen_name, image_profile_url, tweet);
      replyTweetFragment.show(fm, "reply_fragment");
   }

   public Boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager
              = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
   }
}
