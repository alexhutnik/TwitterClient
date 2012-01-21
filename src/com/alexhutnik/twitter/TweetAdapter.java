package com.alexhutnik.twitter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TweetAdapter extends ArrayAdapter<Tweet>{
	private ArrayList<Tweet> tweets;
	private Activity context;

	public TweetAdapter(Activity context, int textViewResourceId, ArrayList<Tweet> tweetsList){
		super(context, textViewResourceId, tweetsList);
		this.tweets = tweetsList;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = tweets.get(position);
		View view = convertView;
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.results_row, null);
		}
		if(tweet != null){

			// name
			TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
			nameTextView.setText(tweet.getName());
			
			// tweet
			TextView ratingTextView = (TextView) view.findViewById(R.id.tweet_text_view);
			ratingTextView.setText(tweet.getText());
		}
		
		return view;
		
	}
}
