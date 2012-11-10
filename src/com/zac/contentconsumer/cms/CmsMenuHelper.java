package com.zac.contentconsumer.cms;

import java.util.ArrayList;
import java.util.List;

public final class CmsMenuHelper {
	
	public static String[] getMenuTitleArray(List<CmsMenu> menus) {
		List<String> list = new ArrayList<String>();

		for (CmsMenu menu : menus) {
			list.add(menu.getTitle());
		}

        return list.toArray(new String[list.size()]);
	}
	
}
