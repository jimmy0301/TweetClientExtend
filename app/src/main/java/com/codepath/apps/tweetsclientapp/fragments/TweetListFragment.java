package com.codepath.apps.tweetsclientapp.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweetsclientapp.R;
import com.codepath.apps.tweetsclientapp.activities.ProfileActivity;
import com.codepath.apps.tweetsclientapp.activities.TweetDetailActivity;
import com.codepath.apps.tweetsclientapp.adapters.TweetsRecyclerAdapter;
import com.codepath.apps.tweetsclientapp.applications.TwitterApplication;
import com.codepath.apps.tweetsclientapp.libs.DividerItemDecoration;
import com.codepath.apps.tweetsclientapp.libs.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweetsclientapp.models.Tweet;
import com.codepath.apps.tweetsclientapp.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.tweetsclientapp.R.id.ivFavorite;
import static com.codepath.apps.tweetsclientapp.R.id.ivRetweet;

/**
 * A simple {@link Fragment} subclass.
 */
public class TweetListFragment extends Fragment {

   private EndlessRecyclerViewScrollListener scrollListener;
   private TwitterClient client;

   private RecyclerView rvTweets;
   private SwipeRefreshLayout swipeContainer;


   private ArrayList<Tweet> tweets;
   private TweetsRecyclerAdapter adapter;

   public TweetListFragment() {
      // Required empty public constructor
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                            Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View v = inflater.inflate(R.layout.fragment_tweet_list, parent, false);
      rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
      rvTweets.setAdapter(adapter);

      swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
      linearLayoutManager.scrollToPosition(0);
      rvTweets.setLayoutManager(linearLayoutManager);

      rvTweets.setHasFixedSize(true);

      RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
      rvTweets.addItemDecoration(itemDecoration);
      swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
              android.R.color.holo_green_light,
              android.R.color.holo_orange_light,
              android.R.color.holo_red_light);

      setupRVListener(linearLayoutManager);
      setupSRListener();

