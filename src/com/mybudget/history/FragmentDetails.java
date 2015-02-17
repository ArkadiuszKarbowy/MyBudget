package com.mybudget.history;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.mybudget.R;
import com.mybudget.database.BalanceSheetAdapter;
import com.mybudget.main.ActionToDelete;
import com.mybudget.main.Selection;

public abstract class FragmentDetails extends Fragment implements
		OnItemClickListener, OnDeleteListener {

	protected String table;
	protected int viewLayout, listView;
	protected String[] from;
	protected int[] to;

	private ActionToDelete action;
	private OnMenuItemVisibleListener iVisible;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			iVisible = (OnMenuItemVisibleListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnMenuItemVisibleListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(viewLayout, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		BalanceSheetAdapter database = new BalanceSheetAdapter(getActivity());

		long idMonthSelected = getArguments().getLong("idM");

		database.open();
		String columns = toString(from);
		Cursor actions = database.getActions(table, columns, idMonthSelected);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.details_list_item, actions, from, to, 0);

		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				TextView date = (TextView) view.findViewById(R.id.oDate);
				if (view.getId() == R.id.oDate) {

					String fullDate = cursor.getString(cursor
							.getColumnIndex("Date"));
					String day = fullDate.substring(0, fullDate.indexOf(" "));
					if (day.length() == 1)
						day = "0" + day;
					date.setText(day);

					return true;
				}
				return false;
			}
		});

		ListView actionsList = (ListView) getView().findViewById(listView);
		actionsList.setAdapter(adapter);
		actionsList.setOnItemClickListener(this);
		database.close();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor c = (Cursor) parent.getAdapter().getItem(position);
	
		action = new ActionToDelete(c.getInt(c.getColumnIndex("fIdNote")),
				table, c.getDouble(c.getColumnIndex("Amount")));
		MenuItem ic_delete = iVisible.passMenuItem();

		Selection s = new Selection(view.findViewById(R.id.item_on_delete),
				view.findViewById(R.id.item_on_not_delete), null, ic_delete);
		s.markSelection();
	}

	@Override
	public ActionToDelete onDeleteAction() {
		return action;
	}

	private String toString(String[] f) {
		String s = "";
		for (int i = 0; i < f.length; i++) {
			s += f[i] + ",";
		}
		return s.substring(0, s.lastIndexOf(","));
	}
}