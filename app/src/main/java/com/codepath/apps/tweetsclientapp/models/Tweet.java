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

/*
"text": "just another test",
    "contributors": null,
    "id": 240558470661799936,
    "retweet_count": 0,
    "in_reply_to_status_id_str": null,
    "geo": null,
    "retweeted": false,
    "in_reply_to_user_id": null,
    "place": null,
    "source": "OAuth Dancer Reborn",
    "user": {
      "name": "OAuth Dancer",
      "profile_sidebar_fill_color": "DDEEF6",
      "profile_background_tile": true,
      "profile_sidebar_border_color": "C0DEED",
      "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
      "created_at": "Wed Mar 03 19:37:35 +0000 2010",
      "location": "San Francisco, CA",
      "follow_request_sent": false,
      "id_str": "119476949",
      "is_translator": false,
      "profile_link_color": "0084B4",
      "entities": {
        "url": {
          "urls": [
            {
              "expanded_url": null,
              "url": "http://bit.ly/oauth-dancer",
              "indices": [
                0,
                26
              ],
              "display_url": null
            }
          ]
        },
        "description": null
      },
*/

// Parse the Json = store the data
public class Tweet implements Parcelable {
   private Long uid;
   private String body;
   private User user;
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

   public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
      Tweet tweet = new Tweet();

      try {
         tweet.uid = jsonObject.getLong("id");
         tweet.body = jsonObject.getString("text");
         tweet.createAt = jsonObject.getString("created_at");
         tweet.user = User.formJSON(jsonObject.getJSONObject("user"));
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

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeLong(this.uid);
      dest.writeString(this.body);
      dest.writeParcelable(this.user, flags);
      dest.writeString(this.createAt);
   }

   public Tweet() {
   }

   protected Tweet(Parcel in) {
      this.uid = in.readLong();
      this.body = in.readString();
      this.user = in.readParcelable(User.class.getClassLoader());
      this.createAt = in.readString();
   }

   public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
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
