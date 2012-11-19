package com.zac.contentconsumer.database;

import java.util.ArrayList;
import java.util.List;

import com.zac.contentconsumer.cms.CmsMenu;

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
                    CmsContract.CmsMenusTable.COLUMN_NAME_MENU_ID,
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
        database.close();
        dbHelper.close();
    }

    //
    // Insert

    public static long insertMenu(SQLiteDatabase database, CmsMenu cmsMenu) {
        ContentValues values = new ContentValues();
        values.put(CmsContract.CmsMenusTable.COLUMN_NAME_MENU_ID, cmsMenu.getId());
        values.put(CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID, cmsMenu.getParentId());
        values.put(CmsContract.CmsMenusTable.COLUMN_NAME_SEQUENCE, cmsMenu.getSequence());
        values.put(CmsContract.CmsMenusTable.COLUMN_NAME_TITLE, cmsMenu.getTitle());
        return database.insert(CmsContract.CmsMenusTable.TABLE_NAME, null, values);
    }

    //
    // Delete

    public static int deleteAllMenus(SQLiteDatabase database) {
        return database.delete(CmsContract.CmsMenusTable.TABLE_NAME, "1", null);
    }

    //
    // Get

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
            List<CmsMenu> children = getMenusByParentId(menu.getId()); // TODO improve
            menu.setChildren(children);
        }

        return menus;
    }

    public List<CmsMenu> getMenusByParentId(long parentId) {
        List<CmsMenu> menus = new ArrayList<CmsMenu>();

        String table = CmsContract.CmsMenusTable.TABLE_NAME;
        String selection = CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(parentId)};
        String orderBy = CmsContract.CmsMenusTable.COLUMN_NAME_SEQUENCE;
        Cursor cursor = database.query(table, allColumns, selection, selectionArgs, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CmsMenu menu = cursorToCmsMenu(cursor);

            int childrenCount = getChildrenCount(menu.getId()); // TODO improve
            menu.setChildrenCount(childrenCount);

            menus.add(menu);
            cursor.moveToNext();
        }

        cursor.close();
        return menus;
    }

    public CmsMenu getMenuWithChildrenById(long id) {
        String table = CmsContract.CmsMenusTable.TABLE_NAME;

        final String[] allColumns =
                {
                        CmsContract.CmsMenusTable.COLUMN_NAME_MENU_ID,
                        CmsContract.CmsMenusTable.COLUMN_NAME_TITLE,
                        CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID,
                        CmsContract.CmsMenusTable.COLUMN_NAME_SEQUENCE//,
                        //"(select count(*) from cms_menus where parent_id = cms_menu_id)"
                };

        String selection = CmsContract.CmsMenusTable.COLUMN_NAME_MENU_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
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
        int count = getChildrenCount(id);
        return count > 0;
    }

    public int getChildrenCount(long id) {
        String table = CmsContract.CmsMenusTable.TABLE_NAME;
        String columnNameParentId = CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID;
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", table, columnNameParentId);

        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        //SQLiteStatement sqliteStatement = database.compileStatement(sql);
        //long count2 = sqliteStatement.simpleQueryForLong();

        return count;
    }

    public List<CmsMenu> getSiblingMenusById(long id, boolean includeSiblingWithChildren) {
        String table = CmsContract.CmsMenusTable.TABLE_NAME;
        String columnId = CmsContract.CmsMenusTable.COLUMN_NAME_MENU_ID;
        String columnParentId = CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID;
//        String format = "select * from %s where %s = (select %s from %s where %s = ?)";
//        String sql = String.format(format, table, columnParentId, columnParentId, table, columnId);

        // where parentId = (select parentId from CmsMenu where menuId = ?
        // and (select count(*) from CmsMenu where parentId = ) = 0)
        String format = "%s = (select %s from %s where %s = ?)";
        String selection = String.format(format, columnParentId, columnParentId, table, columnId);
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = database.query(table, allColumns, selection, selectionArgs, null, null, null);

        List<CmsMenu> menus = new ArrayList<CmsMenu>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CmsMenu menu = cursorToCmsMenu(cursor);

            int childrenCount = getChildrenCount(menu.getId()); // TODO improve
            menu.setChildrenCount(childrenCount);

            if (includeSiblingWithChildren || !hasChild(menu.getId())) { // TODO improve
                menus.add(menu);
            }

            cursor.moveToNext();
        }

        cursor.close();
        return menus;
    }

    //
    // Helpers

    private static CmsMenu cursorToCmsMenu(Cursor c) {
        long id = getLong(c, CmsContract.CmsMenusTable.COLUMN_NAME_MENU_ID);
        long parentId = getLong(c, CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID);
        String title = getString(c, CmsContract.CmsMenusTable.COLUMN_NAME_TITLE);
        return new CmsMenu(id, parentId, title);
    }

    private static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    private static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

}
