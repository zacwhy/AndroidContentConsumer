package com.zac.contentconsumer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CmsSQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "cms.db";
	
	// If you change the database schema, you must increment the database version.
	private static final int DATABASE_VERSION = 19;
	
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String TEXT_TYPE = " TEXT";
	private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_MENUS =
            "CREATE TABLE " + CmsContract.CmsMenusTable.TABLE_NAME + "("
                    + CmsContract.CmsMenusTable._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT"
                    + COMMA_SEP + CmsContract.CmsMenusTable.COLUMN_NAME_MENU_ID + INTEGER_TYPE + NOT_NULL
                    + COMMA_SEP + CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID + INTEGER_TYPE + NOT_NULL
                    + COMMA_SEP + CmsContract.CmsMenusTable.COLUMN_NAME_SEQUENCE + INTEGER_TYPE + NOT_NULL
                    + COMMA_SEP + CmsContract.CmsMenusTable.COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL
                    + COMMA_SEP + "UNIQUE(" + CmsContract.CmsMenusTable.COLUMN_NAME_MENU_ID + ") ON CONFLICT ABORT"
                    + ");";

    private static final String SQL_CREATE_CONTENTS =
            "CREATE TABLE " + CmsContract.CmsContentsTable.TABLE_NAME + "("
                    + CmsContract.CmsContentsTable._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT"
                    + COMMA_SEP + CmsContract.CmsContentsTable.COLUMN_NAME_CMS_MENU_ID + INTEGER_TYPE
                    + COMMA_SEP + CmsContract.CmsContentsTable.COLUMN_NAME_CONTENT + TEXT_TYPE + NOT_NULL
                    + ");";

	private static final String SQL_DROP_MENUS =
	        "DROP TABLE IF EXISTS " + CmsContract.CmsMenusTable.TABLE_NAME;
	
	private static final String SQL_DROP_CONTENTS =
	        "DROP TABLE IF EXISTS " + CmsContract.CmsContentsTable.TABLE_NAME;

	public CmsSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(SQL_CREATE_MENUS);
		database.execSQL(SQL_CREATE_CONTENTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String format = "Upgrading database from version %d to %d, which will destroy all old data";
		String message = String.format(format, oldVersion, newVersion);
		Log.w(CmsSQLiteOpenHelper.class.getName(), message);
		recreate(db);
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
	private void drop(SQLiteDatabase db) {
		db.execSQL(SQL_DROP_MENUS);
		db.execSQL(SQL_DROP_CONTENTS);
	}
	
	private void recreate(SQLiteDatabase db) {
		drop(db);
		onCreate(db);
	}

}
