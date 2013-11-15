package uk.ac.brookes.tederiksson.followyourroutes;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class MyRuns extends Activity {

	private ListView list;
	private TextView noItems;
	
	private SimpleCursorAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_myruns);
	    
	    list = (ListView) findViewById(R.id.listMyRuns);
	    noItems = (TextView) findViewById(R.id.noRuns);
	    
	    String[] from = {TrackTable.COLUMN_NAME, TrackTable.COLUMN_USERID};
	    int[] to = {R.id.listItemName, R.id.listItemUserID};
	    String[] cols = {TrackTable.COLUMN_ID, TrackTable.COLUMN_NAME, TrackTable.COLUMN_USERID};
	    Cursor c = getContentResolver().query(TrackContentProvider.CONTENT_URI, cols, null, null, null);
	    adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_item, c, from, to, 0);
	    if(c.getCount() > 0) {
	    	noItems.setVisibility(View.GONE);
	    }
	    list.setAdapter(adapter);
	    
	}

}
