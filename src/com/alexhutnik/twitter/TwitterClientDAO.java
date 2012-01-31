package com.alexhutnik.twitter;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TwitterClientDAO {
	private SQLiteDatabase database;
	private TwitterClientSQLHelper dbHelper;

	public TwitterClientDAO(Context context) {
		dbHelper = new TwitterClientSQLHelper(context, "tweets.db",null,1);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
}
