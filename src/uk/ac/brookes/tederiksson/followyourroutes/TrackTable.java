package uk.ac.brookes.tederiksson.followyourroutes;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TrackTable {
	public static final String TABLE_TRACKS = "tracks";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_USERID = "userid";
	public static final String COLUMN_XML = "xml";
	
	private static final String DATABASE_CREATE = "create table " + TABLE_TRACKS + "(" + COLUMN_ID
		      + " integer primary key autoincrement, " + COLUMN_NAME
		      + " text not null, " + COLUMN_XML + " text not null, " + COLUMN_USERID + " text not null);";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TrackTable.class.getName(),"Upgraded from "+oldVersion+" to "+newVersion);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
	    onCreate(db);
	}
}
