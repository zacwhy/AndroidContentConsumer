package com.zac.contentconsumer.cms;

import java.util.List;

public interface ICmsMenuManager {
    CmsMenu getRootMenu();
    CmsMenu getMenuById(long id, boolean includeChildren);
    List<CmsMenu> getMenusByParentId(long parentId);
    List<CmsMenu> getSiblingMenusById(long id, boolean includeBranches, boolean includeLeaves);
}
