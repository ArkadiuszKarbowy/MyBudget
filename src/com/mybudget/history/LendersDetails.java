package com.mybudget.history;

import com.mybudget.R;

public class LendersDetails extends FragmentDetails {
	
	public LendersDetails(){
		this.viewLayout = R.layout.details_lenders;
		this.listView = R.id.lenders_list;
		this.table = "Lenders";
		this.from = new String[] { "Date", "Name", "Amount" };
		this.to = new int[] { R.id.oDate, R.id.oTitle, R.id.oAmount };
	}
}
