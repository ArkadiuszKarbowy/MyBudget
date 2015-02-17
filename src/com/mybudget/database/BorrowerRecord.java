package com.mybudget.database;

import android.content.ContentValues;

public class BorrowerRecord implements Record {

	private static String TABLE_NAME = "Borrowers";
	private ContentValues cValues;

	public BorrowerRecord(String name, String note, String date, double amount,
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