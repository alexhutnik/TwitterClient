package com.alexhutnik.twitter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TwitterClientDAO {
	private SQLiteDatabase database;
	private TwitterClientSQLHelper dbHelper;

	public TwitterClientDAO(Context context) {
		dbHelper = new TwitterClientSQLHelper(context, "tweets.db", null, 1);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Cursor getSearchHistory() {
		Cursor cursor = database.query(SQLConstants.TABLES.SEARCH_HISTORY, null, null, null, null, null, null);
		return cursor;
	}
	
	public String getSearchTermById(String id){
		Cursor cursor = database.query(SQLConstants.TABLES.SEARCH_HISTORY, new String[]{SQLConstants.COLUMNS.SEARCH_TEXT}, SQLConstants.COLUMNS.ID+"=?", new String[]{id}, null, null, null);
		if(cursor.moveToFirst()){
			return cursor.getString(0);
		} else {
			return null;
		}
	}

	public long addSearch(String searchTerm) {
		Cursor cursor = database.query(SQLConstants.TABLES.SEARCH_HISTORY, new String[]{SQLConstants.COLUMNS.ID}, SQLConstants.COLUMNS.SEARCH_TEXT+"=?", new String[]{searchTerm}, null, null, null);
		if(cursor.getCount()==0){
			ContentValues cv = new ContentValues();
			cv.put(SQLConstants.COLUMNS.SEARCH_TEXT, searchTerm);
			return database.insert(SQLConstants.TABLES.SEARCH_HISTORY, null, cv);	
		} else {
			return cursor.getLong(0);
		}
	}

	public void addTweet(String tweetId, String name, String text, String searchId) {
		/*
		 * Kind of a tricky situation here. I want to make sure I don't store a
		 * single tweet more than once (i.e. Twitter returned the same tweet for
		 * two different searches). But I also want to reduce the number of
		 * times we hit the DB. Could be up to 3 queries for each tweet. Could
		 * add up to quite a few.
		 * 
		 * Option 1: 
		 * 1. Check DB for existing tweet (+1)
		 * 2a. If it exists grab the row id
		 * 2b. If not, insert the new tweet (+1)
		 * 3. Insert entry in join table (+1) Up to (1+1+1) queries.
		 * 
		 * Option 2:
		 * 1. Using a raw query, insert the new tweet with ON DUPLICATE KEY 1=1 (+1) 
		 * 2. Since that returns an "empty" cursor, re-query to get the id (+1) 
		 * 3. Insert row into join table (+1)
		 * Guaranteed 3 queries.
		 * 
		 * So let's go with the first option. We'll likely hit 3 queries and in
		 * some really far out edge case, we only need two.
		 */
		
		/*
		 * TODO Test to see if we have an entry in the join table too.
		 */
		ContentValues cv = new ContentValues();
		database.beginTransaction();
		Cursor cursor = database.query(SQLConstants.TABLES.TWEETS, new String[] { SQLConstants.COLUMNS.ID }, SQLConstants.COLUMNS.TWEET_ID + "=?", new String[]{tweetId}, null, null, null);
		if (cursor.getCount() == 0) {
			cv.put(SQLConstants.COLUMNS.TWEET_ID, tweetId);
			cv.put(SQLConstants.COLUMNS.TWITTER_NAME, name);
			cv.put(SQLConstants.COLUMNS.TWEET_TEXT, text);
			long tweet_id = database.insert(SQLConstants.TABLES.TWEETS, null, cv);
			cv.clear();
			cv.put(SQLConstants.COLUMNS.SEARCH_ID, searchId);
			cv.put(SQLConstants.COLUMNS.TWEET_ID, tweet_id);
			database.insert(SQLConstants.TABLES.TWEETSXSEARCH, null, cv);
			Log.w(this.getClass().getSimpleName(),"Added new tweet with ID "+tweetId);
		} else {
			cv.put(SQLConstants.COLUMNS.SEARCH_ID, searchId);
			cv.put(SQLConstants.COLUMNS.TWEET_ID, cursor.getString(0));
			database.insert(SQLConstants.TABLES.TWEETSXSEARCH, null, cv);
			Log.w(this.getClass().getSimpleName(),"Re-using existing tweet "+tweetId);
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		cursor.close();
	}

	public Cursor getTweetsBySearchId(String id) {
		Cursor cursor = database.rawQuery("select t._id,t.twitter_name,t.tweet_text from Tweets t, TweetsSearches ts where ts.search_id=? and ts.tweet_id = t._id order by t._id desc" , new String[]{id});
		return cursor;
	}
}
