package com.zac.contentconsumer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.zac.contentconsumer.support.CmsContent;

public class CmsContentDataSource {

	private SQLiteDatabase database;
	private CmsSQLiteOpenHelper dbHelper;
	
	private static String[] allColumns =
		{
			CmsContract.CmsContentsTable.COLUMN_NAME_CMS_MENU_ID,
			CmsContract.CmsContentsTable.COLUMN_NAME_CONTENT
		};

	public CmsContentDataSource(Context context) {
		dbHelper = new CmsSQLiteOpenHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	//
	// insert
	//
	
	public long insertContent(int menuId, String text) {
		return insertContent(database, menuId, text);
	}
	
	public static long insertContent(SQLiteDatabase database, long menuId, String text) {
		ContentValues values = new ContentValues();
		values.put(CmsContract.CmsContentsTable.COLUMN_NAME_CMS_MENU_ID, menuId);
		values.put(CmsContract.CmsContentsTable.COLUMN_NAME_CONTENT, text);
		return database.insert(CmsContract.CmsContentsTable.TABLE_NAME, null, values);
	}
	
	//
	// delete
	//
	
	public static int deleteAllContents(SQLiteDatabase database) {
		return database.delete(CmsContract.CmsContentsTable.TABLE_NAME, "1", null);
	}
	
	//
	// get
	//
	
	public CmsContent getContentByMenuId(long menuId) {
		return getContentByMenuId(database, menuId);
	}
	
	public static CmsContent getContentByMenuId(SQLiteDatabase database, long menuId) {
		String selection = CmsContract.CmsContentsTable.COLUMN_NAME_CMS_MENU_ID + " = ?";
		String[] selectionArgs = { String.valueOf(menuId) };
		Cursor cursor = database.query(CmsContract.CmsContentsTable.TABLE_NAME, allColumns, selection, selectionArgs, null, null, null);
		cursor.moveToFirst();
		
		if (cursor.isAfterLast()) {
			return null;
		}
		
		CmsContent content = cursorToCmsContent(cursor);
		cursor.close();
		return content;
	}
	
	//
	// helpers
	//
	
	private static CmsContent cursorToCmsContent(Cursor c) {
		CmsContent content = new CmsContent();
		
		//Cursor c = cursor;
		content.setMenuId(getLong(c, CmsContract.CmsContentsTable.COLUMN_NAME_CMS_MENU_ID));
		content.setContent(getString(c, CmsContract.CmsContentsTable.COLUMN_NAME_CONTENT));
		
		//content.setMenuId(cursor.getLong(cursor.getColumnIndexOrThrow(CmsContract.CmsContentsTable.COLUMN_NAME_CMS_MENU_ID)));
		//content.setContent(cursor.getString(cursor.getColumnIndexOrThrow(CmsContract.CmsContentsTable.COLUMN_NAME_CONTENT)));
		
		return content;
	}
	
	private static long getLong(Cursor cursor, String columnName) {
		return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
	}
	
	private static String getString(Cursor cursor, String columnName) {
		return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
	}

}
