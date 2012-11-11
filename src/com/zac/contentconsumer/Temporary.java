package com.zac.contentconsumer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zac.contentconsumer.cms.CmsContent;
import com.zac.contentconsumer.database.CmsContentDataSource;
import com.zac.contentconsumer.database.CmsMenuDataSource;
import com.zac.contentconsumer.database.CmsSQLiteOpenHelper;
import com.zac.contentconsumer.cms.CmsMenu;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Temporary {

    final static String TAG_MENUS = "menus";
    final static String TAG_MENU_ID = "id";
    final static String TAG_MENU_PARENT_ID = "parentId";
    final static String TAG_MENU_TITLE = "title";
    final static String TAG_CONTENT = "content";

    private SQLiteDatabase database;

	public static void recreateCms(Context context) {
        Temporary temporary = new Temporary();
        temporary.recreateCms2(context);
    }

    private void recreateCms2(Context context) {
        CmsSQLiteOpenHelper dbHelper = new CmsSQLiteOpenHelper(context);
        database = dbHelper.getWritableDatabase();
		recreateDatabase();
		database.close();
		dbHelper.close();
	}
	
	private void recreateDatabase() {
		CmsMenuDataSource.deleteAllMenus(database);
		CmsContentDataSource.deleteAllContents(database);
		createDatabase();
	}
	
	private void createDatabase() {
        String json = getJson();
        parseJson(json);
	}

    private StringBuilder jsonStringBuilder;

    private String getJson() {
        jsonStringBuilder = new StringBuilder();

        StringBuilder s = jsonStringBuilder;
        s.append("{");
        s.append("'menus':[");

        s.append("{'id':1,'parentId':0,'title':'Home','position':0}");

        addMenu(2, 1, "Menu 1");
        addMenu(3, 2, "Menu 1-1");
        addMenu(4, 3, "Menu 1-1-1", "This is the content for menu 1-1-1.");

        addMenu(5, 1, "Menu 2");
        addMenu(6, 5, "Menu 2-1", "This is the content for menu 2-1.");
        addMenu(7, 5, "Menu 2-2", "This is the content for menu 2-2.");
        addMenu(8, 5, "Menu 2-3", "This is the content for menu 2-3.");
        addMenu(9, 5, "Menu 2-4", "This is the content for menu 2-4.");
        addMenu(10, 5, "Menu 2-5", "This is the content for menu 2-5.");

        addMenu(11, 1, "The quick brown fox jumps over the lazy dog.",
                "<b>The lazy dog jumps over the quick brown fox.</b>");

        s.append("]");
        s.append("}");
        return s.toString();
    }

    private void addMenu(long id, long parentId, String title) {
        addMenu(id, parentId, title, null);
    }

    private void addMenu(long id, long parentId, String title, String content) {
        StringBuilder s = new StringBuilder();
        s.append(String.format(",{'id':%d,'parentId':%d,'title':'%s','position':0", id, parentId, title));

        if (content != null) {
            s.append(String.format(",'content':'%s'", content));
        }

        s.append("}");

        jsonStringBuilder.append(s);
    }

    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray menuArray = jsonObject.getJSONArray(TAG_MENUS);

            for (int i = 0; i < menuArray.length(); i++) {
                try {
                    JSONObject m = menuArray.getJSONObject(i);
                    long menuId = m.getLong(TAG_MENU_ID);

                    CmsMenu cmsMenu = jsonObjectToCmsMenu(menuId, m);
                    insertMenu(cmsMenu);

                    CmsContent cmsContent = jsonObjectToCmsContent(menuId, m);
                    insertContent(cmsContent);
                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private CmsMenu jsonObjectToCmsMenu(long menuId, JSONObject m) throws JSONException {
        long parentId = m.getLong(TAG_MENU_PARENT_ID);
        String title = m.getString(TAG_MENU_TITLE);
        return new CmsMenu(menuId, parentId, title);
    }

    private CmsContent jsonObjectToCmsContent(long menuId, JSONObject m) throws JSONException {
        String content = m.getString(TAG_CONTENT);
        return new CmsContent(menuId, content);
    }

    private long insertMenu(CmsMenu cmsMenu) {
        return CmsMenuDataSource.insertMenu(database, cmsMenu);
    }

    private long insertContent(CmsContent cmsContent) {
        return CmsContentDataSource.insertContent(database, cmsContent);
    }

}
