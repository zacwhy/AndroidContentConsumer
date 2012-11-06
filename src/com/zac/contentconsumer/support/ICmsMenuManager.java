package com.zac.contentconsumer.support;

public interface ICmsMenuManager {
	CmsMenu getRootMenuWithChildren();
	CmsMenu getMenuWithChildrenById(long id);
	boolean hasChild(long id);
}