      return v;
   }

   private void setupSRListener() {
      swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
            if (isNetworkAvailable()) {
               fetchTimelineAsync();
               swipeContainer.setRefreshing(false);
               scrollListener.resetState();
            }
            else {
               Toast.makeText(getActivity(), "There is no network", Toast.LENGTH_LONG).show();
            }
         }
      });
   }

   public void fetchTimelineAsync() {
   }

   private void setupRVListener(LinearLayoutManager linearLayoutManager) {
      scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
         @Override
         public void onLoadMore(Long maxId, int totalItemsCount) {
            if (isNetworkAvailable()) {
               loadNextDataFromApi(maxId);
            }
            else {
               Toast.makeText(getContext(), "There is no available network", Toast.LENGTH_LONG).show();
            }
         }
      };

      rvTweets.addOnScrollListener(scrollListener);
   }

   public void loadNextDataFromApi(Long maxId) {
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      tweets = new ArrayList<>();
      adapter = new TweetsRecyclerAdapter(getActivity(), tweets);

      adapter.setOnItemClickListener(new TweetsRecyclerAdapter.OnRecyclerViewItemClickListener() {
         @Override
         public void onItemClick(View view, Tweet tweet) {
            Intent i = new Intent(getActivity(), TweetDetailActivity.class);
            i.putExtra("tweet", tweet);

            startActivity(i);
         }

         @Override
         public void onViewClick(View v, TextView tvText, int position) {
            if (isNetworkAvailable()) {

               client = TwitterApplication.getRestClient();
               if (v.getId() == ivFavorite) {
                  Integer id = (Integer) v.getTag();
                  ImageView ivFavorite = (ImageView) v;
                  long tweet_id = adapter.getItem(position).getUid();

                  handleIvFavoriteClickEvent(ivFavorite, tvText, id, tweet_id, position);

               } else if (v.getId() == R.id.ivProfileImg) {
                  Intent i = new Intent(getActivity(), ProfileActivity.class);
                  i.putExtra("user_info", adapter.getItem(position));
                  startActivity(i);
               } else if (v.getId() == R.id.ivReply) {
                  ReplyTweetDialogListener listener = (ReplyTweetDialogListener) getActivity();

                  listener.onReplyTweet(adapter.getItem(position));
               } else if (v.getId() == ivRetweet) {
                  Integer id = (Integer) v.getTag();
                  ImageView ivRetweet = (ImageView) v;
                  long tweet_id = adapter.getItem(position).getUid();
                  handleIvRetweetClcikEvent(ivRetweet, tvText, id, tweet_id, position);
               }
            } else {

               Toast.makeText(getActivity(), "There is no network", Toast.LENGTH_LONG).show();
            }
         }
      });
   }

   private void handleIvRetweetClcikEvent(final ImageView ivRetweet, final TextView tvText, Integer id, long tweet_id, final int position) {
      if (id == null)
         id = 0;

      if (id == R.drawable.ic_transform_green_24dp) {
         if (isNetworkAvailable()) {
            client.updateReTweetStatus(tweet_id, false, new JsonHttpResponseHandler() {
               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  try {
                     long retweet_cnt = response.getLong("retweet_count");
                     adapter.getItem(position).setRetweeted(false);
                     adapter.getItem(position).setRetweet_cnt(retweet_cnt);
                     adapter.notifyItemChanged(position);
                  } catch (JSONException e) {
                     e.printStackTrace();
                  }
               }

               @Override
               public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("updateRetweetStatus", errorResponse.toString());
               }
            });
         }
         else {
            Toast.makeText(getContext(), "There is no available network", Toast.LENGTH_LONG).show();
         }
      }
      else if (id == R.drawable.ic_transform_black_24dp) {
         if (isNetworkAvailable()) {
            client.updateReTweetStatus(tweet_id, true, new JsonHttpResponseHandler() {

               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  ivRetweet.setImageResource(R.drawable.ic_transform_green_24dp);
                  ivRetweet.setTag(R.drawable.ic_transform_green_24dp);
                  try {
                     long retweet_cnt = response.getLong("retweet_count");
                     adapter.getItem(position).setRetweeted(true);
                     adapter.getItem(position).setRetweet_cnt(retweet_cnt);
                     adapter.notifyItemChanged(position);
                  } catch (JSONException e) {
                     e.printStackTrace();
                  }
               }

               @Override
               public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("updateRetweetStatus", errorResponse.toString());
               }
            });
         }
         else {
            Toast.makeText(getContext(), "There is no available network", Toast.LENGTH_LONG).show();
         }

      }
      else {
         ivRetweet.setImageResource(R.drawable.ic_transform_black_24dp);
         ivRetweet.setTag(R.drawable.ic_transform_black_24dp);
      }
   }


   private void handleIvFavoriteClickEvent(final ImageView ivFavorite, final TextView tvText,
                                           Integer id, long tweet_id, final int position) {
      if (id == null)
         id = 0;

      if (id == R.drawable.ic_favorite_red_24dp) {
         if (isNetworkAvailable()) {
            client.updateFavoriteStatus(tweet_id, false, new JsonHttpResponseHandler() {
               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                  try {
                     long favorite_cnt = response.getLong("favorite_count");
                     adapter.getItem(position).setFavorited(false);
                     adapter.getItem(position).setFavorite_cnt(favorite_cnt);
                     adapter.notifyItemChanged(position);
                  } catch (JSONException e) {
                     e.printStackTrace();
                  }
               }

               @Override
               public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("updateFavoritesStatus", errorResponse.toString());
               }
            });
         }
         else {
            Toast.makeText(getContext(), "There is no available network", Toast.LENGTH_LONG).show();
         }
      }
      else if (id == R.drawable.ic_favorite_black_24dp) {
         if (isNetworkAvailable()) {
            client.updateFavoriteStatus(tweet_id, true, new JsonHttpResponseHandler() {
               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  try {

                     long favorite_cnt = response.getLong("favorite_count");
                     adapter.getItem(position).setFavorited(true);
                     adapter.getItem(position).setFavorite_cnt(favorite_cnt);
                     adapter.notifyItemChanged(position);
                  } catch (JSONException e) {
                     e.printStackTrace();
                  }
               }

               @Override
               public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("updateFavoritesStatus", errorResponse.toString());
               }
            });
         }
         else {
            Toast.makeText(getContext(), "There is no available network", Toast.LENGTH_LONG).show();
         }

      }
      else {
         ivFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
         ivFavorite.setTag(R.drawable.ic_favorite_black_24dp);
      }
   }

   public void clear() {
      tweets.clear();
   }

   public void addNewTweet(Tweet tweet) {
      tweets.add(0, tweet);
      adapter.notifyDataSetChanged();
   }

   public void addMore(List<Tweet> list) {
      int curSize = adapter.getItemCount();
      tweets.addAll(curSize, list);
      adapter.notifyItemRangeInserted(curSize, list.size() -1);
   }

   public void addAll(List<Tweet> list) {
      tweets.addAll(list);
      adapter.notifyDataSetChanged();
   }

   public Boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager
              = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
   }

   public interface ReplyTweetDialogListener {
      // TODO: Update argument type and name
      void onReplyTweet(Tweet tweet);
   }
}
