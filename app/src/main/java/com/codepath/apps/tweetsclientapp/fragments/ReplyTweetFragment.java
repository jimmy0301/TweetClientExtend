package com.codepath.apps.tweetsclientapp.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.applications.TwitterApplication;
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.codepath.apps.tweetsclientapp.models.User;
import com.codepath.apps.tweetsclientapp.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.tweetsclientapp.models.TweetList_Table.image_url;

/**
 * Created by keyulun on 2017/3/9.
 */

public class ReplyTweetFragment extends DialogFragment {
   // TODO: Rename and change types and number of parameters
   private EditText etContent;
   private TextView tvNickName;
   private TextView tvRealName;
   private ImageView ivProfile;
   private ImageView ivCancel;
   private Button btnTweet;

   private TwitterClient client;
   private Tweet tweet;
   private String name;
   private String screen_name;
   private String image_url;

   public static ReplyTweetFragment newInstance(String user_name, String screen_name, String image_url, Tweet tweet) {
      ReplyTweetFragment fragment = new ReplyTweetFragment();
      Bundle b = new Bundle();

      b.putParcelable("tweet", tweet);
      b.putString("user_name", user_name);
      b.putString("screen_name", screen_name);
      b.putString("image_url", image_url);

      fragment.setArguments(b);

      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (getArguments() != null) {
         tweet = getArguments().getParcelable("tweet");
         name = getArguments().getString("user_name");
         screen_name = getArguments().getString("screen_name");
         image_url = getArguments().getString("image_url");
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_reply_tweet, container, false);
      return view;
   }

   @SuppressWarnings("deprecation")
   @Override
   public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      tvNickName = (TextView) view.findViewById(R.id.tvNickName);
      tvRealName = (TextView) view.findViewById(R.id.tvUserName);
      ivCancel = (ImageView) view.findViewById(R.id.ivCancel);
      ivProfile = (ImageView) view.findViewById(R.id.ivUserImage);
      btnTweet = (Button) view.findViewById(R.id.btnTweet);
      etContent = (EditText) view.findViewById(R.id.etContent);


      if(tweet.getUser().getScreenName() != null) {
         String color_name = "<font color='#42A5F5'>";
         color_name = color_name + "@" + tweet.getUser().getScreenName() + "</font>";
         if (Build.VERSION.SDK_INT <= 23) {
            etContent.setText(Html.fromHtml(color_name));
         }
         else {
            etContent.setText(Html.fromHtml(color_name, Html.FROM_HTML_MODE_LEGACY));
         }

         etContent.setSelection(etContent.getText().length());
      }

      if (name != null) {
         tvRealName.setText(name);
      }

      if (screen_name != null) {
         tvNickName.setText("@" + screen_name);
      }

      if (image_url != null) {
         Glide.with(view.getContext()).load(image_url).into(ivProfile);
      }

      ivCancel.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

            dismiss();
         }
      });

      btnTweet.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            final String content = etContent.getText().toString();
            etContent.setText("");

            client = TwitterApplication.getRestClient();

            try {
               client.postTweet(content, tweet.getUid(), new JsonHttpResponseHandler() {
                  @Override
                  public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    Toast.makeText(view.getContext(), "Reply the tweet successfully", Toast.LENGTH_LONG).show();
                  }

                  @Override
                  public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                     super.onFailure(statusCode, headers, throwable, errorResponse);
                     Toast.makeText(view.getContext(), "Failed to post tweet", Toast.LENGTH_LONG).show();
                  }
               });
            } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
            }
            dismiss();
         }
      });
   }
}
