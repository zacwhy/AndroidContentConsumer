package com.zac.contentconsumer.database;

import java.util.ArrayList;
import java.util.List;

import com.zac.contentconsumer.support.CmsMenu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CmsMenuDataSource {

	private SQLiteDatabase database;
	private CmsSQLiteOpenHelper dbHelper;
	
	private static String[] allColumns =
		{
			CmsContract.CmsMenusTable._ID,
			CmsContract.CmsMenusTable.COLUMN_NAME_TITLE,
			CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID,
			CmsContract.CmsMenusTable.COLUMN_NAME_SEQUENCE
		};

	public CmsMenuDataSource(Context context) {
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
	
	public CmsMenu insertMenu(String title, int parentId, int sequence) {
		ContentValues values = new ContentValues();
		values.put(CmsContract.CmsMenusTable.COLUMN_NAME_TITLE, title);
		values.put(CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID, parentId);
		values.put(CmsContract.CmsMenusTable.COLUMN_NAME_SEQUENCE, sequence);
		return insertMenu(values);
	}

	private CmsMenu insertMenu(ContentValues values) {
		long id = database.insert(CmsContract.CmsMenusTable.TABLE_NAME, null, values);
		return getMenuById(id);
	}
	
	public static long insertMenu(SQLiteDatabase database, String title, long parentId, int sequence) {
		ContentValues values = new ContentValues();
		values.put(CmsContract.CmsMenusTable.COLUMN_NAME_TITLE, title);
		values.put(CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID, parentId);
		values.put(CmsContract.CmsMenusTable.COLUMN_NAME_SEQUENCE, sequence);
		return database.insert(CmsContract.CmsMenusTable.TABLE_NAME, null, values);
	}
	
	public static CmsMenu insertAndReturnMenu(SQLiteDatabase database, String title, long parentId, int sequence) {
		long id = insertMenu(database, title, parentId, sequence);
		return getMenuById(database, id);
	}
	
	//
	// delete
	//
	
	public int deleteAllMenus() {
		return deleteAllMenus(database);
	}
	
	public static int deleteAllMenus(SQLiteDatabase database) {
		return database.delete(CmsContract.CmsMenusTable.TABLE_NAME, "1", null);
	}

	public void deleteMenu(CmsMenu menu) {
		long id = menu.getId();
		String whereClause = CmsContract.CmsMenusTable._ID + " = ?";
		String[] whereArgs = { String.valueOf(id) };
		database.delete(CmsContract.CmsMenusTable.TABLE_NAME, whereClause, whereArgs);
	}
	
	//
	// get
	//
	
	public CmsMenu getMenuById(long id) {
		return getMenuById(database, id);
	}
	
	public static CmsMenu getMenuById(SQLiteDatabase database, long id) {
		String selection = CmsContract.CmsMenusTable._ID + " = ?";
		String[] selectionArgs = { String.valueOf(id) };
		Cursor cursor = database.query(CmsContract.CmsMenusTable.TABLE_NAME, allColumns, selection, selectionArgs, null, null, null);
		cursor.moveToFirst();
		CmsMenu menu = cursorToCmsMenu(cursor);
		cursor.close();
		return menu;
	}

//	public List<DbMenu> getAllMenus() {
//		List<DbMenu> menus = new ArrayList<DbMenu>();
//		Cursor cursor = database.query(ContentConsumerContract.DbMenu.TABLE_NAME, allColumns, null, null, null, null, null);
//
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()) {
//			DbMenu menu = cursorToMenu(cursor);
//			menus.add(menu);
//			cursor.moveToNext();
//		}
//		
//		// Make sure to close the cursor
//		cursor.close();
//		return menus;
//	}
	
	public CmsMenu getRootMenuWithChildren() {
		List<CmsMenu> menus = getMenusWithChildrenByParentId(0);
		
		if (menus.isEmpty()) {
			return null;
		}
		
		return menus.get(0);
	}
	
	private List<CmsMenu> getMenusWithChildrenByParentId(long parentId) {
		List<CmsMenu> menus = getMenusByParentId(parentId);
		
		for (CmsMenu menu : menus) {
			List<CmsMenu> children = getMenusByParentId(menu.getId());
			menu.setChildren(children);
		}
		
		return menus;
	}
	
	private List<CmsMenu> getMenusByParentId(long parentId) {
		List<CmsMenu> menus = new ArrayList<CmsMenu>();
		
		String table = CmsContract.CmsMenusTable.TABLE_NAME;
		String selection = CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID + " = ?";
		String[] selectionArgs = { String.valueOf(parentId) };
		String orderBy = CmsContract.CmsMenusTable.COLUMN_NAME_SEQUENCE;
		Cursor cursor = database.query(table, allColumns, selection, selectionArgs, null, null, orderBy);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			menus.add(cursorToCmsMenu(cursor));
			cursor.moveToNext();
		}
		
		cursor.close();
		return menus;
	}
	
	public CmsMenu getMenuWithChildrenById(long id) {
		String table = CmsContract.CmsMenusTable.TABLE_NAME;
		String selection = CmsContract.CmsMenusTable._ID + " = ?";
		String[] selectionArgs = { String.valueOf(id) };
		Cursor cursor = database.query(table, allColumns, selection, selectionArgs, null, null, null);
		
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			return null;
		}
		
		CmsMenu menu = cursorToCmsMenu(cursor);
		List<CmsMenu> children = getMenusByParentId(menu.getId());
		menu.setChildren(children);
		
		cursor.close();
		return menu;
	}
	
	public boolean hasChild(long id) {
		String table = CmsContract.CmsMenusTable.TABLE_NAME;
		String columnNameParentId = CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID;
		String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", table, columnNameParentId);
		
		Cursor cursor = database.rawQuery(sql, new String[] { String.valueOf(id) });
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		
		//SQLiteStatement sqliteStatement = database.compileStatement(sql);
		//long count2 = sqliteStatement.simpleQueryForLong();
		
		return count > 0;
	}
	
	//
	// helpers
	//
	
	private static CmsMenu cursorToCmsMenu(Cursor c) {
		CmsMenu menu = new CmsMenu();
		menu.setId(getLong(c, CmsContract.CmsMenusTable._ID));
		menu.setTitle(getString(c, CmsContract.CmsMenusTable.COLUMN_NAME_TITLE));
		menu.setParentId(getLong(c, CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID));
		//menu.setSequence(getLong(cursor, ContentConsumerContract.DbMenu.COLUMN_NAME_SEQUENCE));
		return menu;
	}
	
	private static long getLong(Cursor cursor, String columnName) {
		return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
	}
	
	private static String getString(Cursor cursor, String columnName) {
		return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
	}
	
//	private static int getInt(Cursor cursor, String columnName) {
//		return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
//	}

}
