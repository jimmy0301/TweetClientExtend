package com.codepath.apps.tweetsclientapp.activities;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.models.Tweet;

import static android.R.string.yes;
import static com.raizlabs.android.dbflow.config.FlowLog.Level.I;

public class TweetDetailActivity extends AppCompatActivity {
   private Toolbar toolbar;

   private ImageView ivDTProfileImage;
   private TextView tvDTName;
   private TextView tvDTScreenName;
   private TextView tvDTContent;
   private TextView tvDTFavorite;
   private TextView tvDTRetweet;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_tweet_detail);
      toolbar = (Toolbar) findViewById(R.id.toolbar);
      toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_name_color));

      Bundle b = getIntent().getBundleExtra("itemInfo");
      String login_user_name = b.getString("login_user_name");

      toolbar.setTitle(login_user_name);
      Tweet tweet = b.getParcelable("tweet");
      String imageUrl = tweet.getUser().getProfileImageUrl();
      String name = tweet.getUser().getName();
      String screen_name = tweet.getUser().getScreenName();
      String content = tweet.getBody();
      Long favorite_cnt = tweet.getFavorite_cnt();
      Long retweet_cnt = tweet.getRetweet_cnt();

      ivDTProfileImage = (ImageView) findViewById(R.id.ivDTProfileImg);
      tvDTName = (TextView) findViewById(R.id.tvDTName);
      tvDTScreenName = (TextView) findViewById(R.id.tvDTScreenName);
      tvDTContent = (TextView) findViewById(R.id.tvDTContent);
      tvDTFavorite = (TextView) findViewById(R.id.tvDTFavorite);
      tvDTRetweet = (TextView) findViewById(R.id.tvDTRetweet);

      if (imageUrl != null) {
         Glide.with(getApplicationContext()).load(imageUrl).into(ivDTProfileImage);
      }

      tvDTName.setText(name);
      tvDTScreenName.setText(screen_name);
      tvDTContent.setText(content);
      tvDTFavorite.setText("Favorite Count: " + favorite_cnt);
      tvDTRetweet.setText("Retweet Count: " + retweet_cnt);

      setSupportActionBar(toolbar);
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
}
