package com.codepath.apps.tweetsclientapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by keyulun on 2017/2/27.
 */

public class Tweet implements Parcelable {
   private Long uid;
   private String body;
   private User user;
   private Long favorite_cnt;
   private Long retweet_cnt;
   private boolean favorited;
   private boolean retweeted;
   private String createAt;

   public Long getUid() {
      return uid;
   }

   public String getBody() {
      return body;
   }

   public User getUser() {
      return user;
   }

   public String getCreateAt() {
      return createAt;
   }

   public Long getFavorite_cnt() {
      return favorite_cnt;
   }

   public Long getRetweet_cnt() {
      return retweet_cnt;
   }

   public boolean isFavorited() {
      return favorited;
   }

   public boolean isRetweeted() {
      return retweeted;
   }

   public void setUid(Long uid) {
      this.uid = uid;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public void setCreateAt(String createAt) {
      this.createAt = createAt;
   }

   public void setFavorite_cnt(Long favorite_cnt) {
      this.favorite_cnt = favorite_cnt;
   }

   public void setRetweet_cnt(Long retweet_cnt) {
      this.retweet_cnt = retweet_cnt;
   }

   public void setFavorited(boolean favorited) {
      this.favorited = favorited;
   }

   public void setRetweeted(boolean retweeted) {
      this.retweeted = retweeted;
   }

   public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
      Tweet tweet = new Tweet();

      try {
         tweet.uid = Long.parseLong(jsonObject.getString("id_str"));
         tweet.favorite_cnt = jsonObject.getLong("favorite_count");
         tweet.retweet_cnt = jsonObject.getLong("retweet_count");
         tweet.body = jsonObject.getString("text");
         tweet.createAt = jsonObject.getString("created_at");
         tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
         tweet.favorited = jsonObject.getBoolean("favorited");
         tweet.retweeted = jsonObject.getBoolean("retweeted");

      } catch (JSONException e) {
         e.printStackTrace();
      }
      return tweet;
   }

   public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
      ArrayList<Tweet> arrayList = new ArrayList<>();

      for (int i = 0; i < jsonArray.length(); i++) {
         try {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Tweet tweet = Tweet.fromJSON(jsonObject);
            if (tweet != null) {
               arrayList.add(tweet);
            }
         } catch (JSONException e) {
            e.printStackTrace();
            break;
         }
      }
      return arrayList;
   }

   public Tweet() {
   }


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeValue(this.uid);
      dest.writeString(this.body);
      dest.writeParcelable(this.user, flags);
      dest.writeValue(this.favorite_cnt);
      dest.writeValue(this.retweet_cnt);
      dest.writeByte(this.favorited ? (byte) 1 : (byte) 0);
      dest.writeByte(this.retweeted ? (byte) 1 : (byte) 0);
      dest.writeString(this.createAt);
   }

   protected Tweet(Parcel in) {
      this.uid = (Long) in.readValue(Long.class.getClassLoader());
      this.body = in.readString();
      this.user = in.readParcelable(User.class.getClassLoader());
      this.favorite_cnt = (Long) in.readValue(Long.class.getClassLoader());
      this.retweet_cnt = (Long) in.readValue(Long.class.getClassLoader());
      this.favorited = in.readByte() != 0;
      this.retweeted = in.readByte() != 0;
      this.createAt = in.readString();
   }

   public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
      @Override
      public Tweet createFromParcel(Parcel source) {
         return new Tweet(source);
      }

      @Override
      public Tweet[] newArray(int size) {
         return new Tweet[size];
      }
   };
}
