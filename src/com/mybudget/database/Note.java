package com.mybudget.database;

import android.content.ContentValues;

public class Note implements Record {

	private static String TABLE_NAME = "Notes";
	private ContentValues cValues;
	
	public Note(long fIdBalance) {
		cValues = new ContentValues();
		cValues.put("fIdBalance", fIdBalance);
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