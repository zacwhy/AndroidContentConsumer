package com.example.contentconsumer.support;

import java.util.ArrayList;
import java.util.List;

public final class ContentMenuHelper {
	
	public static ContentMenu findDescendantMenuById(long id, ContentMenu menu) {
		if (menu.getId() == id) {
			return menu;
		}

		for (ContentMenu child : menu.getChildren()) {
			ContentMenu result = findDescendantMenuById(id, child);

			if (result != null) {
				return result;
			}
		}

		return null; 
	}

	public static String[] getMenuTextArray(List<ContentMenu> menus) {
		List<String> list = new ArrayList<String>();

		for (ContentMenu menu : menus) {
			list.add(menu.getTitle());
		}

		String[] array = list.toArray(new String[list.size()]);
		return array;
	}
	
}
