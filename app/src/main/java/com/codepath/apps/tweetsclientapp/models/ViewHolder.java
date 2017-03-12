package com.codepath.apps.tweetsclientapp.models;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.adapters.TweetsRecyclerAdapter;
import com.codepath.apps.tweetsclientapp.libs.PatternEditableBuilder;

import java.util.regex.Pattern;

/**
 * Created by keyulun on 2017/2/27.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
   private ImageView imageView;
   private ImageView ivFavorite;
   private ImageView ivReply;
   private ImageView ivRetweet;
   private TextView tvName;
   private TextView tvScreenName;
   private TextView tvContent;
   private TextView tvTime;
   private TextView tvFavorite;
   private TextView tvRetweet;

   public ViewHolder(View itemView, final TweetsRecyclerAdapter.OnRecyclerViewItemClickListener listener) {
      super(itemView);

      imageView = (ImageView) itemView.findViewById(R.id.ivProfileImg);
      ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
      ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);
      ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
      tvName = (TextView) itemView.findViewById(R.id.tvName);
      tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
      tvContent = (TextView) itemView.findViewById(R.id.tvContent);
      tvTime = (TextView) itemView.findViewById(R.id.tvTime);
      tvFavorite = (TextView) itemView.findViewById(R.id.tvFavorite);
      tvRetweet = (TextView) itemView.findViewById(R.id.tvRetweet);

      ivFavorite.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (listener != null) {
               listener.onViewClick(v, tvFavorite, getAdapterPosition());
            }
         }
      });

      imageView.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {
            if (listener != null) {
               listener.onViewClick(v, null, getAdapterPosition());
            }
         }
      });

      ivReply.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (listener != null) {
               listener.onViewClick(v, null, getAdapterPosition());
            }
         }
      });

      ivRetweet.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (listener != null) {
               listener.onViewClick(v, tvRetweet, getAdapterPosition());
            }
         }
      });
   }

   public ImageView getImageView() {
      return imageView;
   }

   public ImageView getIvFavorite() {
      return ivFavorite;
   }

   public ImageView getIvReply() {
      return ivReply;
   }

   public ImageView getIvRetweet() {
      return ivRetweet;
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

   public TextView getTvFavorite() {
      return tvFavorite;
   }

   public TextView getTvRetweet() {
      return tvRetweet;
   }
}
