package com.mybudget.history;

import com.mybudget.R;
import com.mybudget.database.BalanceSheetAdapter;
import com.mybudget.main.ActionToDelete;
import com.mybudget.main.Selection;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;

public class OutcomesDetails extends Fragment implements OnDeleteListener,
		OnItemClickListener {

	private ExpandableListAdapter adapter;
	private ExpandableListView outcomesList;
	private BalanceSheetAdapter database;
	private long idMonthSelected;
	
	private ActionToDelete action;
	private OnMenuItemVisibleListener iVisible;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.details_outcomes, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		database = new BalanceSheetAdapter(getActivity());

		idMonthSelected = getArguments().getLong("idM");
		
		outcomesList = (ExpandableListView) getView().findViewById(
				R.id.outcomes_list);
		adapter = prepareAdapter();

		outcomesList.setAdapter(adapter);
		outcomesList.setOnItemClickListener(this);
	}

	private ExpandableListAdapter prepareAdapter() {
		database.open();
		Cursor categories = database.getCategoriesUsedIn(idMonthSelected);
		return new ExpandableListAdapter(idMonthSelected, getActivity(),
				categories, R.layout.category_headline, new String[] { "Name",
						"SUM(Amount)" }, new int[] { R.id.category,
						R.id.sumAmount }, R.layout.details_list_item,
				new String[] { "Date", "Title", "Amount" }, new int[] {
						R.id.oDate, R.id.oTitle, R.id.oAmount });
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor c = (Cursor) parent.getAdapter().getItem(position);
		
		action = new ActionToDelete(c.getInt(c.getColumnIndex("fIdNote")),
				"Outcomes", c.getDouble(c.getColumnIndex("Amount")));
		MenuItem ic_delete = iVisible.passMenuItem();

		Selection s = new Selection(view.findViewById(R.id.item_on_delete),
				view.findViewById(R.id.item_on_not_delete), null, ic_delete);
		s.markSelection();

	}

	@Override
	public ActionToDelete onDeleteAction() {
		return action;
	}
}