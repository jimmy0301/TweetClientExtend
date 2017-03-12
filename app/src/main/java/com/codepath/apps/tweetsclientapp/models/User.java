package com.codepath.apps.tweetsclientapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by keyulun on 2017/2/27.
 */

public class User implements Parcelable {
   private String name;
   private long uid;
   private String screenName;
   private String profileImageUrl;
   private String tagLine;
   private String profileBGImageUrl;
   private int followersCount;
   private int followingsCount;

   public void setName(String name) {
      this.name = name;
   }

   public void setUid(long uid) {
      this.uid = uid;
   }

   public void setScreenName(String screenName) {
      this.screenName = screenName;
   }

   public void setProfileImageUrl(String profileImageUrl) {
      this.profileImageUrl = profileImageUrl;
   }

   public static User fromJSON(JSONObject jsonObject)  {
      User user = new User();

      try {
         user.name = jsonObject.getString("name");
         user.uid = jsonObject.getLong("id");
         user.screenName = jsonObject.getString("screen_name");
         user.profileImageUrl = jsonObject.getString("profile_image_url");
         user.profileBGImageUrl = jsonObject.getString("profile_background_image_url");
         user.tagLine = jsonObject.getString("description");
         user.followersCount = jsonObject.getInt("followers_count");
         user.followingsCount = jsonObject.getInt("friends_count");
      } catch (JSONException e) {
         e.printStackTrace();
      }

      return user;
   }

   public String getName() {
      return name;
   }

   public long getUid() {
      return uid;
   }

   public String getScreenName() {
      return screenName;
   }

   public String getProfileImageUrl() {
      return profileImageUrl;
   }

   public String getTagLine() {
      return tagLine;
   }

   public int getFollowersCount() {
      return followersCount;
   }

   public int getFollowingsCount() {
      return followingsCount;
   }

   public String getProfileBGImageUrl() {
      return profileBGImageUrl;
   }

   public void setProfileBGImageUrl(String profileBGImageUrl) {
      this.profileBGImageUrl = profileBGImageUrl;
   }

   public User() {
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.name);
      dest.writeLong(this.uid);
      dest.writeString(this.screenName);
      dest.writeString(this.profileImageUrl);
      dest.writeString(this.tagLine);
      dest.writeString(this.profileBGImageUrl);
      dest.writeInt(this.followersCount);
      dest.writeInt(this.followingsCount);
   }

   protected User(Parcel in) {
      this.name = in.readString();
      this.uid = in.readLong();
      this.screenName = in.readString();
      this.profileImageUrl = in.readString();
      this.tagLine = in.readString();
      this.profileBGImageUrl = in.readString();
      this.followersCount = in.readInt();
      this.followingsCount = in.readInt();
   }

   public static final Creator<User> CREATOR = new Creator<User>() {
      @Override
      public User createFromParcel(Parcel source) {
         return new User(source);
      }

      @Override
      public User[] newArray(int size) {
         return new User[size];
      }
   };
}
