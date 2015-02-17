package com.mybudget.history;

import com.mybudget.R;

public class BorrowersDetails extends FragmentDetails {

	public BorrowersDetails() {
		this.viewLayout = R.layout.details_borrowers;
		this.listView = R.id.borrowers_list;
		this.table = "Borrowers";
		this.from = new String[] { "Date", "Name", "Amount" };
		this.to = new int[] { R.id.oDate, R.id.oTitle, R.id.oAmount };
	}
}
