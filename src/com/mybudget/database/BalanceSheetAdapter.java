package com.mybudget.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.mybudget.basicStatus.Summary;

public class BalanceSheetAdapter {

	private static final int VERSION = 2;
	private static final String DB_NAME = "sheet.db";
	private Context context;
	private BalanceSheetHelper dbHelper;
	private SQLiteDatabase db;
	
	public BalanceSheetAdapter(Context context) {
		this.context = context;
	}

	private static class BalanceSheetHelper extends SQLiteOpenHelper {

		public BalanceSheetHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onConfigure(SQLiteDatabase db) {
			super.onConfigure(db);
			db.setForeignKeyConstraintsEnabled(false); // min API 16
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			Log.d("BalanceSheetHelper", "Database creating...");

			createTableSummary(db);

			createTableCategories(db);
			createTableMonths(db);
			createTableYears(db);
			createTableBalanceByYear(db);

			createTableNotes(db);
			createTableIncomes(db);
			createTableOutcomes(db);
			createTableLenders(db);
			createTableBorrowers(db);

			preMade(db);

			Log.d("BalanceSheetHelper", "Database created.");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			db.execSQL("DROP TABLE IF EXISTS " + "Summary");
//			db.execSQL("DROP TABLE IF EXISTS " + "Outcomes");
//			db.execSQL("DROP TABLE IF EXISTS " + "Categories");
//			db.execSQL("DROP TABLE IF EXISTS " + "Incomes");
//			db.execSQL("DROP TABLE IF EXISTS " + "Lenders");
//			db.execSQL("DROP TABLE IF EXISTS " + "Borrowers");
//			db.execSQL("DROP TABLE IF EXISTS " + "Notes");
//			db.execSQL("DROP TABLE IF EXISTS " + "BalanceByYear");
//			db.execSQL("DROP TABLE IF EXISTS " + "Months");
//			db.execSQL("DROP TABLE IF EXISTS " + "Years");

//			Log.d("onUpgrade", "database updating, data lost");
//			onCreate(db);
			Log.d("onUpgrade()", "Reacreating table Months");

			db.execSQL("CREATE TABLE " + "TempMonths" + "(" + "_id"
					+ " integer primary key autoincrement," + "Name" + " integer,"
					+ "Income" + " real," + "Outcome" + " real," + "Net"
					+ " real," + "Savings" + " real," + "Lend" + " real,"
					+ "Borrowed" + " real" + ");");

			Log.d("createTableMonths()", "TEMP Table MONTHS created.");
			
			db.execSQL("INSERT INTO " +"TempMonths " + "SELECT _id, CAST(Name AS integer), "
					+ "Income, Outcome, Net, Savings, Lend, Borrowed " + "FROM Months");
			
			db.execSQL("DROP TABLE IF EXISTS " + "Months");
			
			db.execSQL("ALTER TABLE TempMonths RENAME TO Months");
			Log.d("onUpgrade()", "Reacreated table Months");
		}

		private void preMade(SQLiteDatabase db) {
			Log.d("BalanceSheetHelper", "Basic creating...");

			ArrayList<Category> premadeCategories = new ArrayList<Category>();
			String[] categories = { "Food", "Bills", "Travels", "Clothes",
					"Entertainment", "Education", "Charity", "Add new..." };
			for (int i = 0; i < categories.length; i++) {
				String name = categories[i];
				premadeCategories.add(new Category(name));
			}

			for (Category c : premadeCategories)
				db.insert(c.getTableName(), null, c.getContentValues());

			Summary s = new Summary(1);
			db.insert("Summary", null, s.getContentValues());
			
			Log.d("BalanceSheetHelper", "Basic created.");
		}

		private void createTableSummary(SQLiteDatabase db) {
			Log.d("createTableSummary()", "Table SUMMARY creating...");

			db.execSQL("CREATE TABLE " + "Summary" + "(" + "_id"
					+ " integer primary key autoincrement," + "currency"
					+ " text, " + "total" + " real," + "account" + " real,"
					+ "cash" + " real," + "wallet" + " real," + "savings"
					+ " real," + "interest" + " real," + "lend" + " real,"
					+ "borrowed" + " real" + ");");

			Log.d("createTableSummary()", "Table SUMMARY created.");
		}

