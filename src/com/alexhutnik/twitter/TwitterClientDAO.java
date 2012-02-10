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
