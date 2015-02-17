package com.mybudget.main;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import com.mybudget.R;
import com.mybudget.actions.Actions;
import com.mybudget.basicStatus.BasicValues;
import com.mybudget.basicStatus.Summary;
import com.mybudget.database.BalanceSheetAdapter;
import com.mybudget.history.History;
import com.mybudget.history.RoundDecimal;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private static final int LATEST_ACTIONS = 10;
	private static final int EDIT_DETAILS_REQUEST = 0;
	private static final int ADD_NEW_ACTION = 1;
	ImageView iconDelete, iconNew;
	TextView total, savings, borrowed, lend, currency;

	private BalanceSheetAdapter database;
	Summary bSummary;
	Double[] bValues, cValues, mValues;

	ActionToDelete action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		database = new BalanceSheetAdapter(this);

		iconNew = (ImageView) findViewById(R.id.ic_action_new);
		iconNew.setOnClickListener(this);
		iconDelete = (ImageView) findViewById(R.id.ic_action_delete);
		iconDelete.setOnClickListener(this);

		total = (TextView) findViewById(R.id.main_total);
		savings = (TextView) findViewById(R.id.main_savings);
		borrowed = (TextView) findViewById(R.id.main_borrowed);
		lend = (TextView) findViewById(R.id.main_lend);
		currency = (TextView) findViewById(R.id.currencyTxt);

		getBasicSummaryFromDatabase();
	}

	@Override
	protected void onStart() {
		super.onStart();
		keepUpToDate();
	}

	private void keepUpToDate() {
		getCurrentSummaryFromDatabase();
		updateMainSummaryInfo();
		updateNotifications();
	}

	private void getBasicSummaryFromDatabase() {
		database.open();
		bSummary = database.getBasicSummary();
		database.close();
	}

	private void getCurrentSummaryFromDatabase() {
		database.open();
		cValues = database.sumMonths();
		database.close();
	}

	private void updateMainSummaryInfo() {
		bValues = bSummary.getBValues();
		mValues = sum(bValues, cValues);

		currency.setText(bSummary.getCurrency());
		total.setText(Double.toString(mValues[0]));
		savings.setText(Double.toString(mValues[1]));
		lend.setText(Double.toString(mValues[2]));
		borrowed.setText(Double.toString(mValues[3]));

		Log.d("updateMainSummaryInfo", "  main summary updated");
	}

	private Double[] sum(Double[] b, Double[] c) {
		Double[] m = new Double[4];
		for (int i = 0; i < b.length; i++) {
			m[i] = b[i] + c[i];
			m[i] = RoundDecimal.round(m[i], 2);
		}
		return m;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ic_action_new) {
			Intent intent = new Intent(this, Actions.class);
			startActivityForResult(intent, ADD_NEW_ACTION);
		} else if (v.getId() == R.id.ic_action_delete) {
			action.delete(database);
			v.setVisibility(View.INVISIBLE);
			Toast.makeText(this, "Deleted.", Toast.LENGTH_SHORT).show();

			keepUpToDate();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == EDIT_DETAILS_REQUEST) {
			if (resultCode == RESULT_OK) {
				bSummary = data.getParcelableExtra("basicSummary");
				database.open();
				database.updateBasicSummary(bSummary);
				database.close();
				Log.d("EDIT_DETAILS_REQUEST", "basic summary set");
			} else if (resultCode == RESULT_CANCELED) {
				Log.d("RESULT_CANCELED", "do nothing");
			}
		} else if (requestCode == ADD_NEW_ACTION) {
			if (resultCode == RESULT_OK) {
				getCurrentSummaryFromDatabase();
				Log.d("ADD_NEW_ACTION", "current summary set");

				updateNotifications();
			}
		}
	}

	private void updateNotifications() {
		database.open();
		Cursor cursor = database.getLatestActions(LATEST_ACTIONS);

		ListView latest_actions = (ListView) findViewById(R.id.latest_actions);

		if (cursor.getCount() == 0)
			Selection.changeVisibility(
					findViewById(R.id.main_no_entries_frame), latest_actions);
		else
			Selection.changeVisibility(latest_actions,
					findViewById(R.id.main_no_entries_frame));

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.notify_row, cursor, new String[] { "tableAction",
						"Title", "Name", "Note", "Amount", "Date" }, new int[] {
						R.id.icon, R.id.title, R.id.name, R.id.descript,
						R.id.amount, R.id.date }, 0);
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.icon) {

					int id = cursor.getColumnIndex("tableAction");
					String action = cursor.getString(id);

					ImageView icon = (ImageView) view;
					switch (action) {
					case "Incomes":
						icon.setImageDrawable(getResources().getDrawable(
								R.drawable.ic_dialog_income));
						break;
					case "Outcomes":
						icon.setImageDrawable(getResources().getDrawable(
								R.drawable.ic_dialog_outcome));
						break;
					case "Borrowers":
						icon.setImageDrawable(getResources().getDrawable(
								R.drawable.ic_dialog_borrowed));
						break;
					case "Lenders":
						icon.setImageDrawable(getResources().getDrawable(
								R.drawable.ic_dialog_lend));
						break;
					}
					return true;
				}
				return false;
			}
		});

		latest_actions.setAdapter(adapter);
		latest_actions.setOnItemClickListener(this);
		database.close();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor c = (Cursor) parent.getAdapter().getItem(position);
		action = new ActionToDelete(c.getInt(c.getColumnIndex("fIdNote")),
				c.getString(c.getColumnIndex("tableAction")), c.getDouble(c
						.getColumnIndex("Amount")));

		Selection s = new Selection(view.findViewById(R.id.on_delete_border),
				view.findViewById(R.id.on_not_deleted_border), iconDelete, null);
		s.markSelection();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		updateMainSummaryInfo();
	}

	@Override
	protected void onStop() {
		super.onStop();
		database.open();
		database.updateBasicSummary(bSummary);
		database.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				Intent intent = new Intent(this, BasicValues.class);
				intent.putExtra("basicSummary", bSummary);
				startActivityForResult(intent, EDIT_DETAILS_REQUEST);
				break;
			case R.id.action_history:
				if(isThereANote())
					startActivity(new Intent(this, History.class));
				else
					Toast.makeText(this, "No entries to show.", Toast.LENGTH_SHORT).show();
				break;
			case R.id.action_export:
				database.exportToDownloadFolder();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean isThereANote(){
		database.open();
		boolean any = database.countNotes() != 0;
		database.close();
		return any;
	}
}