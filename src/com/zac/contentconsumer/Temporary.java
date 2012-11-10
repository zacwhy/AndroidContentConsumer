package com.zac.contentconsumer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zac.contentconsumer.database.CmsContentDataSource;
import com.zac.contentconsumer.database.CmsMenuDataSource;
import com.zac.contentconsumer.database.CmsSQLiteOpenHelper;
import com.zac.contentconsumer.cms.CmsMenu;

public final class Temporary {

	public static void recreateMenus(Context context) {
		CmsSQLiteOpenHelper dbHelper = new CmsSQLiteOpenHelper(context);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		recreateDatabase(database);
		database.close();
		dbHelper.close();
	}
	
	private static void recreateDatabase(SQLiteDatabase database) {
		CmsMenuDataSource.deleteAllMenus(database);
		CmsContentDataSource.deleteAllContents(database);
		createDatabase(database);
	}
	
	private static void createDatabase(SQLiteDatabase database) {
		CmsMenu rootMenu = insertMenu(database, "Home", 0, 1);
		long rootMenuId = rootMenu.getId();

		CmsMenu menu1 = insertMenu(database, "Menu 1", rootMenuId, 1);
		CmsMenu menu2 = insertMenu(database, "Menu 2", rootMenuId, 2);
		CmsMenu menu3 = insertMenu(database, "Menu 3", rootMenuId, 3);
		
		insertLeaf(database, "The quick brown fox jumps over the lazy dog.", rootMenuId, 4, "<b>The lazy dog jumps over the quick brown fox.</b>");
		
		CmsMenu menu1_1 = insertMenu(database, "Menu 1-1", menu1.getId(), 1);
		insertLeaf(database, "Menu 1-2", menu1.getId(), 2, "Content for 1-2");
		insertLeaf(database, "Menu 1-3", menu1.getId(), 3, "Content for 1-3");

		insertLeaf(database, "Menu 2-1", menu2.getId(), 1, "Content for 2-1");
		insertLeaf(database, "Menu 2-3", menu2.getId(), 3, "Content for 2-3");
		insertLeaf(database, "Menu 2-2", menu2.getId(), 2, "Content for 2-2");

		insertLeaf(database, "Menu 3-1", menu3.getId(), 1, "This is the content for 3-1.");

		insertLeaf(database, "Menu 1-1-1", menu1_1.getId(), 1, "This is the content for menu 1-1-1.");
	}
	
	private static void insertLeaf(SQLiteDatabase database, String title, long parentId, int sequence, String content) {
		CmsMenu menu = insertMenu(database, title, parentId, sequence);
		insertContent(database, menu.getId(), content);
	}
	
	private static CmsMenu insertMenu(SQLiteDatabase database, String title, long parentId, int sequence) {
		return CmsMenuDataSource.insertAndReturnMenu(database, title, parentId, sequence);
	}
	
	private static long insertContent(SQLiteDatabase database, long menuId, String text) {
		return CmsContentDataSource.insertContent(database, menuId, text);
	}

}
