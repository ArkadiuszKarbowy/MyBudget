package com.mybudget.database;

import android.content.ContentValues;

public class OutcomeRecord implements Record {

	private static String TABLE_NAME = "Outcomes";
	private ContentValues cValues;

	public OutcomeRecord(String title, String date, double amount,
			long fIdCategory, long fIdNote) {
		cValues = new ContentValues();
		cValues.put("Title", title);
		cValues.put("Date", date);
		cValues.put("Amount", amount);
		cValues.put("fIdCategory", fIdCategory);
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