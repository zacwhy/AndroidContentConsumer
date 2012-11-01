package com.example.contentconsumer.support;

//import java.util.List;

import android.content.Context;

//import com.example.contentconsumer.database.DbMenu;
//import com.example.contentconsumer.database.DbMenuDataSource;

public class ContentMenuFactory {
	
	private static ContentMenuManager menuManager;
	//private static ContentMenu menus;
	
	public static ContentMenuManager getMenuManager(Context context) {
		// todo dictionary
		if (menuManager == null) {
			menuManager = new ContentMenuManager(context);
		}
		return menuManager;
	}
	
//	public static ContentMenu getMenus(Context context) {
//		if (menus == null) {
//			menus = buildMenus(context);
//		}
//		return menus;
//	}
//	
//	public static ContentMenu getMenus() {
//		if (menus == null) {
//			menus = buildMenus();
//		}
//		return menus;
//	}
//	
//	public static ContentMenu findMenuById(long id) {
//		return ContentMenuFactory.getMenus().findDescendantById(id);
//	}
//	
//	private static ContentMenu buildMenus(Context context) {
//		ContentMenu rootMenu = ContentMenu.newRootMenu();
//		
//		List<DbMenu> dbMenus = getAllMenus(context);
//		
//		return rootMenu;
//	}
//	
//	private static List<DbMenu> getAllMenus(Context context) {
//		DbMenuDataSource dataSource = new DbMenuDataSource(context);
//		dataSource.open();
//		
//		initDb(dataSource); // todo remove
//		
//		List<DbMenu> menus = dataSource.getAllMenus();
//		dataSource.close();
//		return menus;
//	}
//	
//	// todo remove
//	private static void initDb(DbMenuDataSource dataSource) {
//		DbMenu menu1 = dataSource.insertMenu("Menu 1", 0, 1);
//		DbMenu menu2 = dataSource.insertMenu("Menu 2", 0, 2);
//		DbMenu menu3 = dataSource.insertMenu("Menu 3", 0, 3);
//		
//		DbMenu menu1_1 = dataSource.insertMenu("Menu 1-1", menu1.getId(), 1);
//		DbMenu menu1_2 = dataSource.insertMenu("Menu 1-2", menu1.getId(), 1);
//		DbMenu menu1_3 = dataSource.insertMenu("Menu 1-3", menu1.getId(), 1);
//		
//		DbMenu menu2_1 = dataSource.insertMenu("Menu 2-1", menu2.getId(), 1);
//		DbMenu menu2_2 = dataSource.insertMenu("Menu 2-2", menu2.getId(), 1);
//		
//		DbMenu menu3_1 = dataSource.insertMenu("Menu 3-1", menu3.getId(), 1);
//		
//		DbMenu menu1_1_1 = dataSource.insertMenu("Menu 1-1-1", menu1_1.getId(), 1);
//	}
//	
//	private static ContentMenu buildMenus() {
//		ContentMenu rootMenu = ContentMenu.newRootMenu();
//
//		// todo get data from SQLite
//		ContentMenu menu1 = createNewMenu(1, "Menu 1", rootMenu);
//
//		ContentMenu menu1_1 = createNewMenu(2, "Menu 1-1", menu1);
//		createNewMenu(3, "Menu 1-2", menu1);
//		createNewMenu(4, "Menu 1-3", menu1);
//
//		createNewMenu(5, "Menu 1-1-1", menu1_1);
//
//		ContentMenu menu2 = createNewMenu(6, "Menu 2", rootMenu);
//
//		createNewMenu(7, "Menu 2-1", menu2);
//
//		ContentMenu menu3 = createNewMenu(8, "Menu 3", rootMenu);
//
//		createNewMenu(9, "Menu 3-1", menu3);
//		
//		createNewMenu(10, "The quick brown fox jumps over the lazy dog.", rootMenu);
//
//		return rootMenu;
//	}
//	
//	private static ContentMenu createNewMenu(long id, String title, ContentMenu parent) {
//		ContentMenu menu = new ContentMenu(id, title, parent);
//		parent.getChildren().add(menu);
//		return menu;
//	}
	
}
