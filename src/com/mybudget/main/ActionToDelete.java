package com.mybudget.main;

import com.mybudget.database.BalanceSheetAdapter;

public class ActionToDelete {
	private int fIdNote;
	private String table;
	private double amount;

	public ActionToDelete(int n, String t, double a) {
		fIdNote = n;
		table = t;
		amount = a;
	}

	public void delete(BalanceSheetAdapter db) {
		db.open();
		db.deleteSelectedAction(fIdNote, table, amount);
		db.close();
	}
}
