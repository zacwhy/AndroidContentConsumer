package com.zac.contentconsumer.cms;

import java.util.List;

public interface ICmsMenuManager {
	CmsMenu getRootMenuWithChildren();
	CmsMenu getMenuWithChildrenById(long id);
	boolean hasChild(long id);
    List<CmsMenu> getSiblingMenusById(long id);
}
