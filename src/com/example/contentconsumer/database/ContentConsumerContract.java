package com.example.contentconsumer.database;

import android.provider.BaseColumns;

public class ContentConsumerContract {

	// Prevents the FeedReaderContract class from being instantiated.
	private ContentConsumerContract() {
	}

	public static abstract class DbMenu implements BaseColumns {
		public static final String TABLE_NAME = "menus";
		public static final String COLUMN_NAME_ID = "_id";
		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_PARENT_ID = "parent_id";
		public static final String COLUMN_NAME_SEQUENCE = "sequence";
	}

}
