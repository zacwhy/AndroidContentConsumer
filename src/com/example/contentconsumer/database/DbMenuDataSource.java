package com.example.contentconsumer.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DbMenuDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DbMenuSQLiteOpenHelper dbHelper;
	private String[] allColumns =
		{
			ContentConsumerContract.DbMenu.COLUMN_NAME_ID,
			ContentConsumerContract.DbMenu.COLUMN_NAME_TITLE,
			ContentConsumerContract.DbMenu.COLUMN_NAME_PARENT_ID,
			ContentConsumerContract.DbMenu.COLUMN_NAME_SEQUENCE
		};

	public DbMenuDataSource(Context context) {
		dbHelper = new DbMenuSQLiteOpenHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public DbMenu insertMenu(String title, long parentId, long sequence) {
		ContentValues values = new ContentValues();
		values.put(ContentConsumerContract.DbMenu.COLUMN_NAME_TITLE, title);
		values.put(ContentConsumerContract.DbMenu.COLUMN_NAME_PARENT_ID, parentId);
		values.put(ContentConsumerContract.DbMenu.COLUMN_NAME_SEQUENCE, sequence);
		return insertMenu(values);
	}

	private DbMenu insertMenu(ContentValues values) {
		long id = database.insert(ContentConsumerContract.DbMenu.TABLE_NAME, null, values);
		return getMenuById(id);
	}

	public void deleteMenu(DbMenu menu) {
		long id = menu.getId();
		//System.out.println("Comment deleted with id: " + id);
		String whereClause = ContentConsumerContract.DbMenu.COLUMN_NAME_ID + " = " + id; // todo
		database.delete(ContentConsumerContract.DbMenu.TABLE_NAME, whereClause, null);
	}
	
	public void deleteAllMenus() {
		database.delete(ContentConsumerContract.DbMenu.TABLE_NAME, null, null);
	}
	
	public DbMenu getMenuById(long id) {
		String selection = ContentConsumerContract.DbMenu.COLUMN_NAME_ID + " = ?";
		String[] selectionArgs = { String.valueOf(id) };
		Cursor cursor = database.query(ContentConsumerContract.DbMenu.TABLE_NAME, allColumns, selection, selectionArgs, null, null, null);
		cursor.moveToFirst();
		DbMenu menu = cursorToMenu(cursor);
		cursor.close();
		return menu;
	}

	public List<DbMenu> getAllMenus() {
		List<DbMenu> menus = new ArrayList<DbMenu>();
		Cursor cursor = database.query(ContentConsumerContract.DbMenu.TABLE_NAME, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DbMenu menu = cursorToMenu(cursor);
			menus.add(menu);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return menus;
	}

	private DbMenu cursorToMenu(Cursor cursor) {
		DbMenu menu = new DbMenu();
		menu.setId(getLong(cursor, ContentConsumerContract.DbMenu.COLUMN_NAME_ID));
		menu.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(ContentConsumerContract.DbMenu.COLUMN_NAME_TITLE)));
		menu.setParentId(getLong(cursor, ContentConsumerContract.DbMenu.COLUMN_NAME_PARENT_ID));
		menu.setSequence(getLong(cursor, ContentConsumerContract.DbMenu.COLUMN_NAME_SEQUENCE));
		return menu;
	}
	
	private long getLong(Cursor cursor, String columnName) {
		int columnIndex = cursor.getColumnIndexOrThrow(columnName);
		return cursor.getLong(columnIndex);
	}

}
