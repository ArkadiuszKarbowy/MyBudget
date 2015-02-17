package com.mybudget.database;

import android.content.ContentValues;

public class BalanceByYear implements Record{

	private static String TABLE_NAME = "BalanceByYear";
	private ContentValues cValues;

	public BalanceByYear(long fIdYear, long fIdMonth) {
		cValues = new ContentValues();
		cValues.put("fIdYear", fIdYear);
		cValues.put("fIdMonth", fIdMonth);
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