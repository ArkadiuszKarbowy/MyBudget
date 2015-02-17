package com.mybudget.history;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.mybudget.R;
import com.mybudget.database.BalanceSheetAdapter;

public class ExpandableListAdapter extends SimpleCursorTreeAdapter {

	Context context;
	String whichTable;
	long idMonthSelected;
	private BalanceSheetAdapter database;

	public ExpandableListAdapter(long idMonthSelected, Context context,
			Cursor groupCursor, int groupLayout, String[] groupFrom,
			int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupCursor, groupLayout, groupFrom, groupTo,
				childLayout, childFrom, childTo);

		this.context = context;
		this.idMonthSelected = idMonthSelected;

		database = new BalanceSheetAdapter(context);
	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		String categoryName = groupCursor.getString(groupCursor
				.getColumnIndex("Name"));
		database.open();
		Cursor outcomes = database.getOutcomesOfCategory(categoryName,
				idMonthSelected);

		// // database.close();
		return outcomes;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View rowView = super.getChildView(groupPosition, childPosition,
				isLastChild, convertView, parent);

		TextView date = (TextView) rowView.findViewById(R.id.oDate);
		Cursor outcomes = getChild(groupPosition, childPosition);

		String fullDate = outcomes.getString(outcomes.getColumnIndex("Date"));

		String day = fullDate.substring(0, fullDate.indexOf(" "));
		if (day.length() == 1)
			day = "0" + day;
		date.setText(day);

		return rowView;
	}
}