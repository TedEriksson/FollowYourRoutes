package uk.ac.brookes.tederiksson.followyourroutes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

public class PreviewRun extends FragmentActivity {

	private long runID;
	private Track track;
	private GoogleMap mMap;
	private Button startRunButton;
	private TextView textViewTopSpeed, textViewTime,textViewAvgSpeed, textViewTotalDistance;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_previewrun);
	    setUpMapIfNeeded();
	    Intent intent = getIntent();
	    runID = intent.getLongExtra("runID", -1);
	    if(runID != -1) {
	    	String[] cols = {TrackTable.COLUMN_XML, TrackTable.COLUMN_USERID};
			Cursor c = getContentResolver().query(TrackContentProvider.CONTENT_URI, cols, TrackTable.COLUMN_ID+"="+runID, null, null);
			if(c != null && c.moveToFirst()) {
				int index = c.getColumnIndex(TrackTable.COLUMN_XML);
				track = new Track(c.getString(index));
				//Toast.makeText(getApplicationContext(), c.getString(index), Toast.LENGTH_LONG).show();
			}
	    }
	    setTitle(track.getName());
	    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Track.LocationToLatLng(track.getFirstLocation()),16));
	    mMap.addPolyline(track.getPolyLineOptions());
	    
	    textViewTopSpeed = (TextView) findViewById(R.id.textViewTopSpeed);
	    textViewTopSpeed.setText(String.format("%.2fkph", (track.getTopSpeed()*3.6)));
	    
	    textViewAvgSpeed = (TextView) findViewById(R.id.textViewAverageSpeed);
	    textViewAvgSpeed.setText(String.format("%.2fkph", (track.getAverageSpeed()*3.6)));
	    
	    textViewTotalDistance = (TextView) findViewById(R.id.textViewTotalDistance);
	    textViewTotalDistance.setText(String.format("%.2fm", track.getTotalDistance()));
	    
	    textViewTime = (TextView) findViewById(R.id.textViewTime);
	    textViewTime.setText(DateFormat.format("mm:ss", track.getTime()).toString());
	    
	    this.overridePendingTransition(R.anim.right_toleft_in,
                R.anim.right_to_left_out);
	    
	    startRunButton = (Button) findViewById(R.id.buttonRunIt);
	    
	    startRunButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentWithID = new Intent(getApplicationContext(), RunMap.class);
				intentWithID.putExtra("runID", runID);
				startActivity(intentWithID);
			}
		});
	    
	    Log.d("TopSpeed", Float.toString(track.getTopSpeed()));
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

}