		private void createTableCategories(SQLiteDatabase db) {
			Log.d("createTableCategory()", "Table CATEGORIES creating...");

			db.execSQL("CREATE TABLE " + "Categories" + "(" + "_id"
					+ " integer primary key autoincrement," + "Name" + " text"
					+ ");");

			Log.d("createTableCategory()", "Table CATEGORIES created.");
		}

		private void createTableMonths(SQLiteDatabase db) {
			Log.d("createTableMonths()", "Table MONTHS creating...");

			db.execSQL("CREATE TABLE " + "Months" + "(" + "_id"
					+ " integer primary key autoincrement," + "Name" + " integer,"
					+ "Income" + " real," + "Outcome" + " real," + "Net"
					+ " real," + "Savings" + " real," + "Lend" + " real,"
					+ "Borrowed" + " real" + ");");

			Log.d("createTableMonths()", "Table MONTHS created.");
		}

		private void createTableYears(SQLiteDatabase db) {
			Log.d("createTableYears()", "Table YEARS creating...");

			db.execSQL("CREATE TABLE " + "Years" + "(" + "_id"
					+ " integer primary key autoincrement," + "Name" + " text"
					+ ");");

			Log.d("createTableYears()", "Table YEARS created.");
		}

		private void createTableBalanceByYear(SQLiteDatabase db) {
			Log.d("createTableBalanceByYear()",
					"Table BALANCEBYYEAR creating...");

			db.execSQL("CREATE TABLE " + "BalanceByYear" + "(" + "_id"
					+ " integer primary key autoincrement," + "fIdYear"
					+ " integer not null, " + "fIdMonth" + " integer not null,"
					+ "FOREIGN KEY" + "(fIdYear)" + " REFERENCES "
					+ "Years(_id)," + "FOREIGN KEY" + "(fIdMonth)"
					+ " REFERENCES " + "Months(_id)" + ");");

			Log.d("createTableBalanceByYear()", "Table BALANCEBYYEAR created.");
		}

		private void createTableIncomes(SQLiteDatabase db) {
			Log.d("createTableIncomes()", "Table INCOMES creating...");

			db.execSQL("CREATE TABLE " + "Incomes" + "(" + "_id"
					+ " integer primary key autoincrement," + "tableAction"
					+ " text," + "Title" + " text," + "Date" + " text,"
					+ "Interest" + " real," + "Amount" + " real," + "fIdNote"
					+ " integer not null," + "FOREIGN KEY" + "(fIdNote)"
					+ " REFERENCES " + "Notes(_id)" + ");");

			Log.d("createTableIncomes()", "Table INCOMES created.");
		}

		private void createTableOutcomes(SQLiteDatabase db) {
			Log.d("createTableOutcomes()", "Table OUTCOMES creating...");

			db.execSQL("CREATE TABLE " + "Outcomes" + "(" + "_id"
					+ " integer primary key autoincrement," + "tableAction"
					+ " text," + "Title" + " text," + "Date" + " text,"
					+ "Amount" + " real," + "fIdCategory"
					+ " integer not null, " + "fIdNote" + " integer not null,"
					+ "FOREIGN KEY" + "(fIdCategory)" + " REFERENCES "
					+ "Categories(_id)," + "FOREIGN KEY" + "(fIdNote)"
					+ " REFERENCES " + "Notes(_id)" + ");");

			Log.d("createTableOutcomes()", "Table OUTCOMES created.");
		}

		private void createTableLenders(SQLiteDatabase db) {
			Log.d("createTableLenders()", "Table LENDERS creating...");

			db.execSQL("CREATE TABLE " + "Lenders" + "(" + "_id"
					+ " integer primary key autoincrement," + "tableAction"
					+ " text," + "Name" + " text," + "Date" + " text," + "Note"
					+ " text," + "Amount" + " real," + "fIdNote"
					+ " integer not null," + "FOREIGN KEY" + "(fIdNote)"
					+ " REFERENCES " + "Notes(_id)" + ");");

			Log.d("createTableLenders()", "Table LENDERS created.");
		}

