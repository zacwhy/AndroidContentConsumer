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
	public CmsMenu getRootMenuWithChildren() {
		open();
		CmsMenu menu = dataSource.getRootMenuWithChildren();
		close();
		return menu;
	}

	@Override
	public CmsMenu getMenuWithChildrenById(long id) {
		open();
		CmsMenu menu = dataSource.getMenuWithChildrenById(id);
		close();
		return menu;
	}

	@Override
	public boolean hasChild(long id) {
		open();
		boolean hasChild = dataSource.hasChild(id);
		close();
		return hasChild;
	}

    @Override
    public List<CmsMenu> getSiblingMenusById(long id) {
        open();
        List<CmsMenu> menus = dataSource.getSiblingMenusById(id);
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
