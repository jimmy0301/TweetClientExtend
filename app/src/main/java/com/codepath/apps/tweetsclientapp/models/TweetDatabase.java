package com.codepath.apps.tweetsclientapp.models;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Database(name = TweetDatabase.NAME, version = TweetDatabase.VERSION)
public class TweetDatabase {

   public static final String NAME = "TweetDatabase";

   public static final int VERSION = 2;

   @Migration(version = 2, database = TweetDatabase.class)
   public static class Migration2 extends AlterTableMigration<TweetList> {

      public Migration2(Class<TweetList> table) {
         super(table);
      }

      @Override
      public void onPreMigrate() {
         addColumn(SQLiteType.INTEGER, "favoriteCnt");
         addColumn(SQLiteType.INTEGER, "retweetCnt");
      }
   }
}
