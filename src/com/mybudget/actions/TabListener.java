package com.mybudget.actions;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class TabListener<T extends Fragment> implements ActionBar.TabListener {
	private Fragment fragment;
	private final Activity activity;
	private final String tag;
	private final Class<T> clz;

	public TabListener(Activity activity, String tag, Class<T> clz) {
		this.activity = activity;
		this.tag = tag;
		this.clz = clz;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (fragment == null) {
			fragment = Fragment.instantiate(activity, clz.getName());
			ft.replace(android.R.id.content, fragment, tag);
		} else if (fragment.isDetached()) {
			ft.attach(fragment);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (fragment != null) {
			ft.detach(fragment);
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
}