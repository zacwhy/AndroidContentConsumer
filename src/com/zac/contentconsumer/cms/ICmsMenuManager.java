package com.zac.contentconsumer.cms;

import java.util.List;

public interface ICmsMenuManager {
    CmsMenu getRootMenu();
    CmsMenu getMenuWithChildrenById(long id);
    List<CmsMenu> getSiblingMenusById(long id, boolean includeBranches, boolean includeLeaves);
    List<CmsMenu> getMenusByParentId(long parentId);
}
