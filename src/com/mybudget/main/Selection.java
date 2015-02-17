package com.mybudget.main;

import android.view.MenuItem;
import android.view.View;

public class Selection {

	View onDelete, onNotDelete, actionIcon;
	MenuItem item;

	public Selection(View onDelete, View onNotDelete, View icon, MenuItem item) {
		this.onDelete = onDelete;
		this.onNotDelete = onNotDelete;
		actionIcon = icon;
		this.item = item;
	}

	public void markSelection() {
		if (onDelete.getVisibility() == View.VISIBLE)
			setUnselected();
		else
			setSelected();
	}

	private void setSelected() {
		if (actionIcon != null && actionIcon.getVisibility() == View.INVISIBLE) {
			actionIcon.setVisibility(View.VISIBLE);
			changeVisibility(onDelete, onNotDelete);
		}
		else if(item != null && !item.isVisible()){
			item.setVisible(true);
			changeVisibility(onDelete, onNotDelete);
		}
	}

	private void setUnselected() {
		if (actionIcon != null)
			actionIcon.setVisibility(View.INVISIBLE);
		else
			item.setVisible(false);
		changeVisibility(onNotDelete, onDelete);
	}

	public static void changeVisibility(View visible, View gone) {
		gone.setVisibility(View.GONE);
		visible.setVisibility(View.VISIBLE);
	}
}