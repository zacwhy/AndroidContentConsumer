package com.zac.contentconsumer.support;

import com.zac.contentconsumer.database.CmsContentDataSource;
import android.content.Context;

public class CmsContentManager implements ICmsContentManager {

	private Context context;
	CmsContentDataSource dataSource;
	
	public CmsContentManager(Context context) {
		this.context = context;
	}
	
	@Override
	public CmsContent getContentByMenuId(long menuId) {
		open();
		CmsContent content = dataSource.getContentByMenuId(menuId);
		close();
		return content;
	}
	
	private void open() {
		dataSource = new CmsContentDataSource(context);
		dataSource.open();
	}
	
	private void close() {
		dataSource.close();
	}

}
