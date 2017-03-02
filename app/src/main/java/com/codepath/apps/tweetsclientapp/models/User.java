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


   public static User formJSON(JSONObject jsonObject)  {
      User user = new User();

      try {
         user.name = jsonObject.getString("name");
         user.uid = jsonObject.getLong("id");
         user.screenName = jsonObject.getString("screen_name");
         user.profileImageUrl = jsonObject.getString("profile_image_url");
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
   }

   public User() {
   }

   protected User(Parcel in) {
      this.name = in.readString();
      this.uid = in.readLong();
      this.screenName = in.readString();
      this.profileImageUrl = in.readString();
   }

   public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
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
