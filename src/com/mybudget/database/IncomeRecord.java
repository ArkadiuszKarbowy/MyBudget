package com.mybudget.database;

import android.content.ContentValues;

public class IncomeRecord implements Record {

	private static String TABLE_NAME = "Incomes";
	private ContentValues cValues;

	public IncomeRecord(String title, String date, double amount,
			double interest, long fIdNote) {
		cValues = new ContentValues();
		cValues.put("Title", title);
		cValues.put("Date", date);
		cValues.put("Amount", amount);
		cValues.put("Interest", interest);
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