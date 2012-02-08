package com.alexhutnik.twitter;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;

public class SearchResultsActivity extends ListActivity {
	
	TweetAdapter tweetAdapter;
	ArrayList<Tweet> tweetsList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		
		tweetsList = (ArrayList<Tweet>) getIntent().getSerializableExtra("tweets");
		tweetAdapter = new TweetAdapter(this, R.layout.results_row, tweetsList);
		setListAdapter(tweetAdapter);
	}
}
