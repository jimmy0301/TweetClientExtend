package com.codepath.apps.tweetsclientapp.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.codepath.apps.tweetsclientapp.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TweetDetailActivity extends AppCompatActivity {
   private Toolbar toolbar;

   private ImageView ivDTProfileImage;
   private ImageView ivReply;
   private ImageView ivFavorite;
   private ImageView ivRetweet;
   private TextView tvDTName;
   private TextView tvDTScreenName;
   private TextView tvDTContent;
   private TextView tvDTFavorite;
   private TextView tvDTRetweet;
   private ReplyTweetFragment replyTweetFragment;
   private FragmentManager fm;

   private Long favorite_cnt;
   private Long retweet_cnt;
   private String name = null;
   private String screen_name = null;
   private String image_profile_url = null;

   private TwitterClient client;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_tweet_detail);
      toolbar = (Toolbar) findViewById(R.id.toolbar);
      toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_name_color));

      final Tweet tweet = getIntent().getParcelableExtra("tweet");
      String tweet_imageUrl = tweet.getUser().getProfileImageUrl();
      String tweet_name = tweet.getUser().getName();
      String tweet_screen_name = tweet.getUser().getScreenName();
      String tweet_content = tweet.getBody();
      favorite_cnt = tweet.getFavorite_cnt();
      retweet_cnt = tweet.getRetweet_cnt();

      ivDTProfileImage = (ImageView) findViewById(R.id.ivDTProfileImg);
      ivFavorite = (ImageView) findViewById(R.id.ivDTFavorite);
      ivReply = (ImageView) findViewById(R.id.ivDTReply);
      ivRetweet = (ImageView) findViewById(R.id.ivDTRetweet);
      tvDTName = (TextView) findViewById(R.id.tvDTName);
      tvDTScreenName = (TextView) findViewById(R.id.tvDTScreenName);
      tvDTContent = (TextView) findViewById(R.id.tvDTContent);
      tvDTFavorite = (TextView) findViewById(R.id.tvDTFavorite);
      tvDTRetweet = (TextView) findViewById(R.id.tvDTRetweet);

      if (tweet.isFavorited()) {
         ivFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
         ivFavorite.setTag(R.drawable.ic_favorite_red_24dp);
         tvDTFavorite.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.counterOverlow));
      } else {
         ivFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
         ivFavorite.setTag(R.drawable.ic_favorite_black_24dp);
         tvDTFavorite.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.screen_name));
      }
      tvDTFavorite.setText("" + tweet.getFavorite_cnt());

      if (tweet.isRetweeted()) {
         ivRetweet.setImageResource(R.drawable.ic_transform_green_24dp);
         ivRetweet.setTag(R.drawable.ic_transform_green_24dp);
         tvDTRetweet.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_retweet));
      } else {
         ivRetweet.setImageResource(R.drawable.ic_transform_black_24dp);
         ivRetweet.setTag(R.drawable.ic_transform_black_24dp);
         tvDTRetweet.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.screen_name));
      }

      tvDTRetweet.setText("" + tweet.getRetweet_cnt());

      if (tweet_imageUrl != null) {
         Glide.with(getApplicationContext()).load(tweet_imageUrl).into(ivDTProfileImage);
      }

      tvDTName.setText(tweet_name);
      tvDTScreenName.setText(tweet_screen_name);
      tvDTContent.setText(tweet_content);
      tvDTFavorite.setText("" + favorite_cnt);
      tvDTRetweet.setText("" + retweet_cnt);

      setSupportActionBar(toolbar);
      fm = getSupportFragmentManager();

      if (isNetworkAvailable()) {
         client = TwitterApplication.getRestClient();
         getUserInfo(tweet);

         ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               replyTweetFragment.show(fm, "reply_fragment");
            }
         });

         ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Integer id = (Integer) ivFavorite.getTag();

               handleIvFavoriteClickEvent(ivFavorite, tvDTFavorite, id, tweet.getUid());
            }
         });

         ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Integer id = (Integer) ivRetweet.getTag();

               handleIvRetweetClcikEvent(ivRetweet, tvDTRetweet, id, tweet.getUid());
            }
         });
      }
      else {
         Toast.makeText(getApplicationContext(), "There is no network. Please check it", Toast.LENGTH_LONG).show();
      }
   }

   private void getUserInfo(final Tweet tweet) {
      client.getUserProfile(new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
         try {
            name = response.getString("name");
            screen_name = response.getString("screen_name");
            image_profile_url = response.getString("profile_image_url");
            replyTweetFragment = ReplyTweetFragment.newInstance(name, screen_name, image_profile_url, tweet);

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

   private void handleIvRetweetClcikEvent(final ImageView ivRetweet, final TextView tvDTRetweet, Integer id, Long uid) {
      if (id == null)
         id = 0;

      if (id == R.drawable.ic_transform_green_24dp) {
         if (isNetworkAvailable()) {
            client.updateReTweetStatus(uid, false, new JsonHttpResponseHandler() {
               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  try {
                     long retweet_cnt = response.getLong("retweet_count");
                     ivRetweet.setImageResource(R.drawable.ic_transform_black_24dp);
                     ivRetweet.setTag(R.drawable.ic_transform_black_24dp);
                     tvDTRetweet.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.screen_name));
                     tvDTRetweet.setText("" + retweet_cnt);
                  } catch (JSONException e) {
                     e.printStackTrace();
                  }
               }

               @Override
               public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("updateRetweetStatus", errorResponse.toString());
               }
            });
         } else {
            Toast.makeText(getApplicationContext(), "There is no available network", Toast.LENGTH_LONG).show();
         }
      } else if (id == R.drawable.ic_transform_black_24dp) {
         if (isNetworkAvailable()) {
            client.updateReTweetStatus(uid, true, new JsonHttpResponseHandler() {

               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  ivRetweet.setImageResource(R.drawable.ic_transform_green_24dp);
                  ivRetweet.setTag(R.drawable.ic_transform_green_24dp);
                  try {
                     long retweet_cnt = response.getLong("retweet_count");
                     ivRetweet.setImageResource(R.drawable.ic_transform_green_24dp);
                     ivRetweet.setTag(R.drawable.ic_transform_green_24dp);
                     tvDTRetweet.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_retweet));
                     tvDTRetweet.setText("" + retweet_cnt);
                  } catch (JSONException e) {
                     e.printStackTrace();
                  }
               }

               @Override
               public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("updateRetweetStatus", errorResponse.toString());
               }
            });
         } else {
            Toast.makeText(getApplicationContext(), "There is no available network", Toast.LENGTH_LONG).show();
         }

      } else {
         ivRetweet.setImageResource(R.drawable.ic_transform_black_24dp);
         ivRetweet.setTag(R.drawable.ic_transform_black_24dp);
      }
   }

   private void handleIvFavoriteClickEvent(final ImageView ivFavorite, final TextView tvDTFavorite, Integer id, Long uid) {
      if (id == null)
         id = 0;

      if (id == R.drawable.ic_favorite_red_24dp) {
         if (isNetworkAvailable()) {
            client.updateFavoriteStatus(uid, false, new JsonHttpResponseHandler() {
               @RequiresApi(api = Build.VERSION_CODES.M)
               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  ivFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                  ivFavorite.setTag(R.drawable.ic_favorite_black_24dp);
                  try {
                     long favorite_cnt = response.getLong("favorite_count");
                     tvDTFavorite.setText("" + favorite_cnt);
                     tvDTFavorite.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.screen_name));
                  } catch (JSONException e) {
                     e.printStackTrace();
                  }
               }

               @Override
               public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("updateFavoritesStatus", errorResponse.toString());
               }
            });
         } else {
            Toast.makeText(getApplicationContext(), "There is no available network", Toast.LENGTH_LONG).show();
         }
      } else if (id == R.drawable.ic_favorite_black_24dp) {
         if (isNetworkAvailable()) {
            client.updateFavoriteStatus(uid, true, new JsonHttpResponseHandler() {
               @RequiresApi(api = Build.VERSION_CODES.M)
               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  ivFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                  ivFavorite.setTag(R.drawable.ic_favorite_red_24dp);
                  try {

                     long favorite_cnt = response.getLong("favorite_count");
                     tvDTFavorite.setText("" + favorite_cnt);

                     tvDTFavorite.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.counterOverlow));
                  } catch (JSONException e) {
                     e.printStackTrace();
                  }
               }

               @Override
               public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("updateFavoritesStatus", errorResponse.toString());
               }
            });
         } else {
            Toast.makeText(getApplicationContext(), "There is no available network", Toast.LENGTH_LONG).show();
         }

      } else {
         ivFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
         ivFavorite.setTag(R.drawable.ic_favorite_black_24dp);
      }
   }

   public Boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager
              = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
   }
}
