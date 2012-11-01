package com.example.contentconsumer.support;

import android.content.Context;

public class ContentMenuFactory {
	
	private static ContentMenuManager menuManager;
	
	public static ContentMenuManager getMenuManager(Context context) {
		// todo dictionary
		if (menuManager == null) {
			menuManager = new ContentMenuManager(context);
		}
		return menuManager;
	}
	
}
