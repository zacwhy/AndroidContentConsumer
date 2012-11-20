package com.zac.contentconsumer.cms;

import java.util.List;

public interface ICmsMenuManager {
    CmsMenu getRootMenu();
    List<CmsMenu> getMenusByParentId(long parentId);
    List<CmsMenu> getSiblingMenusById(long id, boolean includeBranches, boolean includeLeaves);
}
