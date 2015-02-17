package com.mybudget.basicStatus;

import com.mybudget.database.Record;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

public class Summary implements Parcelable, Record {
	private static String TABLE_NAME = "Summary";

	private String currency;
	private double bTotal;
	private double bAccount, bCash, bWallet;
	private double bSavings, bInterest;
	private double bLend, bBorrowed;

	private long bId;

	public Summary(long id) {
		bId = id;
		currency = "PLN";
		bTotal = bAccount = bCash = bWallet = bSavings = bInterest = bLend = bBorrowed = 0;
	}

	public Summary() {
		this(-1);
	}

	public Summary(Parcel data) {
		setId(data.readLong());
		setCurrency(data.readString());
		setTotal(data.readDouble());
		setAccount(data.readDouble());
		setCash(data.readDouble());
		setWallet(data.readDouble());
		setSavings(data.readDouble());
		setInterest(data.readDouble());
		setLend(data.readDouble());
		setBorrowed(data.readDouble());
	}

	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeString(getCurrency());
		dest.writeDouble(getTotal());
		dest.writeDouble(getAccount());
		dest.writeDouble(getCash());
		dest.writeDouble(getWallet());
		dest.writeDouble(getSavings());
		dest.writeDouble(getInterest());
		dest.writeDouble(getLend());
		dest.writeDouble(getBorrowed());

	}

	public static final Creator<Summary> CREATOR = new Creator<Summary>() {
		@Override
		public Summary createFromParcel(Parcel parcel) {
			return new Summary(parcel);
		}

		@Override
		public Summary[] newArray(int size) {
			return new Summary[size];
		}

	};
	
	public Double[] getBValues(){
		return new Double[]{bTotal, bSavings, bLend, bBorrowed};
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
		cv.put("currency", currency);
		cv.put("total", this.getTotal());
		cv.put("account", this.getAccount());
		cv.put("cash", this.getCash());
		cv.put("wallet", this.getWallet());
		cv.put("savings", this.getSavings());
		cv.put("interest", this.getInterest());
		cv.put("lend", this.getLend());
		cv.put("borrowed", this.getBorrowed());

		return cv;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public long getId() {
		return bId;
	}

	public void setId(long bId) {
		this.bId = bId;
	}

	public double getTotal() {
		return bTotal;
	}

	public void setTotal(double bTotal) {
		this.bTotal = bTotal;
	}

	public double getAccount() {
		return bAccount;
	}

	public void setAccount(double bAccount) {
		this.bAccount = bAccount;
	}

	public double getCash() {
		return bCash;
	}

	public void setCash(double bCash) {
		this.bCash = bCash;
	}

	public double getWallet() {
		return bWallet;
	}

	public void setWallet(double bWallet) {
		this.bWallet = bWallet;
	}

	public double getSavings() {
		return bSavings;
	}

	public void setSavings(double bSavings) {
		this.bSavings = bSavings;
	}

	public double getInterest() {
		return bInterest;
	}

	public void setInterest(double bInterest) {
		this.bInterest = bInterest;
	}

	public double getLend() {
		return bLend;
	}

	public void setLend(double bLend) {
		this.bLend = bLend;
	}

	public double getBorrowed() {
		return bBorrowed;
	}

	public void setBorrowed(double bBorrowed) {
		this.bBorrowed = bBorrowed;
	}
}