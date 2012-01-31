package com.alexhutnik.twitter;

public class SQLConstants {
	public static class COLUMNS {
		public static final String ID = "_id";
		public static final String TWEET_ID = "tweet_id";
		public static final String TWITTER_NAME = "twitter_name";
		public static final String TWEET_TEXT = "tweet_text";
		public static final String SEARCH_TEXT = "search_text";
		public static final String SEARCH_ID = "search_id";
	}
	public static class TABLES {
		public static final String TWEETS = "Tweets";
		public static final String SEARCH_HISTORY = "SearchHistory";
		public static final String TWEETSXSEARCH = "TweetsSearches";
	}
	
	public static class SCHEMA {
		//put CREATE TABLE statements here
		public static final String CREATE_TWEETS = String.format("create table %s " +
																 "(%s integer primary key autoincrement," +
																 "%s integer unique," +
																 "%s text not null," +
																 "%s text not null);",
																 SQLConstants.TABLES.TWEETS,
																 SQLConstants.COLUMNS.ID,
																 SQLConstants.COLUMNS.TWEET_ID,
																 SQLConstants.COLUMNS.TWITTER_NAME,
																 SQLConstants.COLUMNS.TWEET_TEXT);
		
		public static final String CREATE_SEARCH_HISTORY = String.format("create table %s " +
														   "(%s integer primary key autoincrement," +
														   "%s text not null);",
														   SQLConstants.TABLES.SEARCH_HISTORY,
														   SQLConstants.COLUMNS.ID,
														   SQLConstants.COLUMNS.SEARCH_TEXT);
		
		public static final String CREATE_TWEETXSEARCH = String.format("create table %s " +
													   "(%s integer primary key autoincrement," +
													   "%s integer not null," +
													   "%s integer not null);",
													   SQLConstants.TABLES.TWEETSXSEARCH,
													   SQLConstants.COLUMNS.ID,
													   SQLConstants.COLUMNS.SEARCH_ID,
													   SQLConstants.COLUMNS.TWEET_ID);
	}
}