		private void createTableBorrowers(SQLiteDatabase db) {
			Log.d("createTableBorrowers()", "Table BORROWERS creating...");

			db.execSQL("CREATE TABLE " + "Borrowers" + "(" + "_id"
					+ " integer primary key autoincrement," + "tableAction"
					+ " text," + "Name" + " text," + "Date" + " text," + "Note"
					+ " text," + "Amount" + " real," + "fIdNote"
					+ " integer not null," + "FOREIGN KEY" + "(fIdNote)"
					+ " REFERENCES " + "Notes(_id)" + ");");

			Log.d("createTableBorrowers()", "Table BORROWERS created.");
		}

		private void createTableNotes(SQLiteDatabase db) {
			Log.d("createTableNotes()", "Table NOTES creating...");

			db.execSQL("CREATE TABLE " + "Notes" + "(" + "_id"
					+ " integer primary key autoincrement," + "fIdBalance"
					+ " integer," + "FOREIGN KEY" + "(fIdBalance)"
					+ " REFERENCES " + "BalanceByYear(_id)" + ");");

			Log.d("createTableNotes()", "Table NOTES created.");
		}
	}

	public BalanceSheetAdapter open() {
		dbHelper = new BalanceSheetHelper(context, DB_NAME, null, VERSION);

		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLException e) {
			db = dbHelper.getReadableDatabase();
		}
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public Cursor getMonth(long idM) {
		return db.rawQuery("Select * from Months where _id=" + idM, null);
	}

	public long provideIdNoteForNewAction(String y, int m) {

		long idBby = provideIdBbYForNewNote(y, m);
		Note note = new Note(idBby);
		long idNote = saveRecord(note);
		return idNote;
	}

	private long provideIdBbYForNewNote(String y, int m) {
		long idBbY = -1, idY = -1, idM = -1; // not present in datebase

		if (IsYearPresent(y)) {
			idY = getYearId(y);
			idM = getMonthIdIfPresent(m, idY);
			if (idM != -1) {
				idBbY = getBbYId(idY, idM);
			} else {
				idM = saveRecord(new Month(m, 0, 0, 0, 0, 0, 0));
				idBbY = saveRecord(new BalanceByYear(idY, idM));
			}
		} else {
			idY = saveRecord(new Year(y));
			idM = saveRecord(new Month(m, 0, 0, 0, 0, 0, 0));
			idBbY = saveRecord(new BalanceByYear(idY, idM));
		}
		Log.d("New BbY inserted into row: ", Long.toString(idBbY));
		return idBbY;
	}

	private boolean IsYearPresent(String y) {
		Cursor c = db.rawQuery("select _id from Years where Name='" + y + "'",
				null);
		Log.d("isYearPresent", Integer.toString(c.getCount()));
		return c.getCount() == 1;
	}

	private long getYearId(String y) {
		Cursor c = db.rawQuery("select _id from Years where Name='" + y + "'",
				null);
		c.moveToFirst();
		Log.d("getYearId", Integer.toString(c.getInt(0)));
		return c.getInt(0);
	}

	private long getMonthIdIfPresent(int m, long idY) {
		Cursor c = db.rawQuery("select _id from Months where Name=" + m + "",
				null);
		long idM = -1;
		if (c.getCount() > 0) {
			while (c.moveToNext() && idM == -1) {
				if(isMonthRelatedToTheYear(idY, c.getInt(0)))
					idM = c.getInt(0);
			}
		}

		Log.d("getMonthIdIfPresent", Long.toString(idM));
		return idM;
	}

	private boolean isMonthRelatedToTheYear(long idY, long idM) {
		Cursor c = db.rawQuery("select * from BalanceByYear where fIdYear="
				+ idY + " and fIdMonth=" + idM, null);
		Log.d("isMonthRelatedToTheYear", Integer.toString(c.getCount()));
		return c.getCount() == 1;
	}

