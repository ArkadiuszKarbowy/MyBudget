package com.mybudget.database;

import android.content.ContentValues;

public class Category implements Record {
	private static String TABLE_NAME = "Categories";
	private ContentValues cValues;

	public Category(String name) {
		cValues = new ContentValues();
		cValues.put("Name", name);
	}

	@Override
	public ContentValues getContentValues() {
		return cValues;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}