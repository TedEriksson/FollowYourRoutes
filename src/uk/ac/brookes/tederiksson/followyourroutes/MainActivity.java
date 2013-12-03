package uk.ac.brookes.tederiksson.followyourroutes;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Button buttonStartNewRun, buttonFindARun, buttonMyRuns, buttonOptions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		buttonStartNewRun = (Button) findViewById(R.id.buttonStartNewRun);
		
		buttonStartNewRun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), RunMap.class));
			}
		});
		
		buttonMyRuns = (Button) findViewById(R.id.buttonMyRuns);
		
		buttonMyRuns.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), MyRuns.class));
			}
		});
		
		buttonFindARun = (Button) findViewById(R.id.buttonFindRuns);
		
		buttonFindARun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), FindsRuns.class));
			}
		});
		
		buttonOptions = (Button) findViewById(R.id.buttonOptions);
		
		buttonOptions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), SetOptions.class));
			}
		});
		
//		//TESTING THE DATABASE
//		Random rand = new Random();
//		ContentValues values = new ContentValues();
//		values.put(TrackTable.COLUMN_NAME, "NAME "+ rand.nextInt(100));
//		values.put(TrackTable.COLUMN_USERID, "NULLY");
//		values.put(TrackTable.COLUMN_XML, "NULLY");
//		getContentResolver().insert(TrackContentProvider.CONTENT_URI, values);
//		
//		String[] cols = {TrackTable.COLUMN_NAME, TrackTable.COLUMN_USERID};
//		Cursor c = getContentResolver().query(TrackContentProvider.CONTENT_URI, cols, null, null, null);
//		if(c != null && c.moveToFirst()) {
//			int index = c.getColumnIndex(TrackTable.COLUMN_NAME);
//			Log.d("DATABASE", c.getString(index));
//			index = c.getColumnIndex(TrackTable.COLUMN_USERID);
//			Log.d("DATABASE", c.getString(index));
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
