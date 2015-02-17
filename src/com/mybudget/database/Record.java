package com.mybudget.database;

import android.content.ContentValues;

public interface Record {
	public ContentValues getContentValues();

	public String getTableName();
}
