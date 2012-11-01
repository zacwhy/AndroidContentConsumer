package com.example.contentconsumer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbMenuSQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "content_consumer.db";
	
	// If you change the database schema, you must increment the database version.
	private static final int DATABASE_VERSION = 4;
	
	//private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ", ";

	// Database creation sql statement
	private static final String SQL_CREATE_MENUS =
		"CREATE TABLE " + ContentConsumerContract.DbMenu.TABLE_NAME + "("
		+ ContentConsumerContract.DbMenu.COLUMN_NAME_ID + " integer primary key autoincrement"
		+ COMMA_SEP + ContentConsumerContract.DbMenu.COLUMN_NAME_TITLE + " text not null"
		+ COMMA_SEP + ContentConsumerContract.DbMenu.COLUMN_NAME_PARENT_ID + " integer"
		+ COMMA_SEP + ContentConsumerContract.DbMenu.COLUMN_NAME_SEQUENCE + " integer"
		+ ");";
	
	private static final String SQL_DROP_MENUS =
	        "DROP TABLE IF EXISTS " + ContentConsumerContract.DbMenu.TABLE_NAME;
	
	public DbMenuSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(SQL_CREATE_MENUS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String msg = "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data";
		Log.w(DbMenuSQLiteOpenHelper.class.getName(), msg);
		recreate(db);
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
	private void drop(SQLiteDatabase db) {
		db.execSQL(SQL_DROP_MENUS);
	}
	
	private void recreate(SQLiteDatabase db) {
		drop(db);
		onCreate(db);
	}

}
