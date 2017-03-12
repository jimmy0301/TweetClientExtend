package com.codepath.apps.tweetsclientapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.applications.TwitterApplication;
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.codepath.apps.tweetsclientapp.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static java.util.Collections.addAll;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserTimelineFragment extends TweetListFragment {

   private TwitterClient client;

   public static UserTimelineFragment newInstance(String screen_name) {
      UserTimelineFragment userfragment = new UserTimelineFragment();
      Bundle args = new Bundle();
      args.putString("screen_name", screen_name);
      userfragment.setArguments(args);
      return userfragment;
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (isNetworkAvailable()) {
         client = TwitterApplication.getRestClient();
         populateTimeline();
      }
      else {
         Toast.makeText(getActivity(), "There is no network", Toast.LENGTH_LONG).show();
      }
   }

   //Fill in list view
   private void populateTimeline() {
      String screenName = getArguments().getString("screen_name");
      client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

            addAll(Tweet.fromJSONArray(response));
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.d("DEBUG", errorResponse.toString());
         }
      });
   }

   @Override
   public void loadNextDataFromApi(Long maxId) {
      String screen_name = getArguments().getString("screen_name");
      client.getMoreUserTimeline(screen_name, maxId, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            addMore(Tweet.fromJSONArray(response));
         }
      });
   }

   @Override
   public void fetchTimelineAsync() {
      clear();
      populateTimeline();
   }
}
