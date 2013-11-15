package uk.ac.brookes.tederiksson.followyourroutes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TrackDatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "tracks.db";
	public static final int DATABASE_VERSION = 1;
	
	public TrackDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		TrackTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		TrackTable.onUpgrade(db, oldVersion, newVersion);		
	}

}
