package com.alexhutnik.twitter;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TwitterClientActivity extends ListActivity {
	private ProgressDialog progressDialog;
	private EditText searchEditText;
	private Cursor searchHistory;
	private ListView historyListView;
	private CursorAdapter searchHistoryAdapter;
	private TwitterClientDAO dao;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//DB setup
		dao = new TwitterClientDAO(this);
		dao.open();
		
		//History management
		searchHistory = dao.getSearchHistory();
		startManagingCursor(searchHistory);
		searchHistoryAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, searchHistory, new String[] {SQLConstants.COLUMNS.SEARCH_TEXT}, new int[] {android.R.id.text1});
		this.setListAdapter(searchHistoryAdapter);
	    
	    searchEditText = (EditText) findViewById(R.id.editText1);
	    
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				long searchId = dao.addSearch(searchEditText.getText().toString());
				searchHistoryAdapter.changeCursor(dao.getSearchHistory());
				progressDialog = ProgressDialog.show(TwitterClientActivity.this, "Please wait...", "Retrieving data...", true, true);
				ExecuteHTTPTwitterSearch task = new ExecuteHTTPTwitterSearch();
				task.executeWithSearchId(searchEditText.getText().toString(),searchId);
		    	progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
		    	searchEditText.setText("");
			}
		});
	} 
    

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		progressDialog = ProgressDialog.show(TwitterClientActivity.this, "Please wait...", "Retrieving data...", true, true);
		ExecuteDBTwitterSearch task = new ExecuteDBTwitterSearch();
		task.execute(String.valueOf(id));
    	progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
	};
	
	private class ExecuteHTTPTwitterSearch extends	AsyncTask<String, Void, ArrayList<Tweet>> {
		long _searchId;

		@Override
		protected ArrayList<Tweet> doInBackground(String... params) {
			String term = params[0];		
			String jsonString = HttpRetriever.retrieve("http://search.twitter.com/search.json?q="+term);
			return TweetProcessor.processSearchResults(jsonString, dao, _searchId);
		}

		protected void onPostExecute(final ArrayList<Tweet> tweetList) {
			launchSearchResultsWithTweets(tweetList);
		}
		
		public void executeWithSearchId(String searchText, long searchId){
			_searchId = searchId;
			this.execute(searchText);
		}
	}
	
	private class ExecuteDBTwitterSearch extends AsyncTask<String, Void, ArrayList<Tweet>> {

		@Override
		protected ArrayList<Tweet> doInBackground(String... params) {
			String id = params[0];
			return dao.getTweetsBySearchId(id);
		}

		protected void onPostExecute(final ArrayList<Tweet> tweetList) {
			launchSearchResultsWithTweets(tweetList);
		}
	}
	
	private void launchSearchResultsWithTweets(final ArrayList<Tweet> tweetList){
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
	
	protected void onResume(){
		super.onResume();
		dao.open();
		searchHistoryAdapter.changeCursor(dao.getSearchHistory());
	}
	
	protected void onPause(){
		super.onPause();
		dao.close();
	}

}