	private long getBbYId(long idY, long idM) {
		Cursor c = db.rawQuery("select * from BalanceByYear where fIdYear="
				+ idY + " and fIdMonth=" + idM, null);
		c.moveToFirst();
		Log.d("getBbYId", Integer.toString(c.getInt(0)));
		return c.getInt(0);
	}

	public long saveRecord(Record r) {
		long i = db.insert(r.getTableName(), null, r.getContentValues());
		Log.d("saveRecord()", "New record insterted into row: " + i);
		return i;
	}

	public void updateBasicSummary(Summary s) {
		long i = db.update(s.getTableName(), s.getContentValues(), "_id=1",
				null);
		Log.d("updateBasicSummary()", "Updated rows: " + i);
	}

	public Summary getBasicSummary() {
		Summary s = new Summary(1);

		Cursor c = db.rawQuery("select * from Summary", null);
		c.moveToFirst();
		s.setCurrency(c.getString(1));
		s.setTotal(c.getDouble(2));
		s.setAccount(c.getDouble(3));
		s.setCash(c.getDouble(4));
		s.setWallet(c.getDouble(5));
		s.setSavings(c.getDouble(6));
		s.setInterest(c.getDouble(7));
		s.setLend(c.getDouble(8));
		s.setBorrowed(c.getDouble(9));
		Log.d("getBasicSummary()",
				"Got basic summary from database, row: " + c.getPosition());
		return s;
	}

	public Double[] sumMonths() {
		Cursor c = db.rawQuery("select sum(Net), sum(Savings), sum(Lend), sum(Borrowed) from Months", null);
		c.moveToFirst();
		return new Double []{ c.getDouble(0), c.getDouble(1), c.getDouble(2), c.getDouble(3) };
	}

	public Cursor getLatestActions(int n) {
		String[] subQueries;
		int notes = countNotes();
		if (countNotes() > n) {
			String limit = Integer.toString(n);
			String in = " where fIdNote in (select _id from Notes order by _id desc limit "
					+ limit + ")";

			subQueries = makeSelect(in);
		} else {
			String where = "";
			subQueries = makeSelect(where);
		}

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String sql = qb.buildUnionQuery(subQueries, "fIdNote desc", null);
		Cursor actions = db.rawQuery(sql, null);

		Log.d("getLatestActions", "Got: " + actions.getCount() + " of: "
				+ Integer.toString(notes));
		return actions;
	}

	public int countNotes() {
		Cursor notes = db.rawQuery("SELECT * FROM Notes", null);
		return notes.getCount();
	}

	private String[] makeSelect(String statment) {
		String selectBorrowers = "select _id, tableAction, null as Title, Name, Date, Note, Amount, null as fIdCategory, fIdNote from Borrowers";
		String selectLenders = "select _id, tableAction, null, Name, Date, Note, Amount, null, fIdNote from Lenders";
		String selectOutcomes = "select _id, tableAction, Title, null, Date, null, Amount, fIdCategory, fIdNote from Outcomes";
		String selectIncomes = "select _id, tableAction, Title, null, Date, null,  Amount, null, fIdNote from Incomes";

		String borrowers, lenders, outcomes, incomes;

		borrowers = selectBorrowers + statment;
		lenders = selectLenders + statment;
		outcomes = selectOutcomes + statment;
		incomes = selectIncomes + statment;

		return new String[] { borrowers, lenders, outcomes, incomes };
	}

	public Cursor getMonthlyBalanceByYear(String year) {
	 return db.rawQuery("select * from months where _id in "
				+ "(select fIdMonth from BalanceByYear where fIdYear in "
				+ "(select _id from Years where Name='" + year
				+ "')) order by Name desc", null);
	}
	
	public Cursor getAnnualBalance() {
			return db.rawQuery("select Y._id, Y.Name, SUM(Income), SUM(Outcome), SUM(Savings), "
					+ "SUM(Income)/12 AS AvgIn, SUM(Outcome)/12 AS AvgOut, SUM(Savings)/12 As AvgSaved "
					+ "FROM Months AS M "
					+ "JOIN BalanceByYear AS Bby ON M._id=Bby._id "
					+ "JOIN Years AS Y ON Bby.fIdYear=Y._id "
					+ "GROUP BY Y.Name ORDER BY Y.Name desc "
					, null);
	}
	
