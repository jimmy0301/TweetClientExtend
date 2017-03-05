package com.codepath.apps.tweetsclientapp.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.tweetsclientapp.R;

/**
 * Created by keyulun on 2017/2/27.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
   private ImageView imageView;
   private TextView tvName;
   private TextView tvScreenName;
   private TextView tvContent;
   private TextView tvTime;

   public ViewHolder(View itemView) {
      super(itemView);

      imageView = (ImageView) itemView.findViewById(R.id.ivProfileImg);
      tvName = (TextView) itemView.findViewById(R.id.tvName);
      tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
      tvContent = (TextView) itemView.findViewById(R.id.tvContent);
      tvTime = (TextView) itemView.findViewById(R.id.tvTime);
   }

   public ImageView getImageView() {
      return imageView;
   }

   public TextView getTvName() {
      return tvName;
   }

   public TextView getTvScreenName() {
      return tvScreenName;
   }

   public TextView getTvContent() {
      return tvContent;
   }

   public TextView getTvTime() {
      return tvTime;
   }
}
