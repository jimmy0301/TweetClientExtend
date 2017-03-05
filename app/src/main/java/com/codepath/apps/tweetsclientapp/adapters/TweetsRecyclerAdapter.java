package com.codepath.apps.tweetsclientapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.codepath.apps.tweetsclientapp.models.ViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by keyulun on 2017/2/27.
 */

public class TweetsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
   private List<Tweet> mTweets;
   private Context mContext;
   private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener = null;


   public TweetsRecyclerAdapter(Context context, List<Tweet> tweets) {
      mTweets = tweets;
      mContext = context;
   }


   public Long getLastItemId() {
      return mTweets.get(mTweets.size() - 1).getUid();
   }

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      RecyclerView.ViewHolder viewHolder;
      LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

      View view = layoutInflater.inflate(R.layout.item_tweet, parent, false);
      viewHolder = new ViewHolder(view);

      view.setOnClickListener(this);
      return viewHolder;
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      ViewHolder viewHolder = (ViewHolder) holder;
      configureViewHolder(viewHolder, position);
   }

   private void configureViewHolder(ViewHolder viewHolder, int position) {
      Tweet tweet = mTweets.get(position);
      if (tweet != null) {
         viewHolder.getTvContent().setText(tweet.getBody());
         viewHolder.getTvName().setText(tweet.getUser().getName());
         viewHolder.getTvScreenName().setText("@" + tweet.getUser().getScreenName());
         viewHolder.getTvTime().setText(getRelativeTimeAgo(tweet.getCreateAt()));
         viewHolder.getImageView().setImageResource(0);
         Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.getImageView());
      }

      viewHolder.itemView.setTag(tweet);
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

   private Context getContext() {
      return mContext;
   }

   @Override
   public int getItemCount() {
      return mTweets.size();
   }

   @Override
   public void onClick(View v) {
      if (onRecyclerViewItemClickListener != null) {
         onRecyclerViewItemClickListener.onItemClick(v, (Tweet)v.getTag());
      }
   }

   public static interface OnRecyclerViewItemClickListener {
      void onItemClick(View view, Tweet tweet);
   }

   public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
      this.onRecyclerViewItemClickListener = listener;
   }
}
