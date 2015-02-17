package com.mybudget.history;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mybudget.R;
import com.mybudget.database.BalanceSheetAdapter;
import com.mybudget.main.ActionToDelete;

public class Details extends Activity implements OnMenuItemVisibleListener {

	private long idMonthSelected;
	private BalanceSheetAdapter database;

	String net, in, out, savings, borrowed, lend, currency;
	MenuItem actionMenuItem;
	OnDeleteListener fCallback;
	int currentNavItem;
	ActionBar actionBar;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);

		database = new BalanceSheetAdapter(this);
		currentNavItem = 0 ;
	}

	@Override
	protected void onStart() {
		super.onStart();
		actionBar = getActionBar();
		SpinnerAdapter actionsAdapter = ArrayAdapter.createFromResource(this,
				R.array.actions, R.layout.actionbar_spinner);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setListNavigationCallbacks(actionsAdapter,
				new NavigationListener());

		actionBar.setSelectedNavigationItem(currentNavItem);
		idMonthSelected = getIntent().getExtras().getLong("idM");
		prepareOverview();
	}

	private void prepareOverview() {
		setValues();

		((TextView) findViewById(R.id.dNet)).setText(net);
		((TextView) findViewById(R.id.dIn)).setText(in);
		((TextView) findViewById(R.id.dOut)).setText(out);
		((TextView) findViewById(R.id.dSaved)).setText(savings);
		((TextView) findViewById(R.id.dBorrowed)).setText(borrowed);
		((TextView) findViewById(R.id.dLend)).setText(lend);
		((TextView) findViewById(R.id.dCurrencyTxt)).setText(currency);
	}

	private void setValues() {
		database.open();
		Cursor m = database.getMonth(idMonthSelected);

		if (isMonthAvailable(m)) {
			m.moveToFirst();
			in = Double.toString(RoundDecimal.round(m.getDouble(2), 2));
			out = Double.toString(RoundDecimal.round(m.getDouble(3), 2));
			net = Double.toString(RoundDecimal.round(m.getDouble(4), 2));
			savings = Double.toString(RoundDecimal.round(m.getDouble(5), 2));
			lend = Double.toString(RoundDecimal.round(m.getDouble(6), 2));
			borrowed = Double.toString(RoundDecimal.round(m.getDouble(7), 2));

			currency = database.getBasicSummary().getCurrency();
			database.close();
		} else
			finish();
	}

	private boolean isMonthAvailable(Cursor m) {
		return m.getCount() != 0;
	}

	private class NavigationListener implements OnNavigationListener {
		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			currentNavItem = itemPosition;
			
			String[] items = getResources().getStringArray(R.array.actions);
			Fragment fragment = new Fragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			switch (itemPosition) {
			case 0:
				fragment = new IncomesDetails();
				break;
			case 1:
				fragment = new OutcomesDetails();
				break;
			case 2:
				fragment = new BorrowersDetails();
				break;
			case 3:
				fragment = new LendersDetails();
				break;
			}
		
			fragment.setArguments(getIntent().getExtras());
			ft.replace(R.id.details_container, fragment, items[itemPosition]);
			if (fragment.isDetached())
				ft.attach(fragment);
			ft.commit();

			fCallback = (OnDeleteListener) fragment;
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.details, menu);
		actionMenuItem = menu.findItem(R.id.delete_b_or_l);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {

		case R.id.delete_b_or_l:
			ActionToDelete action = fCallback.onDeleteAction();
			action.delete(database);
			item.setVisible(false);
			onStart();
			Log.d("onOptions", Integer.toString(currentNavItem));
			Toast.makeText(this, "Deleted.", Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public MenuItem passMenuItem() {
		return actionMenuItem;
	}
}