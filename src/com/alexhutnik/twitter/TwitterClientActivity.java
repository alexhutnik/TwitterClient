package com.alexhutnik.twitter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TwitterClientActivity extends Activity {
	private ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				performSearch("twitter");
			}
		});
	}

	private void performSearch(String term) {
		
    	progressDialog = ProgressDialog.show(TwitterClientActivity.this, "Please wait...", "Retrieving data...", true, true);
		ExecuteTwitterSearch task = new ExecuteTwitterSearch();
		task.execute(term);
    	progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
	}

	private class CancelTaskOnCancelListener implements OnCancelListener {
		private AsyncTask<?, ?, ?> task;

		public CancelTaskOnCancelListener(AsyncTask<?, ?, ?> task) {
			this.task = task;
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			if (task != null) {
				task.cancel(true);
			}
		}
	}

	private class ExecuteTwitterSearch extends	AsyncTask<String, Void, ArrayList<Tweet>> {

		@Override
		protected ArrayList<Tweet> doInBackground(String... params) {
			String term = params[0];
			ArrayList<Tweet> tweetList = new ArrayList<Tweet>();			
			try {
				String jsonString = HttpRetriever.retrieve("http://search.twitter.com/search.json?q="+term);
				JSONObject jObject = new JSONObject(jsonString);
				JSONArray tweets = jObject.getJSONArray("results");
				Log.d(getClass().getSimpleName(),"array size: " + tweets.length());

				for (int i = 0; i < tweets.length(); i++) {
				    JSONObject row = tweets.getJSONObject(i);
				    tweetList.add(new Tweet(row.getString("from_user"), row.getString("text")));
				}
			} catch (JSONException je) {
				Log.e(getClass().getSimpleName(), "Error processing JSON");
			}
			
			return tweetList;
		}

		protected void onPostExecute(final ArrayList<Tweet> tweetList) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					
					Intent intent = new Intent(TwitterClientActivity.this,	SearchResultsActivity.class);
					intent.putExtra("tweets", tweetList);
					startActivity(intent);
				}
			});
		}

	}

}