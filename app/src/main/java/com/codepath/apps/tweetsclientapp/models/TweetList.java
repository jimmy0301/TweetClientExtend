package com.codepath.apps.tweetsclientapp.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the DBFlow wiki for more details:
 * https://github.com/codepath/android_guides/wiki/DBFlow-Guide
 *
 * Note: All models **must extend from** `BaseModel` as shown below.
 * 
 */
@Table(database = TweetDatabase.class)
public class TweetList extends BaseModel {

   private static final int NUM_ITEMS = 10;

   @PrimaryKey
   @Column
   Long id;

   // Define table fields
   @Column
   private String name;

   @Column
   private String screen_name;

   @Column
   private String image_url;

   @Column
   private String content;

   @Column
   private String createAt;

   @Column
   private Long favoriteCnt;

   @Column
   private Long retweetCnt;

   public TweetList() {
      super();
   }

   // Parse model from JSON
   public TweetList(JSONObject object) {
      super();

      try {
         this.name = object.getString("title");
      } catch (JSONException e) {
         e.printStackTrace();
      }
   }

   // Getters
   public String getName() {
      return name;
   }

   // Setters
   public void setName(String name) {
      this.name = name;
   }

   public Long getId() {
      return id;
   }

   public String getScreen_name() {
      return screen_name;
   }

   public String getImage_url() {
      return image_url;
   }

   public String getContent() {
      return content;
   }

   public String getCreateAt() {
      return createAt;
   }

   public Long getFavoriteCnt() {
      return favoriteCnt;
   }

   public Long getRetweetCnt() {
      return retweetCnt;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setScreen_name(String screen_name) {
      this.screen_name = screen_name;
   }

   public void setImage_url(String image_url) {
      this.image_url = image_url;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public void setCreateAt(String createAt) {
      this.createAt = createAt;
   }

   public void setFavoriteCnt(Long favoriteCnt) {
      this.favoriteCnt = favoriteCnt;
   }

   public void setRetweetCnt(Long retweetCnt) {
      this.retweetCnt = retweetCnt;
   }

   /* The where class in this code below will be marked red until you first compile the project, since the code
    * for the SampleModel_Table class is generated at compile-time.
	 */

   // Record Finders
   public static List<TweetList> loadMoreTweetbyId(long max_id) {
      return new Select().from(TweetList.class).where(TweetList_Table.id.lessThan(max_id)).orderBy(TweetList_Table.id, false)
      .limit(NUM_ITEMS).queryList();
   }

   public static List<TweetList> recentItems() {

      return new Select().from(TweetList.class).orderBy(TweetList_Table.id, false).limit(NUM_ITEMS).queryList();
   }

   public static void addTimeLineList(List<TweetList> list) {
      FlowManager.getDatabase(TweetDatabase.class)
              .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                      new ProcessModelTransaction.ProcessModel<TweetList>() {
                         @Override
                         public void processModel(TweetList user, DatabaseWrapper databaseWrapper) {
                            // do work here -- i.e. user.delete() or user.update()
                            user.save();
                         }
                      }).addAll(list).build())  // add elements (can also handle multiple)
              .error(new Transaction.Error() {
                 @Override
                 public void onError(Transaction transaction, Throwable error) {

                 }
              })
              .success(new Transaction.Success() {
                 @Override
                 public void onSuccess(Transaction transaction) {

                 }
              }).build().execute();
   }
}
