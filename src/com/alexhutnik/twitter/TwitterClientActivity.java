package com.alexhutnik.twitter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.Toast;

public class TwitterClientActivity extends ListActivity {
	private ProgressDialog progressDialog;
	private EditText searchEditText;
	private Cursor searchHistory;
	private CursorAdapter searchHistoryAdapter;
	private TwitterClientDAO dao;
	private Resources res;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		res = getResources();
		// DB setup
		dao = new TwitterClientDAO(this);
		dao.open();

		// History management
		searchHistory = dao.getSearchHistory();
		startManagingCursor(searchHistory);
		searchHistoryAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, searchHistory,
				new String[] { SQLConstants.COLUMNS.SEARCH_TEXT }, new int[] { android.R.id.text1 });
		this.setListAdapter(searchHistoryAdapter);

		searchEditText = (EditText) findViewById(R.id.editText1);

		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String searchTerm = searchEditText.getText().toString();
				if (!searchTerm.isEmpty()) {
					long searchId = dao.addSearch(searchTerm);
					searchHistoryAdapter.changeCursor(dao.getSearchHistory());
					progressDialog = ProgressDialog.show(TwitterClientActivity.this, res.getString(R.string.please_wait), res.getString(R.string.retrieving_data), true, true);
					ExecuteHTTPTwitterSearch task = new ExecuteHTTPTwitterSearch();
					task.executeWithSearchId(searchTerm, String.valueOf(searchId));
					progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
					searchEditText.setText("");
				} else {
					Toast.makeText(getApplicationContext(), res.getText(R.string.empty_search), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		launchSearchResults(String.valueOf(id));
	};

	private class ExecuteHTTPTwitterSearch extends AsyncTask<String, Void, Boolean> {
		String _searchId;

		@Override
		protected Boolean doInBackground(String... params) {
			try{
				String term = params[0];
				String jsonString = HttpRetriever.retrieve(term);
				TweetProcessor.processSearchResults(jsonString, dao, _searchId);
			} catch (Exception e){
				return false;
			}
			return true;
		}

		protected void onPostExecute(Boolean success) {
			if(success){
				launchSearchResults(String.valueOf(_searchId));
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (progressDialog != null) {
							progressDialog.dismiss();
							progressDialog = null;
						}	
						Toast.makeText(getApplicationContext(), res.getString(R.string.generic_error), Toast.LENGTH_SHORT).show();	
					}
				});
			}
		}

		public void executeWithSearchId(String searchText, String searchId) {
			_searchId = searchId;
			this.execute(searchText);
		}
	}

	private void launchSearchResults(final String searchId) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}

				Intent intent = new Intent(TwitterClientActivity.this, SearchResultsActivity.class);
				intent.putExtra("searchId", searchId);
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

	protected void onResume() {
		super.onResume();
		dao.open();
		searchHistoryAdapter.changeCursor(dao.getSearchHistory());
	}

	protected void onPause() {
		super.onPause();
		dao.close();
	}

}