package com.mybudget.history;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mybudget.R;
import com.mybudget.database.BalanceSheetAdapter;

public class History extends Activity implements OnItemClickListener,
		OnClickListener {

	ImageView previous, next;
	TextView year;
	private BalanceSheetAdapter database;
	String requestedYear;
	int index;
	ArrayList<String> years;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		database = new BalanceSheetAdapter(this);

		year = (TextView) findViewById(R.id.year);
		years = new ArrayList<String>();

		previous = (ImageView) findViewById(R.id.previous);
		next = (ImageView) findViewById(R.id.next);
		previous.setOnClickListener(this);
		next.setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		getAvailableYears();
		index = years.size() - 1;
		setRequestedYear(index);
	}



	private void getAvailableYears() {
		database.open();
		Cursor c = database.getYears();

		while (c.moveToNext())
			years.add(c.getString(0));

		database.close();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.previous:
			if (isYearAvailable(index - 1))
				setRequestedYear(--index);
			break;
		case R.id.next:
			if (isYearAvailable(index + 1))
				setRequestedYear(++index);
			break;
		}
	}

	private void setRequestedYear(int i) {
		requestedYear = years.get(i);
		year.setText(requestedYear);
		updateBalanceByYearList();
	}

	private boolean isYearAvailable(int i) {
		return i >= 0 && i < years.size();
	}

	private void updateBalanceByYearList() {
		database.open();
		Cursor cursor = database.getMonthlyBalanceByYear(requestedYear);

		ListView history = (ListView) findViewById(R.id.monthy_list);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.history_list_item, cursor, new String[] { "Name",
						"Income", "Outcome", "Net" }, new int[] { R.id.month,
						R.id.total_in, R.id.total_out, R.id.total_net }, 0);

		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				TextView month = (TextView) view.findViewById(R.id.month);
				TextView net = (TextView) view.findViewById(R.id.total_net);
				if (view.getId() == R.id.month) {
					int numeric = Integer.parseInt(cursor.getString(cursor
							.getColumnIndex("Name")));
					String monthName = getMonthName(numeric);
					month.setText(monthName);

					return true;
				} else if (view.getId() == R.id.total_net) {
					double total_net = Double.parseDouble(cursor.getString(cursor
							.getColumnIndex("Net")));

					
					net.setText(Double.toString(RoundDecimal.round(total_net, 2)));

					return true;
				}
				return false;
			}
		});

		history.setAdapter(adapter);
		history.setOnItemClickListener(this);
		database.close();
	}

	private String getMonthName(int numeric) {
		DateFormatSymbols dfs = DateFormatSymbols.getInstance(Locale.US);
		String[] months = dfs.getMonths();
		return months[numeric - 1];
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor c = (Cursor) parent.getAdapter().getItem(position);
		long idM = c.getLong(c.getColumnIndex("_id"));

		Intent intent = new Intent(this, Details.class);
		intent.putExtra("idM", idM);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.summary_by_year, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_summary_by_year:
				startActivity(new Intent(this, SummaryByYear.class));
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}