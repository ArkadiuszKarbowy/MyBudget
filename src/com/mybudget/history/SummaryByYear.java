package com.mybudget.history;

import com.mybudget.R;
import com.mybudget.database.BalanceSheetAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SummaryByYear extends Activity implements OnItemClickListener {

	private BalanceSheetAdapter database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary_by_year);

		database = new BalanceSheetAdapter(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		updateSummaryByYearList();
	}

	private void updateSummaryByYearList() {
		database.open();
		Cursor cursor = database.getAnnualBalance();

		ListView history = (ListView) findViewById(R.id.sby_list);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.sby_list_item, cursor, new String[] { "Name",
						"SUM(Income)", "SUM(Outcome)", "SUM(Savings)", "AvgIn",
						"AvgOut", "AvgSaved" }, new int[] { R.id.sby_year,
						R.id.sby_in, R.id.sby_out, R.id.sby_saved,
						R.id.sby_avgIn, R.id.sby_avgOut, R.id.sby_avgSaved }, 0);

		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				TextView avgin = (TextView) view.findViewById(R.id.sby_avgIn);
				TextView avgout = (TextView) view.findViewById(R.id.sby_avgOut);
				TextView avgsaved = (TextView) view
						.findViewById(R.id.sby_avgSaved);
				if (view.getId() == R.id.sby_avgIn) {
					double in = cursor.getDouble(cursor.getColumnIndex("AvgIn"));
					avgin.setText(Double.toString(RoundDecimal.round(in, 2)));
					return true;
				} else if (view.getId() == R.id.sby_avgOut) {
					double out = cursor.getDouble(cursor
							.getColumnIndex("AvgOut"));
					avgout.setText(Double.toString(RoundDecimal.round(out, 2)));
					return true;
				} else if (view.getId() == R.id.sby_avgSaved) {
					double saved = cursor.getDouble(cursor
							.getColumnIndex("AvgSaved"));
					avgsaved.setText(Double.toString(RoundDecimal.round(saved,
							2)));
					return true;
				}
				return false;
			}
		});

		history.setAdapter(adapter);
		history.setOnItemClickListener(this);
		database.close();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor c = (Cursor) parent.getAdapter().getItem(position);
		String year = c.getString(c.getColumnIndex("Name"));

		database.open();
		if(database.isThereAnOutcome(year))
		{
			database.close();
			Intent intent = new Intent(this, YearStats.class);
			intent.putExtra("year", year);
			startActivity(intent);
		}
		else
			Toast.makeText(this, "No outcomes to summary.", Toast.LENGTH_SHORT).show();
	}
}
