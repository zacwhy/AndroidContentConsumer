package com.zac.contentconsumer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.zac.contentconsumer.cms.CmsMenu;

import java.util.ArrayList;
import java.util.List;

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

    public List<CmsMenu> getMenusByParentId(long parentId) {
        String table = CmsContract.CmsMenusTable.TABLE_NAME;
        String selection = CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(parentId)};
        String orderBy = CmsContract.CmsMenusTable.COLUMN_NAME_SEQUENCE;
        Cursor cursor = database.query(table, allColumns, selection, selectionArgs, null, null, orderBy);
        return cursorToCmsMenus(cursor);
    }

    public CmsMenu getMenuById(long id, boolean includeChildren) {
        String table = CmsContract.CmsMenusTable.TABLE_NAME;
        String selection = CmsContract.CmsMenusTable.COLUMN_NAME_MENU_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = database.query(table, allColumns, selection, selectionArgs, null, null, null);

        CmsMenu cmsMenu = null;

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            cmsMenu = cursorToCmsMenu(cursor);
        }
        cursor.close();

        if (cmsMenu == null) {
            return null;
        }

        if (includeChildren) {
            List<CmsMenu> children = getMenusByParentId(cmsMenu.getId()); // TODO improve
            cmsMenu.setChildren(children);
        }

        return cmsMenu;
    }

    public List<CmsMenu> getSiblingMenusById(long id, boolean includeBranches, boolean includeLeaves) {
        String table = CmsContract.CmsMenusTable.TABLE_NAME;
        String columnId = CmsContract.CmsMenusTable.COLUMN_NAME_MENU_ID;
        String columnParentId = CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID;

        // WHERE parentId = (SELECT top 1 parentId FROM cms_menus WHERE menu_id = ?)
        String format = "%s = (SELECT %s FROM %s WHERE %s = ?)";
        String selection = String.format(format, columnParentId, columnParentId, table, columnId);
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = database.query(table, allColumns, selection, selectionArgs, null, null, null);

        List<CmsMenu> filteredMenus = new ArrayList<CmsMenu>(); // TODO improve
        List<CmsMenu> cmsMenus = cursorToCmsMenus(cursor);

        for (CmsMenu cmsMenu : cmsMenus) {
            if (includeBranches && cmsMenu.hasChild()) {
                filteredMenus.add(cmsMenu);
            }
            if (includeLeaves && !cmsMenu.hasChild()) {
                filteredMenus.add(cmsMenu);
            }
        }

        return filteredMenus;
    }

    public int getChildrenCount(long id) {
        String table = CmsContract.CmsMenusTable.TABLE_NAME;
        String columnNameParentId = CmsContract.CmsMenusTable.COLUMN_NAME_PARENT_ID;
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", table, columnNameParentId);

        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    //
    // Helpers

    private List<CmsMenu> cursorToCmsMenus(Cursor cursor) {
        List<CmsMenu> cmsMenus = new ArrayList<CmsMenu>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CmsMenu cmsMenu = cursorToCmsMenu(cursor);

            int childrenCount = getChildrenCount(cmsMenu.getId()); // TODO improve
            cmsMenu.setChildrenCount(childrenCount);

            cmsMenus.add(cmsMenu);
            cursor.moveToNext();
        }

        cursor.close();
        return cmsMenus;
    }

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
