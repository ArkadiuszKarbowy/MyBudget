package com.mybudget.history;

import com.mybudget.R;


public class IncomesDetails extends FragmentDetails {


	public IncomesDetails(){
		this.viewLayout = R.layout.details_incomes;
		this.listView = R.id.incomes_list;
		this.table = "Incomes";
		this.from = new String[] { "Date", "Title", "Amount" };
		this.to = new int[] { R.id.oDate, R.id.oTitle, R.id.oAmount };
	}
}