	public Cursor getAnnualOutcomesSummary(String year) {
		String outcomes = "(select * from Outcomes AS OUT "
				+ "JOIN Notes AS N ON OUT.fIdNote=N._id "
				+ "JOIN BalanceByYear AS Bby ON N.fIdBalance=Bby._id "
				+ "JOIN Years AS Y ON Bby.fIdYear=Y._id WHERE Y.Name =" + year + ") ";
		String sql = "select C._id as _id, C.Name as CategoryName, SUM(Amount) as Total FROM " + outcomes + "AS O "
				+ "JOIN Categories AS C ON O.fIdCategory=C._id "
				+ "GROUP BY C._id ORDER BY Total desc";
		return db.rawQuery(sql, null);
	}

	public Cursor getAllCategories() {
		return db.rawQuery("select _id, Name from Categories", null);
	}

	public Cursor getCategoriesUsedIn(long idM) {
		String outcomes = "(select * from Outcomes where fIdNote in "
				+ "(select _id from Notes where fIdBalance in "
				+ "(select _id from BalanceByYear where fIdMonth=" + idM
				+ ")))";
		String sql = "select *, SUM(Amount) from " + outcomes + "AS O "
				+ "JOIN Categories AS C ON O.fIdCategory=C._id "
				+ "GROUP BY C._id";
		return db.rawQuery(sql, null);
	}

	public Cursor getOutcomesOfCategory(String categoryName, long idM) {
		int idC = getCategoryId(categoryName);
		return db.rawQuery(
				"select _id, Date, Title, Amount from Outcomes where fIdCategory="
						+ idC + " and fIdNote in "
						+ "(select _id from Notes where fIdBalance in "
						+ "(select _id from BalanceByYear where fIdMonth="
						+ idM + "))", null);
	}

	private int getCategoryId(String name) {
		Cursor c = db.rawQuery("select _id from Categories where Name='" + name
				+ "'", null);
		c.moveToFirst();
		return c.getInt(0);
	}

	public Cursor getYears() {
		Cursor c = db.rawQuery("select Name from Years", null);
		if(c.getCount() == 0){
			Calendar date = Calendar.getInstance();
			Year y = new Year(Integer.toString(date.get(Calendar.YEAR)));
			saveRecord(y);
		}
		return db.rawQuery("select Name from Years", null);
	}

	public void insertNewCategory(String name, long idNewC) {
		Category newCategory = new Category(name);
		db.update("Categories", newCategory.getContentValues(),
				"_id=" + idNewC, null);
		attachNewCategoryPrompt();
	}

	private void attachNewCategoryPrompt() {
		Category prompt = new Category("Add new...");
		db.insert("Categories", null, prompt.getContentValues());
	}

	public Cursor getActions(String table, String columns, long idM) {
		return db.rawQuery(
				"select _id, fIdNote, " + columns + " from " + table
						+ " where fIdNote in "
						+ "(select _id from Notes where fIdBalance in "
						+ "(select _id from BalanceByYear where fIdMonth="
						+ idM + "))", null);
	}

	public void deleteSelectedAction(int fIdNote, String table, Double amount) {
		Month m = getMonthSummary(fIdNote);

		double interest = getActionInterest(fIdNote);
		deleteAction(fIdNote, table);
		boolean isLast = isLastNote(fIdNote);
		long idBby = deleteNote(fIdNote);

		if (isLast) {
			long idM = deleteBalanceByYear(idBby);
			deleteMonth(idM);
		} else {

			final String ON_DECREASE = "DECREASE";

			switch (table) {
			case "Incomes":
				m.updateIncomes(amount, interest, ON_DECREASE);
				break;
			case "Outcomes":
				m.updateOutcomes(amount, ON_DECREASE);
				break;
			case "Borrowers":
				m.updateBorrowed(amount, ON_DECREASE);
				break;
			case "Lenders":
				m.updateLend(amount, ON_DECREASE);
				break;
			}
			updateMonthSummary(m);
		}
	}

