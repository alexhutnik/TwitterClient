package com.alexhutnik.twitter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TwitterClientSQLHelper extends SQLiteOpenHelper {

	public TwitterClientSQLHelper(Context context, String name,	CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQLConstants.SCHEMA.CREATE_TWEETS);
		db.execSQL(SQLConstants.SCHEMA.CREATE_SEARCH_HISTORY);
		db.execSQL(SQLConstants.SCHEMA.CREATE_TWEETXSEARCH);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
