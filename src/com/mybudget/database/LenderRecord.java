package com.mybudget.database;

import android.content.ContentValues;

public class LenderRecord implements Record {

	private static String TABLE_NAME = "Lenders";
	private ContentValues cValues;

	public LenderRecord(String name, String note, String date, double amount,
			long fIdNote) {
		cValues = new ContentValues();
		cValues.put("Name", name);
		cValues.put("Note", note);
		cValues.put("Date", date);
		cValues.put("Amount", amount);
		cValues.put("fIdNote", fIdNote);
		cValues.put("tableAction", TABLE_NAME);
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