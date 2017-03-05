package com.codepath.apps.tweetsclientapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import com.codepath.apps.tweetsclientapp.models.TweetList;
import com.codepath.apps.tweetsclientapp.models.User;
import com.codepath.apps.tweetsclientapp.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddTweetFragment.PostTweetDialogListener} interface
 * to handle interaction events.
 * Use the {@link AddTweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTweetFragment extends DialogFragment {
   // TODO: Rename parameter arguments, choose names that match
   // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

   private EditText etContent;
   private TextView tvNickName;
   private TextView tvRealName;
   private ImageView ivProfile;
   private ImageView ivCancel;
   private Button btnTweet;

   private TwitterClient client;

   // TODO: Rename and change types of parameters
   private String name;
   private String screen_name;
   private String image_url;

   public AddTweetFragment() {
      // Required empty public constructor
   }

   /**
    * Use this factory method to create a new instance of
    * this fragment using the provided parameters.
    *
    *
    * @return A new instance of fragment AddTweetFragment.
    */
   // TODO: Rename and change types and number of parameters
   public static AddTweetFragment newInstance(String name, String screen_name, String image_url) {
      AddTweetFragment fragment = new AddTweetFragment();
      Bundle b = new Bundle();

      b.putString("name", name);
      b.putString("screen_name", screen_name);
      b.putString("image_url", image_url);

      fragment.setArguments(b);

      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (getArguments() != null) {
         name = getArguments().getString("name");
         screen_name = getArguments().getString("screen_name");
         image_url = getArguments().getString("image_url");
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_add_tweet, container, false);
      return view;
   }

   @Override
   public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      tvNickName = (TextView) view.findViewById(R.id.tvNickName);
      tvRealName = (TextView) view.findViewById(R.id.tvUserName);
      ivCancel = (ImageView) view.findViewById(R.id.ivCancel);
      ivProfile = (ImageView) view.findViewById(R.id.ivUserImage);
      btnTweet = (Button) view.findViewById(R.id.btnTweet);
      etContent = (EditText) view.findViewById(R.id.etContent);

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
            final PostTweetDialogListener listener = (PostTweetDialogListener) getActivity();

            client = TwitterApplication.getRestClient();

            try {
               client.postTweet(content, new JsonHttpResponseHandler() {
                  @Override
                  public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                     User user = new User();

                     user.setName(name);
                     user.setProfileImageUrl(image_url);
                     user.setScreenName(screen_name);

                     try {
                        String createAt = response.getString("created_at");
                        Long id = Long.parseLong(response.getString("id_str"));
                        AddTweetToDB(id, name, screen_name, image_url,
                                content, createAt);

                        listener.onPostTweet(user, createAt, content);
                     } catch (JSONException e) {
                        e.printStackTrace();
                     }
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

   private void AddTweetToDB(Long id, String name, String screen_name, String image_url,
                             String content, String createAt) {
      TweetList tweetList = new TweetList();

      tweetList.setId(id);
      tweetList.setName(name);
      tweetList.setCreateAt(createAt);
      tweetList.setContent(content);
      tweetList.setImage_url(image_url);
      tweetList.setScreen_name(screen_name);
      tweetList.save();
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      /*if (context instanceof OnFragmentInteractionListener) {
         mListener = (OnFragmentInteractionListener) context;
      } else {
         throw new RuntimeException(context.toString()
                 + " must implement OnFragmentInteractionListener");
      }*/
   }

   @Override
   public void onDetach() {
      super.onDetach();
   }

   /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
   public interface PostTweetDialogListener {
      // TODO: Update argument type and name
      void onPostTweet(User user, String createAt, String tweetContent);
   }
}
