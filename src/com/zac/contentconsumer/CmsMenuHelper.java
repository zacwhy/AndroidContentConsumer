package com.zac.contentconsumer;

import com.zac.contentconsumer.cms.CmsMenu;

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

    public static int getPosition(long menuId, List<CmsMenu> menus) {
        for (int i = 0; i < menus.size(); i++) {
            CmsMenu menu = menus.get(i);
            if (menu.getId() == menuId) {
                return i;
            }
        }
        return -1;
    }

}
