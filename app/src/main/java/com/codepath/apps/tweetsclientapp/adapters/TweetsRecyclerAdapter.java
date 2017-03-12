package com.codepath.apps.tweetsclientapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.libs.PatternEditableBuilder;
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.codepath.apps.tweetsclientapp.models.ViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

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
      viewHolder = new ViewHolder(view, onRecyclerViewItemClickListener);

      view.setOnClickListener(this);
      return viewHolder;
   }

   @RequiresApi(api = Build.VERSION_CODES.M)
   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      ViewHolder viewHolder = (ViewHolder) holder;

      configureViewHolder(viewHolder, position);
   }

   private void configureViewHolder(final ViewHolder viewHolder, int position) {
      Tweet tweet = mTweets.get(position);
      Long favorite_cnt = tweet.getFavorite_cnt();
      Long retweet_cnt = tweet.getRetweet_cnt();
      if (tweet != null) {
         viewHolder.getTvContent().setText(tweet.getBody());
         viewHolder.getTvName().setText(tweet.getUser().getName());
         viewHolder.getTvScreenName().setText("@" + tweet.getUser().getScreenName());
         viewHolder.getTvTime().setText(getRelativeTimeAgo(tweet.getCreateAt()));
         viewHolder.getImageView().setImageResource(0);
         if (tweet.isFavorited()) {
            viewHolder.getIvFavorite().setImageResource(R.drawable.ic_favorite_red_24dp);
            viewHolder.getIvFavorite().setTag(R.drawable.ic_favorite_red_24dp);
            viewHolder.getTvFavorite().setTextColor(ContextCompat.getColor(getContext(), R.color.counterOverlow));
         }
         else {
            viewHolder.getIvFavorite().setImageResource(R.drawable.ic_favorite_black_24dp);
            viewHolder.getIvFavorite().setTag(R.drawable.ic_favorite_black_24dp);
            viewHolder.getTvFavorite().setTextColor(ContextCompat.getColor(getContext(), R.color.screen_name));
         }

         if (tweet.isRetweeted()) {
            viewHolder.getIvRetweet().setImageResource(R.drawable.ic_transform_green_24dp);
            viewHolder.getIvRetweet().setTag(R.drawable.ic_transform_green_24dp);
            viewHolder.getTvRetweet().setTextColor(ContextCompat.getColor(getContext(), R.color.color_retweet));
         }
         else {
            viewHolder.getIvRetweet().setImageResource(R.drawable.ic_transform_black_24dp);
            viewHolder.getIvRetweet().setTag(R.drawable.ic_transform_black_24dp);
            viewHolder.getTvRetweet().setTextColor(ContextCompat.getColor(getContext(), R.color.screen_name));
         }

         new PatternEditableBuilder().
                 addPattern(Pattern.compile("((\\@(\\w+))|(\\#(\\w+)))"), Color.BLUE,
                         new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                               Toast.makeText(getContext(), "Click the tag: " + text, Toast.LENGTH_LONG).show();
                            }
                         }).into(viewHolder.getTvContent());

         viewHolder.getTvFavorite().setText("" + favorite_cnt);
         viewHolder.getTvRetweet().setText("" + retweet_cnt);
         Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.getImageView());
      }

      viewHolder.itemView.setTag(tweet);
   }

   public Tweet getItem(int posision) {
      return mTweets.get(posision);
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

   public interface OnRecyclerViewItemClickListener {
      void onItemClick(View view, Tweet tweet);
      void onViewClick(View v, TextView tvView, int position);
   }

   public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
      this.onRecyclerViewItemClickListener = listener;
   }
}
