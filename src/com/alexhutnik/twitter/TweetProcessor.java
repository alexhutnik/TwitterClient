package com.alexhutnik.twitter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class TweetProcessor {
	public static ArrayList<Tweet> processSearchResults(Cursor cursor) {
		ArrayList<Tweet> tweetList = new ArrayList<Tweet>();
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				tweetList.add(new Tweet(cursor.getString(0), cursor.getString(1)));
				cursor.moveToNext();
			}
		}
		return tweetList;
	}

	public static ArrayList<Tweet> processSearchResults(String jsonString, TwitterClientDAO dao, long searchId) {
		ArrayList<Tweet> tweetList = new ArrayList<Tweet>();
		try {
			JSONObject jObject = new JSONObject(jsonString);
			JSONArray tweets = jObject.getJSONArray("results");

			for (int i = 0; i < tweets.length(); i++) {
				JSONObject row = tweets.getJSONObject(i);
				tweetList.add(new Tweet(row.getString("from_user"), row.getString("text")));
				dao.addTweet(row.getString("id"), row.getString("from_user"), row.getString("text"), searchId);
			}
		} catch (JSONException je) {
			//TODO Add Exception handling with a toast
		}

		return tweetList;
	}

}
