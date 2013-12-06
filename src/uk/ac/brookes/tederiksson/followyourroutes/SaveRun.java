package uk.ac.brookes.tederiksson.followyourroutes;

import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SaveRun extends FragmentActivity {
	
	private Track track;
	
	private EditText editTextName, editTextUserID;
	private TextView textViewTopSpeed, textViewTime;
	private Button buttonSaveRun;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_saverun);
	    
	    Intent intent = getIntent();
	    
	    if (intent.getStringExtra("track") != null) {
	    	track = new Track(intent.getStringExtra("track"));
	    } else {
	    	Toast.makeText(getApplicationContext(), "Track is null", Toast.LENGTH_SHORT).show();
	    }
	    Log.d("SAVERUN", intent.getStringExtra("track"));
	    
	    editTextName = (EditText) findViewById(R.id.editTextSaveName);
	    editTextUserID = (EditText) findViewById(R.id.editTextSaveUserID);
	    editTextUserID.setText(getSharedPreferences(SetOptions.prefs, 0).getString(SetOptions.userID, "-1"));
	    
	    textViewTopSpeed = (TextView) findViewById(R.id.textViewTopSpeed);
	    textViewTopSpeed.setText(Float.toString(track.getTopSpeed()));
	    
	    textViewTime = (TextView) findViewById(R.id.textViewTime);
	    textViewTime.setText(DateFormat.format("hh:mm:ss", track.getTime()).toString());
	    
	    buttonSaveRun = (Button) findViewById(R.id.buttonSaveRun);
	    
	    buttonSaveRun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				track.setName(editTextName.getText().toString());
				track.setUserID(editTextUserID.getText().toString());
				RunUploader runUploader = new RunUploader();
				runUploader.execute(track);
			}
		});
	}
	

	@Override
	public void onBackPressed() {
		
	}
	
	private class RunUploader extends AsyncTask<Track, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			if(result)
				Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getApplicationContext(), "Upload failed, try later", Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(Track... params) {
			boolean uploaded = false;
			//Upload
			if (ServerHandler.uploadTrack(track.getXml()))
				uploaded = true;
			
			//add to db
			ContentValues values = new ContentValues();
			values.put(TrackTable.COLUMN_NAME, track.getName());
			values.put(TrackTable.COLUMN_USERID, track.getUserID());
			values.put(TrackTable.COLUMN_XML, track.getXml());
			values.put(TrackTable.COLUMN_UPLOADED, (uploaded)?1:0);
			getContentResolver().insert(TrackContentProvider.CONTENT_URI, values);
			
			
			return uploaded;
		}
		
	}

}
