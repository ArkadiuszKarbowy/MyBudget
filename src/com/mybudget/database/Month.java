package com.mybudget.database;

import android.content.ContentValues;

public class Month implements Record {
	private static String TABLE_NAME = "Months";

	private long _id;
	private int name;
	private double income, outcome, net, savings, lend, borrowed;

	private ContentValues cValues;

	public Month(int name, double income, double outcome, double net,
			double savings, double lend, double borrowed) {
		this.name = name;
		this.income = income;
		this.outcome = outcome;
		this.net = net;
		this.savings = savings;
		this.lend = lend;
		this.borrowed = borrowed;
		cValues = new ContentValues();
	}

	private void prepareContentValues(int n, double i, double o, double net,
			double s, double l, double b) {
		cValues.put("Name", n);
		cValues.put("Income", i);
		cValues.put("Outcome", o);
		cValues.put("Net", net);
		cValues.put("Savings", s);
		cValues.put("Lend", l);
		cValues.put("Borrowed", b);
	}

	@Override
	public ContentValues getContentValues() {
		prepareContentValues(name, income, outcome, net, savings, lend,
				borrowed);
		return cValues;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	public void updateIncomes(double amount, double interest, String state) {
		double saved = amount * interest / 100.0;
		if (state == "INCREASE") {
			savings = savings + saved;
			income = income + amount;
		} else if (state == "DECREASE") {
			savings = savings - saved;
			income = income - amount;
		}
		updateNet();
	}

	public void updateOutcomes(double amount, String state) {
		if (state == "INCREASE")
			outcome = outcome + amount;
		else if (state == "DECREASE")
			outcome = outcome - amount;

		updateNet();
	}

	public void updateBorrowed(double amount, String state) {
		if (state == "INCREASE")
			borrowed = borrowed + amount;
		else if (state == "DECREASE")
			borrowed = borrowed - amount;

		updateNet();
	}

	public void updateLend(double amount, String state) {
		if (state == "INCREASE")
			lend = lend + amount;
		else if (state == "DECREASE")
			lend = lend - amount;

		updateNet();
	}

	private void updateNet() {
		net = income - outcome - savings - lend + borrowed;
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}
}