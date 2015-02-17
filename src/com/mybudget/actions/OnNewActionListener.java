package com.mybudget.actions;

import java.util.Calendar;
import com.mybudget.database.Month;
import com.mybudget.database.Record;

public interface OnNewActionListener {
	public Record onNewActionTaken(long idNote);

	public Month onUpdateSummaryElement(Month m, String state);

	public void onActionDateChange(Calendar c);
	
	public boolean isFormFilled();
}
