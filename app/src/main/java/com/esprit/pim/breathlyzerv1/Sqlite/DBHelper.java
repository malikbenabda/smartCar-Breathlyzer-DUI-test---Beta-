package com.esprit.pim.breathlyzerv1.Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	public static final String TABLE_CONTACT = "contacts";
	
	public static final String ID_CONTACT = "id";
	public static final String NAME_CONTACT = "name";
	public static final String NUMBER_CONTACT = "number";
	public static final String LOGO_ARTICLE = "logo";

	private static final String CREATE_CONTACT = "CREATE TABLE " + TABLE_CONTACT + " ("+
			ID_CONTACT + " INTEGER, "+
			NAME_CONTACT + " TEXT, "+
			NUMBER_CONTACT + " TEXT, "+
								LOGO_ARTICLE + " BLOB);";

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Create Data Base
		db.execSQL(CREATE_CONTACT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old, int newVersion) {
		// TODO Re-Create Data Base
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT + ";");
		onCreate(db);
	}

}
