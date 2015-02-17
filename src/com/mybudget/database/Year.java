package com.mybudget.database;

import android.content.ContentValues;

public class Year implements Record {
	private static String TABLE_NAME = "Years";
	private ContentValues cValues;

	public Year(String name) {
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