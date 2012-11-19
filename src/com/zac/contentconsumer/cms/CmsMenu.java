package com.zac.contentconsumer.cms;

import java.util.List;

public class CmsMenu {

    private long id;
    private String title;
    private long parentId;
    private int sequence;
    private int childrenCount;
    private List<CmsMenu> children;

    public CmsMenu(long id, long parentId, String title) {
        setId(id);
        setParentId(parentId);
        setTitle(title);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }


    public List<CmsMenu> getChildren() {
        return children;
    }

    public void setChildren(List<CmsMenu> children) {
        this.children = children;
    }


    public boolean isRoot() {
        return getParentId() == 0;
    }

    public boolean hasChild() {
        return getChildrenCount() > 0;
    }

}
