package com.codepath.apps.tweetsclientapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by keyulun on 2017/2/27.
 */

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
   public TweetsArrayAdapter(@NonNull Context context, @NonNull List<Tweet> tweets) {
      super(context, android.R.layout.simple_list_item_1, tweets);
   }

   @NonNull
   @Override
   public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      Tweet tweet = getItem(position);

      if (convertView == null) {
         convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
      }

      ImageView ivProfileImg = (ImageView) convertView.findViewById(R.id.ivProfileImg);
      TextView tvUserName = (TextView) convertView.findViewById(R.id.tvName);
      TextView tvContent = (TextView) convertView.findViewById(R.id.tvContent);
      TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);


      //Log.d("adapter", tweet.getBody());
      tvUserName.setText(tweet.getUser().getName());
      tvContent.setText(tweet.getBody());
      tvTime.setText(getRelativeTimeAgo(tweet.getCreateAt()));
      ivProfileImg.setImageResource(android.R.color.transparent);
      Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImg);

      return convertView;
   }

   // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
   public String getRelativeTimeAgo(String rawJsonDate) {
      String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
      SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
      sf.setLenient(true);

      String relativeDate = "";
      try {
         long dateMillis = sf.parse(rawJsonDate).getTime();
         relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                 System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
      } catch (ParseException e) {
         e.printStackTrace();
      }

      return relativeDate;
   }

}
