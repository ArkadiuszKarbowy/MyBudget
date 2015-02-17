package com.mybudget.basicStatus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mybudget.R;

public class BasicValues extends Activity implements OnClickListener {

	Summary bSummary;
	EditText bAccount, bCashTotal, bWallet, bCashSavings, bInterest, bLend,
			bBorrowed;
	Spinner bCurrency;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basic);
		setActionBar();
		initAndSetBasicValues();
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
		actionBar.setCustomView(custom);
	}

	private void initAndSetBasicValues() {
		bCurrency = (Spinner) findViewById(R.id.currency);
		bAccount = (EditText) findViewById(R.id.account);
		bCashTotal = (EditText) findViewById(R.id.cash_total);
		bWallet = (EditText) findViewById(R.id.wallet);
		bCashSavings = (EditText) findViewById(R.id.cash_savings);
		bInterest = (EditText) findViewById(R.id.interest);
		bLend = (EditText) findViewById(R.id.cash_lend);
		bBorrowed = (EditText) findViewById(R.id.cash_borrowed);
		
		setDecimalDigitsFilter();
		
		Bundle extras = getIntent().getExtras();
		bSummary = extras.getParcelable("basicSummary");

		ArrayAdapter<String> adapter = (ArrayAdapter<String>) bCurrency
				.getAdapter();
		String currency = bSummary.getCurrency();
		bCurrency.setSelection(adapter.getPosition(currency));
		
		bAccount.setText(String.valueOf(bSummary.getAccount()));
		bCashTotal.setText(String.valueOf(bSummary.getCash()));
		bWallet.setText(String.valueOf(bSummary.getWallet()));
		bCashSavings.setText(String.valueOf(bSummary.getSavings()));
		bInterest.setText(String.valueOf(bSummary.getInterest()));
		bLend.setText(String.valueOf(bSummary.getLend()));
		bBorrowed.setText(String.valueOf(bSummary.getBorrowed()));

	}
	
	private void setDecimalDigitsFilter(){
		bAccount.setFilters(new InputFilter[] {new DecimalDigitsFilter(2)});
		bCashTotal.setFilters(new InputFilter[] {new DecimalDigitsFilter(2)});
		bWallet.setFilters(new InputFilter[] {new DecimalDigitsFilter(2)});
		bCashSavings.setFilters(new InputFilter[] {new DecimalDigitsFilter(2)});
		bInterest.setFilters(new InputFilter[] {new DecimalDigitsFilter(2.6)});
		bLend.setFilters(new InputFilter[] {new DecimalDigitsFilter(2)});
		bBorrowed.setFilters(new InputFilter[] {new DecimalDigitsFilter(2)});
	}

	@Override
	public void onClick(View view) {
		Intent data = new Intent();
		bSummary.setCurrency(bCurrency.getSelectedItem().toString());
		Double account = myParseDouble(bAccount);
		Double cashTotal =myParseDouble(bCashTotal);
		Double wallet = myParseDouble(bWallet);

		Double total = account + cashTotal + wallet;
		
		bSummary.setTotal(total);
		bSummary.setAccount(account);
		bSummary.setCash(cashTotal);
		bSummary.setWallet(wallet);
		bSummary.setSavings(myParseDouble(bCashSavings));
		bSummary.setInterest(myParseDouble(bInterest));
		bSummary.setLend(myParseDouble(bLend));
		bSummary.setBorrowed(myParseDouble(bBorrowed));

		data.putExtra("basicSummary", bSummary);
		setResult(RESULT_OK, data);
		finish();
	}

	private double myParseDouble(EditText input) {
		return input.length() != 0 ? Double.parseDouble(input.getText().toString()) : 0d;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.discard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.discard) {
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}