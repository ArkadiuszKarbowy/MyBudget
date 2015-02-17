package com.mybudget.actions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mybudget.R;
import com.mybudget.basicStatus.DecimalDigitsFilter;
import com.mybudget.database.BalanceSheetAdapter;
import com.mybudget.database.Month;
import com.mybudget.database.OutcomeRecord;
import com.mybudget.database.Record;

public class Outcome extends Fragment implements OnItemSelectedListener,
		OnNewActionListener {

	private TextView title, amount;
	private Spinner spinner;
	private double atrAmount;
	private String atrDate, atrTitle;
	private long idCategory, idAddCategory;

	private SimpleCursorAdapter adapter;
	private BalanceSheetAdapter database;
	private OnFragmentVisibleListener fListener;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.outcome, container,
				false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();

		database = new BalanceSheetAdapter(getActivity());

		title = (TextView) view.findViewById(R.id.outcome_title);
		amount = (TextView) view.findViewById(R.id.outcome_amount);
		amount.setFilters(new InputFilter[] {new DecimalDigitsFilter(2)});
		spinner = (Spinner) view.findViewById(R.id.outcome_category_list);

		adapter = inflateAdapter();
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		fListener.passVisibleFragment(this);
	}

	private SimpleCursorAdapter inflateAdapter() {
		database.open(); 
		Cursor cursor = database.getAllCategories();
		idAddCategory = cursor.getCount();
		database.close();
		return new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, cursor,
				new String[] { "Name" }, new int[] { android.R.id.text1 }, 0);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		Cursor c = (Cursor) parent.getAdapter().getItem(pos);
		idCategory = c.getInt(0);

		if (idCategory == idAddCategory)
			showAlert();
	}

	private void showAlert() {
		final EditText input = new EditText(getActivity());

		new AlertDialog.Builder(getActivity())
				.setMessage("Add your category")
				.setView(input)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Editable editable = input.getText();
								database.open();
								database.insertNewCategory(editable.toString(),
										idAddCategory);
								database.close();
								adapter = inflateAdapter();
								spinner.setAdapter(adapter);
								adapter.notifyDataSetChanged();
								spinner.setSelection((int)idAddCategory-2); //coz setSelection is zero-based
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								spinner.setSelection(0);
							}
						}).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
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
		atrAmount = Double.parseDouble(amount.getText().toString());
		atrTitle = title.getText().toString();
		return new OutcomeRecord(atrTitle, atrDate, atrAmount, idCategory,
				idNote);
	}

	@Override
	public Month onUpdateSummaryElement(Month m, String state) {
		m.updateOutcomes(atrAmount, state);
		return m;
	}
}