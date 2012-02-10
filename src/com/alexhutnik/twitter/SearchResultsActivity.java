package com.alexhutnik.twitter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SearchResultsActivity extends ListActivity {
	
	private ProgressDialog progressDialog;
	private SimpleCursorAdapter tweetAdapter;
	private Cursor tweetCursor;
	private String _searchId;
	private TwitterClientDAO dao;
	private Resources res;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		
		res = getResources();
		
		dao = new TwitterClientDAO(this);
		dao.open();
		
		_searchId = (String) getIntent().getSerializableExtra("searchId");
		tweetCursor = dao.getTweetsBySearchId(_searchId);
		startManagingCursor(tweetCursor);
		tweetAdapter = new SimpleCursorAdapter(this, R.layout.results_row, tweetCursor, new String[] {SQLConstants.COLUMNS.TWITTER_NAME,SQLConstants.COLUMNS.TWEET_TEXT}, new int[] {R.id.name_text_view,R.id.tweet_text_view});
		setListAdapter(tweetAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.refresh_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.refresh:
	        	refreshTweets();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void refreshTweets(){
		progressDialog = ProgressDialog.show(SearchResultsActivity.this, res.getString(R.string.please_wait), res.getString(R.string.retrieving_data), true, true);
		ExecuteTweetRefresh task = new ExecuteTweetRefresh();
		task.execute(_searchId);
    	progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
	}
	
	private class ExecuteTweetRefresh extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				String term = dao.getSearchTermById(params[0]);
				String jsonString = HttpRetriever.retrieve(term);
				TweetProcessor.processSearchResults(jsonString, dao, params[0]);
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		protected void onPostExecute(Boolean success) {
			if(success){
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
	
						if (progressDialog != null) {
							progressDialog.dismiss();
							progressDialog = null;
						}
						Cursor cursor = dao.getTweetsBySearchId(_searchId);
						startManagingCursor(cursor);
						tweetAdapter.changeCursor(cursor);
					}
				});
			} else {
				Toast.makeText(getApplicationContext(), res.getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
			}
		}
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
	}
	
	protected void onPause(){
		super.onPause();
		dao.close();
	}
	
}
