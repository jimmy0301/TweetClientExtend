package com.codepath.apps.tweetsclientapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.tweetsclientapp.applications.TwitterApplication;
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.codepath.apps.tweetsclientapp.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class MentionsFragment extends TweetListFragment {

   private TwitterClient client;

   public MentionsFragment() {
      // Required empty public constructor
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (isNetworkAvailable()) {
         client = TwitterApplication.getRestClient();
         populateMentionsTimeline();
      }
      else {
         Toast.makeText(getActivity(), "There is no network", Toast.LENGTH_LONG).show();
      }
   }

   //Fill in list view
   private void populateMentionsTimeline() {
      client.getMentionsTimeline(new JsonHttpResponseHandler() {
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
      client.getMoreMentionsTimeline(maxId, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            addMore(Tweet.fromJSONArray(response));
         }
      });
   }

   @Override
   public void fetchTimelineAsync() {
      clear();
      populateMentionsTimeline();
   }
}
