package com.mybudget.basicStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalDigitsFilter implements InputFilter {
	Pattern mPattern;

	public DecimalDigitsFilter(int digitsAfterZero) {
		mPattern = Pattern.compile("[0-9]*((\\.[0-9]{0," + (digitsAfterZero)
				+ "})?)||(\\.)?");
	}

	public DecimalDigitsFilter(double c) {
		mPattern = Pattern.compile("[0-9]{0,2}|100|([0-9]{0,2}\\.[0-9]{0,1})");
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		String result = dest.subSequence(0, dstart).toString()
				+ source.subSequence(start, end).toString()
				+ dest.subSequence(dend, dest.length()).toString();

		Matcher matcher = mPattern.matcher(result);
		if (!matcher.matches())
			return "";
		return null;
	}
}