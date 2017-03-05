package com.codepath.apps.tweetsclientapp.networks;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.io.UnsupportedEncodingException;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
   public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
   public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
   public static final String REST_CONSUMER_KEY = "Od0YuudvMx8kExlsOMwN5r36h";       // Change this
   public static final String REST_CONSUMER_SECRET = "v16nWHTWrDtlMYNZh1NSwglqcqDLzXZKbsVzgLuFEFC6f6XNo2"; // Change this
   public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

   public TwitterClient(Context context) {
      super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
   }

   public void getHomeTimelinelist(AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("statuses/home_timeline.json");
      RequestParams params = new RequestParams();
      params.put("count", "10");
      params.put("since_id", 1);
      getClient().get(apiUrl, params, handler);
   }

   public void getUserProfile(AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("account/verify_credentials.json");
      getClient().get(apiUrl, handler);
   }

   public void postTweet(String content, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
      String apiUrl = getApiUrl("statuses/update.json");
      RequestParams params = new RequestParams();
      params.put("status", content);
      getClient().post(apiUrl, params, handler);
   }

   public void getMoreHomeTimelinelist(Long max_id, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("statuses/home_timeline.json");
      // Can specify query string params directly or through RequestParams.
      RequestParams params = new RequestParams();
      params.put("count", "10");
      params.put("max_id", max_id);
      Log.d("query string", params.toString());
      getClient().get(apiUrl, params, handler);
   }
}
