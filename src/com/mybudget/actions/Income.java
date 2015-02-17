package com.mybudget.actions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mybudget.R;
import com.mybudget.basicStatus.DecimalDigitsFilter;
import com.mybudget.database.BalanceSheetAdapter;
import com.mybudget.database.IncomeRecord;
import com.mybudget.database.Month;
import com.mybudget.database.Record;

public class Income extends Fragment implements OnNewActionListener {

	private EditText title, amount;

	private double atrAmount, atrInterest;
	private String atrTitle, atrDate;

	private OnFragmentVisibleListener fListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			fListener = (OnFragmentVisibleListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentVisibleListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.income, container,
				false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		title = (EditText) getView().findViewById(R.id.income_title);
		amount = (EditText) getView().findViewById(R.id.income_amount);
		amount.setFilters(new InputFilter[] {new DecimalDigitsFilter(2)});
		
		setInterest();
		fListener.passVisibleFragment(this);
	}
	
	@Override
	public boolean isFormFilled() {
		return title.length() != 0 && amount.length() !=0;
	}

	@Override
	public void onActionDateChange(Calendar c) {
		atrDate = new SimpleDateFormat("d MMMM y", Locale.US).format(c
				.getTime());
	}

	@Override
	public Record onNewActionTaken(long idNote) {
		atrTitle = title.getText().toString();
		atrAmount = Double.parseDouble(amount.getText().toString());
		return new IncomeRecord(atrTitle, atrDate, atrAmount, atrInterest, idNote);
	}

	@Override
	public Month onUpdateSummaryElement(Month m, String state) {
		m.updateIncomes(atrAmount, atrInterest, state);
		return m;
	}

	private void setInterest(){
		BalanceSheetAdapter database = new BalanceSheetAdapter(getActivity());
		database.open();
		atrInterest = database.getCurrentInterest();
		database.close();
	}
}