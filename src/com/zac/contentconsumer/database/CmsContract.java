package com.zac.contentconsumer.database;

import android.provider.BaseColumns;

public class CmsContract {

	// Prevents the class from being instantiated.
	private CmsContract() {}

	public static abstract class CmsMenusTable implements BaseColumns {
		public static final String TABLE_NAME = "cms_menus";
        public static final String COLUMN_NAME_MENU_ID = "cms_menu_id";
        public static final String COLUMN_NAME_PARENT_ID = "parent_id";
		public static final String COLUMN_NAME_SEQUENCE = "sequence";
        public static final String COLUMN_NAME_TITLE = "title";
    }
	
	public static abstract class CmsContentsTable implements BaseColumns {
		public static final String TABLE_NAME = "cms_contents";
		public static final String COLUMN_NAME_CMS_MENU_ID = "cms_menu_id";
		public static final String COLUMN_NAME_CONTENT = "content";
	}

}
