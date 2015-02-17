package com.mybudget.actions;

import java.util.Calendar;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;

import com.mybudget.R;
import com.mybudget.database.BalanceSheetAdapter;
import com.mybudget.database.Month;
import com.mybudget.database.Record;

public class Actions extends Activity implements OnFragmentVisibleListener,
		OnClickListener, OnDateSetListener {

	private static final String ON_INCREASE = "INCREASE";
	public Fragment currentVisible;
	private BalanceSheetAdapter database;
	OnNewActionListener fCallBack;
	Calendar date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setActionBar();
		createTabs(getActionBar());

		date = Calendar.getInstance();
		database = new BalanceSheetAdapter(this);

		if (savedInstanceState != null) {
			int index = savedInstanceState.getInt("selected_tab_index", 0);
			getActionBar().setSelectedNavigationItem(index);
		}
	}

	private void setActionBar() {
		LayoutInflater inflater = (LayoutInflater) getActionBar()
				.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = inflater.inflate(
				R.layout.custom_actionbar_done, null);

		customActionBarView.findViewById(R.id.actionbar_done)
				.setOnClickListener(this);

		customizeActionBarOptions(customActionBarView);
	}

	private void customizeActionBarOptions(View custom) {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_SHOW_TITLE
						| ActionBar.DISPLAY_HOME_AS_UP
						| ActionBar.DISPLAY_USE_LOGO);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setCustomView(custom);
	}

	private void createTabs(ActionBar actionBar) {
		String tag = getResources().getString(R.string.income);
		Tab tab = actionBar
				.newTab()
				.setText(R.string.income)
				.setTabListener(
						new TabListener<Income>(this, tag, Income.class));
		actionBar.addTab(tab);

		tag = getResources().getString(R.string.outcome);
		tab = actionBar
				.newTab()
				.setText(R.string.outcome)
				.setTabListener(
						new TabListener<Outcome>(this, tag, Outcome.class));
		actionBar.addTab(tab);

		tag = getResources().getString(R.string.lend);
		tab = actionBar.newTab().setText(R.string.lend)
				.setTabListener(new TabListener<Lent>(this, tag, Lent.class));
		actionBar.addTab(tab);

		tag = getResources().getString(R.string.borrowed);
		tab = actionBar
				.newTab()
				.setText(R.string.borrowed)
				.setTabListener(
						new TabListener<Borrowed>(this, tag, Borrowed.class));
		actionBar.addTab(tab);
	}

	@Override
	public void passVisibleFragment(Fragment f) {
		currentVisible = f;
	}

	@Override
	public void onClick(View view) {
		fCallBack = (OnNewActionListener) currentVisible;

		if (fCallBack.isFormFilled()) {
			long idNote = registryNewAction();
			updateRelatedMonthSummary(idNote);

			setResult(RESULT_OK);
		} else {
			setResult(RESULT_CANCELED);
		}

		finish();
	}

	private long registryNewAction() {
		fCallBack.onActionDateChange(date);
		String y =  Integer.toString(date.get(Calendar.YEAR));
		int m = date.get(Calendar.MONTH)+1; //zero-based
		Log.d("month", Integer.toString(m));
		database.open();
		long idNote = database.provideIdNoteForNewAction(y, m);
		Record newAction = fCallBack.onNewActionTaken(idNote);
		database.saveRecord(newAction);
		database.close();

		return idNote;
	}
	
	private void updateRelatedMonthSummary(long idNote) {
		database.open();
		Month month = database.getMonthSummary(idNote);
		month = fCallBack.onUpdateSummaryElement(month, ON_INCREASE);
		database.updateMonthSummary(month);
		database.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.date, menu);
		getMenuInflater().inflate(R.menu.discard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.date_picker) {
			DialogFragment newDate = new DatePickerFragment();
			newDate.show(getFragmentManager(), "dataPicker");
			return true;
		} else if (id == R.id.discard) {
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		date.set(year, monthOfYear, dayOfMonth);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		int index = getActionBar().getSelectedNavigationIndex();
		outState.putInt("selected_tab_index", index);
	}
}