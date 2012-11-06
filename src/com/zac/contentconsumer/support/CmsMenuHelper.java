package com.zac.contentconsumer.support;

import java.util.ArrayList;
import java.util.List;

public final class CmsMenuHelper {
	
	public static String[] getMenuTitleArray(List<CmsMenu> menus) {
		List<String> list = new ArrayList<String>();

		for (CmsMenu menu : menus) {
			list.add(menu.getTitle());
		}

		String[] array = list.toArray(new String[list.size()]);
		return array;
	}
	
}
