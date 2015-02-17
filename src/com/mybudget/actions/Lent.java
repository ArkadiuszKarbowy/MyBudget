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
import com.mybudget.database.LenderRecord;
import com.mybudget.database.Month;
import com.mybudget.database.Record;

public class Lent extends Fragment implements OnNewActionListener {

	private EditText name, note, amount;

	private double atrAmount;
	private String atrDate;

	private OnFragmentVisibleListener fListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.lend, container, false);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			fListener = (OnFragmentVisibleListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onFragmentVisibleListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();

		name = (EditText) view.findViewById(R.id.lend_name_surname);
		note = (EditText) view.findViewById(R.id.lend_note);
		amount = (EditText) view.findViewById(R.id.lend_amount);
		amount.setFilters(new InputFilter[] {new DecimalDigitsFilter(2)});

		fListener.passVisibleFragment(this);
	}

	@Override
	public boolean isFormFilled() {
		return name.length() != 0 && note.length() !=0 && amount.length() !=0;
	}
	
	@Override
	public void onActionDateChange(Calendar c) {
		atrDate = new SimpleDateFormat("d MMMM y", Locale.US).format(c
				.getTime());
	}

	@Override
	public Record onNewActionTaken(long idNote) {
		String atrName = name.getText().toString();
		String atrDescript = note.getText().toString();
		atrAmount = Double.parseDouble(amount.getText().toString());

		return new LenderRecord(atrName, atrDescript, atrDate, atrAmount,
				idNote);
	}

	@Override
	public Month onUpdateSummaryElement(Month m, String state) {
		m.updateLend(atrAmount, state);
		return m;
	}
}