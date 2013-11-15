package uk.ac.brookes.tederiksson.followyourroutes;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class TrackContentProvider extends ContentProvider {

	private TrackDatabaseHelper database;
	
	public static final String AUTHORITY = "uk.ac.brookes.tederiksson.followyourroutes.tracks";
	private static final String BASE_PATH = "tracks";
	
	public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+BASE_PATH);
	
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sUriMatcher.addURI(AUTHORITY, BASE_PATH, 1);
		sUriMatcher.addURI(AUTHORITY, BASE_PATH+"/#", 2);
	}
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues vals) {
		int uriType = sUriMatcher.match(uri);
		//int uriType = 2;
		SQLiteDatabase db = database.getWritableDatabase();
		Log.d(TrackContentProvider.class.getName(),"starting uritype: "+Integer.toString(uriType));
		long id = 0;
		switch(uriType) {
		case 2: break;
		case 1: 
			id = db.insert(TrackTable.TABLE_TRACKS, null, vals);
			Log.d(TrackContentProvider.class.getName(),"inserted");
			break;
		default:
			throw new IllegalArgumentException("Unknown URL: "+uri);
		}
		Log.d(TrackContentProvider.class.getName(),"id: "+Long.toString(id));
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH+"/"+id);
	}

	@Override
	public boolean onCreate() {
		database = new TrackDatabaseHelper(getContext());
		return (database != null);
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		queryBuilder.setTables(TrackTable.TABLE_TRACKS);
		
		int uriType = sUriMatcher.match(uri);
		switch(uriType) {
		case 1: break;
		case 2: 
			queryBuilder.appendWhere(TrackTable.COLUMN_ID+"="+uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: "+uri);
		}
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor c = queryBuilder.query(db, columns, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
