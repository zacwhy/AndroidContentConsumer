package com.zac.contentconsumer.cms;

public interface ICmsMenuManager {
	CmsMenu getRootMenuWithChildren();
	CmsMenu getMenuWithChildrenById(long id);
	boolean hasChild(long id);
}
