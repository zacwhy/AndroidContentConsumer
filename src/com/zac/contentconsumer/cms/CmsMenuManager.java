package com.zac.contentconsumer.cms;

import com.zac.contentconsumer.database.CmsMenuDataSource;

import android.content.Context;

import java.util.List;

public class CmsMenuManager implements ICmsMenuManager {

    private Context context;
    private CmsMenuDataSource dataSource;

    public CmsMenuManager(Context context) {
        this.context = context;
    }

    @Override
    public CmsMenu getRootMenu() {
        List<CmsMenu> menus = getMenusByParentId(0);
        if (menus.isEmpty()) {
            return null;
        }
        return menus.get(0);
    }

    @Override
    public CmsMenu getMenuWithChildrenById(long id) {
        open();
        CmsMenu menu = dataSource.getMenuWithChildrenById(id);
        close();
        return menu;
    }

    @Override
    public List<CmsMenu> getSiblingMenusById(long id, boolean includeBranches, boolean includeLeaves) {
        open();
        List<CmsMenu> menus = dataSource.getSiblingMenusById(id, includeBranches, includeLeaves);
        close();
        return menus;
    }

    @Override
    public List<CmsMenu> getMenusByParentId(long parentId) {
        open();
        List<CmsMenu> menus = dataSource.getMenusByParentId(parentId);
        close();
        return menus;
    }

    private void open() {
        dataSource = new CmsMenuDataSource(context);
        dataSource.open();
    }

    private void close() {
        dataSource.close();
    }

}
