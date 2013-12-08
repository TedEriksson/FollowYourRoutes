package uk.ac.brookes.tederiksson.followyourroutes;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

public class SaveRun extends FragmentActivity {
	
	private Track track;
	
	private EditText editTextName, editTextUserID;
	private TextView textViewTopSpeed, textViewTime,textViewAvgSpeed, textViewTotalDistance;
	private Button buttonSaveRun;
	private ProgressDialog dialog;
	private GoogleMap mMap;

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
	    textViewTopSpeed.setText(String.format("%.2fkph", (track.getTopSpeed()*3.6)));
	    
	    textViewAvgSpeed = (TextView) findViewById(R.id.textViewAverageSpeed);
	    textViewAvgSpeed.setText(String.format("%.2fkph", (track.getAverageSpeed()*3.6)));
	    
	    textViewTotalDistance = (TextView) findViewById(R.id.textViewTotalDistance);
	    textViewTotalDistance.setText(String.format("%.2fm", track.getTotalDistance()));
	    
	    textViewTime = (TextView) findViewById(R.id.textViewTime);
	    textViewTime.setText(DateFormat.format("mm:ss", track.getTime()).toString());
	    
	    dialog = new ProgressDialog(SaveRun.this);
	    dialog.setMessage("Uploading Track");
	    dialog.setCancelable(false);
	    dialog.setCanceledOnTouchOutside(false);
	    
	    buttonSaveRun = (Button) findViewById(R.id.buttonSaveRun);
	    
	    buttonSaveRun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					dialog.show();
					track.setName(editTextName.getText().toString());
					track.setUserID(editTextUserID.getText().toString());
					Log.d("SaveRun", "UserID: "+track.getUserID());
					RunUploader runUploader = new RunUploader();
					runUploader.execute(track);
			}
		});
	    
	    setUpMapIfNeeded();
	    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Track.LocationToLatLng(track.getFirstLocation()),16));
	    mMap.addPolyline(track.getPolyLineOptions());
	}
	
	private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap == null) {
            Log.d("RunMap", "Map failed");
            return;

        }
     // Initialise map options. 
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        UiSettings settings = mMap.getUiSettings();
        
        settings.setZoomControlsEnabled(false);
        settings.setRotateGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
    }
	

	@Override
	public void onBackPressed() {
		
	}
	
	private class RunUploader extends AsyncTask<Track, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if(result)
				Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getApplicationContext(), "Upload failed/Auto upload is diabled/No internet connection. Run saved to device", Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
		}

		@Override
		protected Boolean doInBackground(Track... params) {
			boolean uploaded = false;
			//Upload
			if (isConnected() && getSharedPreferences(SetOptions.prefs, 0).getBoolean(SetOptions.autoUpload, true) && ServerHandler.uploadTrack(track.getXml()))
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
		
		private boolean isConnected() {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			return (cm.getActiveNetworkInfo() != null);
		}
		
	}

}
