package com.alexhutnik.twitter;

import org.json.JSONArray;
import org.json.JSONObject;

public class TweetProcessor {
	public static void processSearchResults(String jsonString, TwitterClientDAO dao, String searchId) throws Exception {
		JSONObject jObject = new JSONObject(jsonString);
		JSONArray tweets = jObject.getJSONArray("results");
		if(tweets.length() == 0){
			throw new Exception();
		}
		for (int i = 0; i < tweets.length(); i++) {
			JSONObject row = tweets.getJSONObject(i);
			dao.addTweet(row.getString("id"), row.getString("from_user"), row.getString("text"), searchId);
		}
	}

}
