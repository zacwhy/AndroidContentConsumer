package com.example.contentconsumer.support;

import java.util.ArrayList;
import java.util.List;

import com.example.contentconsumer.database.DbMenu;
import com.example.contentconsumer.database.DbMenuDataSource;

import android.content.Context;

public class ContentMenuManager {
	
	private Context context;
	private ContentMenu menus;
	
	public ContentMenuManager(Context context) {
		this.context = context;
	}
	
	public ContentMenu getMenus() {
		if (menus == null) {
			menus = buildMenus();
		}
		return menus;
	}
	
	public ContentMenu findMenuById(long id) {
		return getMenus().findDescendantById(id);
	}
	
	
	private ContentMenu buildMenus() {
		List<DbMenu> allDbMenus = getAllMenus(context);
		ContentMenu rootMenu = getRootMenuFromDbMenus(allDbMenus);
		return rootMenu;
	}
	
	private static ContentMenu getRootMenuFromDbMenus(List<DbMenu> dbMenus) {
		return getMenuFromDbMenus(0, null, dbMenus);
	}
	
	private static ContentMenu getMenuFromDbMenus(long id, ContentMenu parentMenu, List<DbMenu> dbMenus) {
		ContentMenu menu;
		
		if (parentMenu == null) {
			menu = new ContentMenu(id);
		} else {
			DbMenu dbMenu = findDbMenuById(id, dbMenus);
			menu = createNewMenu(dbMenu.getId(), dbMenu.getTitle(), parentMenu);
			
			dbMenus.remove(dbMenu); // improve efficiency by removing added menus
		}
		
		List<DbMenu> childDbMenus = getChildDbMenusById(menu.getId(), dbMenus);
		for (DbMenu childDbMenu : childDbMenus) {
			getMenuFromDbMenus(childDbMenu.getId(), menu, dbMenus);
		}
		
		return menu;
	}
	
	private static DbMenu findDbMenuById(long id, List<DbMenu> dbMenus) {
		for (DbMenu dbMenu : dbMenus) {
			if (dbMenu.getId() == id) {
				return dbMenu;
			}
		}
		return null;
	}
	
	private static ContentMenu createNewMenu(long id, String title, ContentMenu parent) {
		ContentMenu menu = new ContentMenu(id, title, parent);
		parent.addChild(menu);
		return menu;
	}

	private static List<DbMenu> getChildDbMenusById(long id, List<DbMenu> allDbMenus) {
		List<DbMenu> dbMenus = new ArrayList<DbMenu>();
		for (DbMenu dbMenu : allDbMenus) {
			if (dbMenu.getParentId() == id) {
				dbMenus.add(dbMenu);
			}
		}
		return dbMenus;
	}
	
	
	private List<DbMenu> getAllMenus(Context context) {
		DbMenuDataSource dataSource = new DbMenuDataSource(context);
		dataSource.open();
		
		recreateDbMenus(dataSource); // todo remove
		
		List<DbMenu> menus = dataSource.getAllMenus();
		dataSource.close();
		return menus;
	}
	
	// todo remove
	private void recreateDbMenus(DbMenuDataSource dataSource) {
		dataSource.deleteAllMenus();
		createDbMenus(dataSource);
	}
	
	// todo remove
	private void createDbMenus(DbMenuDataSource dataSource) {
		DbMenu rootMenu = dataSource.insertMenu("Home", 0, 1);
		long rootMenuId = rootMenu.getId();
		
		DbMenu menu1 = dataSource.insertMenu("Menu 1", rootMenuId, 1);
		DbMenu menu2 = dataSource.insertMenu("Menu 2", rootMenuId, 2);
		DbMenu menu3 = dataSource.insertMenu("Menu 3", rootMenuId, 3);
		DbMenu menu4 = dataSource.insertMenu("The quick brown fox jumps over the lazy dog.", rootMenuId, 4);
		
		DbMenu menu1_1 = dataSource.insertMenu("Menu 1-1", menu1.getId(), 1);
		DbMenu menu1_2 = dataSource.insertMenu("Menu 1-2", menu1.getId(), 2);
		DbMenu menu1_3 = dataSource.insertMenu("Menu 1-3", menu1.getId(), 3);
		
		DbMenu menu2_1 = dataSource.insertMenu("Menu 2-1", menu2.getId(), 1);
		DbMenu menu2_2 = dataSource.insertMenu("Menu 2-2", menu2.getId(), 2);
		
		DbMenu menu3_1 = dataSource.insertMenu("Menu 3-1", menu3.getId(), 1);
		
		DbMenu menu1_1_1 = dataSource.insertMenu("Menu 1-1-1", menu1_1.getId(), 1);
	}
	
}
