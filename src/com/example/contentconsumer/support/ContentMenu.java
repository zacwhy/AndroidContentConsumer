package com.example.contentconsumer.support;

import java.util.ArrayList;
import java.util.List;

public class ContentMenu {
	
	private long id;
	private String title;
	
	private ContentMenu parent;
	private List<ContentMenu> children;
	
	public ContentMenu(long id, String title, ContentMenu parent) {
		setId(id);
		setTitle(title);
		this.parent = parent;
	}
	
	public ContentMenu(long id) {
		setId(id);
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
	
	
	public ContentMenu getParent() {
		return parent;
	}
	
	public List<ContentMenu> getChildren() {
		if (children == null) {
			children = new ArrayList<ContentMenu>();
		}
		return children;
	}
	
	public ContentMenu getFirstChild() {
		if (!hasChildren()) {
			return null;
		}
		return getChildren().get(0);
	}
	
	public void addChild(ContentMenu child) {
		getChildren().add(child);
	}
	
	
	public long getChildrenCount() {
		return getChildren().size();
	}
	
	public Boolean hasChildren() {
		return children != null && getChildrenCount() > 0;
	}
	
	public Boolean isRoot() {
		return getId() == 0;
	}
	
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return getTitle();
	}
	
	public ContentMenu findDescendantById(long id) {
		return ContentMenuHelper.findDescendantMenuById(id, this);
	}

}