	private double getActionInterest(int fIdNote) {
		Cursor c = db.rawQuery(
				"select _id,  Interest from Incomes where fIdNote in "
						+ "(select _id from Notes where _id=" + fIdNote + ")",
				null);
		return c.moveToFirst() ? c.getDouble(1): 0d;
	}

	public Month getMonthSummary(long idNote) {
		String sql = "select * from Months where _id in (select fIdMonth from BalanceByYear where _id in "
				+ "(select fIdBalance from Notes where _id=" + idNote + "))";

		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		Log.d("getSummary()",
				"Got month summary from database, name: " + c.getString(1));
		Month m = new Month(c.getInt(1), c.getDouble(2), c.getDouble(3),
				c.getDouble(4), c.getDouble(5), c.getDouble(6), c.getDouble(7));
		m.set_id(c.getLong(0));
		return m;
	}

	public boolean deleteAction(int fIdNote, String table) {
		return db.delete(table, "fIdNote=" + fIdNote, null) > 0;
	}

	public boolean isLastNote(int fIdNote) {
		Cursor c = db.rawQuery("select fIdBalance from Notes where _id="
				+ fIdNote, null);
		c.moveToFirst();
		long fIdBby = c.getLong(0);

		c = db.rawQuery("select _id from Notes where fIdBalance=" + fIdBby,
				null);
		Log.d("isLastNote()",
				" idBby=" + fIdBby + "and returned: " + c.getCount());
		return c.getCount() == 1;
	}

	public long deleteNote(int fIdNote) {
		Cursor c = db.rawQuery("select fIdBalance from Notes where _id="
				+ fIdNote, null);
		c.moveToFirst();
		long fIdBby = c.getLong(0);

		db.delete("Notes", "_id=" + fIdNote, null);
		Log.d("deleteNote()", "Deleted idN=" + fIdNote + "and returned fIdBby="
				+ fIdBby);
		return fIdBby;
	}

	public long deleteBalanceByYear(long idBby) {
		Cursor c = db.rawQuery("select fIdMonth from BalanceByYear where _id="
				+ idBby, null);
		c.moveToFirst();
		long fIdM = c.getLong(0);
		db.delete("BalanceByYear", "_id=" + idBby, null);

		Log.d("deleteBalanceByYear()", "Deleted idBby=" + idBby
				+ "and returned fIdM=" + fIdM);
		return fIdM;
	}

	public void deleteMonth(long idM) {
		db.delete("Months", "_id=" + idM, null);
		Log.d("deleteMonth()", "Deleted idM=" + idM);
	}

	public void updateMonthSummary(Month m) {
		String id = "_id=" + m.get_id();
		long i = db.update(m.getTableName(), m.getContentValues(), id, null);
		Log.d("updateMonthSummaryIntoDB()", "Updated rows: " + i);
	}

	public double getCurrentInterest() {
		Cursor c = db.rawQuery("select interest from Summary", null);
		c.moveToFirst();
		return c.getDouble(0);
	}

	public void exportToDownloadFolder() {
		String date = new SimpleDateFormat("yyyy.MM.dd").format(Calendar.getInstance().getTime());
		String backupName = "myBudgetSheet_" + date +".db";
		    try {
				File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), backupName); 
				File currentDB = context.getDatabasePath(DB_NAME); 
				if (currentDB.exists()) {
				    FileChannel src = new FileInputStream(currentDB).getChannel();
				    FileChannel dst = new FileOutputStream(backupDB).getChannel();
				    dst.transferFrom(src, 0, src.size());
				    src.close();
				    dst.close();
				    Toast.makeText(context, "Exported " + backupName + " to download folder.", Toast.LENGTH_LONG).show();

					Log.d("Exporting database", "Exported: " + backupName);
				}
			} catch (IOException e) {
				Toast.makeText(context, "Error. Try Again.", Toast.LENGTH_SHORT).show();
			}
	}
	
	public boolean isThereAnOutcome(String year){
		return db.rawQuery("SELECT Date FROM Outcomes WHERE Date LIKE '%" + year + "%'", null).getCount() != 0;
	}
}