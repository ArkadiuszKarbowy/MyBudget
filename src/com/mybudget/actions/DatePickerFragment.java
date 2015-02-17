package com.mybudget.actions;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class DatePickerFragment extends DialogFragment {

	private OnDateSetListener fOnDateSetListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		try {
			fOnDateSetListener = (OnDateSetListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString()
					+ " must implement OnDateSetListener");
		}

		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(getActivity(), fOnDateSetListener, year,
				month, day);
	}
}