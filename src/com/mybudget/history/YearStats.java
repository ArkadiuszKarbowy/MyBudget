package com.mybudget.history;

import com.mybudget.R;
import com.mybudget.R.id;
import com.mybudget.R.layout;
import com.mybudget.database.BalanceSheetAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.os.Build;

public class YearStats extends Activity {

	private BalanceSheetAdapter database;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.year_stats);

		database = new BalanceSheetAdapter(this);
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		updateOutcomesSummary(getIntent().getExtras().getString("year"));
	}

	private void updateOutcomesSummary(String year) {
		database.open();
		Cursor cursor = database.getAnnualOutcomesSummary(year);

		ListView history = (ListView) findViewById(R.id.sby_outcomes_details);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.year_stats_list_item, cursor, new String[] { "CategoryName", "Total" }, 
				new int[] { R.id.sby_out_cat, R.id.sby_out_total, }, 0);
		
		history.setAdapter(adapter);
		database.close();
	}

}
