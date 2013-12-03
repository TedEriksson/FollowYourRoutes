package uk.ac.brookes.tederiksson.followyourroutes;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class RunMap extends FragmentActivity {

	private String name = "NOTSET";
	private GoogleMap mMap;
	private CheckBox lock;
	private Location myLocation;
	private Track track, passedTrack;
	private LocationManager locationManager;
	private TextView textViewTime;
	private Button startButton;
	private Chronometer chronometer;
	private long currentTime = 0;
	private boolean timerRunning = false;
	
	LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			myLocation = location;
			if(lock.isChecked()) {
				gotoLocation(myLocation);
			}
			if(timerRunning) {
				track.addPoint(myLocation);
				mMap.addPolyline(track.getPolyLineOptions().color(Color.RED));
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_runmap);
	    
	    lock = (CheckBox) findViewById(R.id.checkBoxLockCamera);
	    textViewTime = (TextView) findViewById(R.id.runTime);
	    startButton = (Button) findViewById(R.id.buttonStart);
	    chronometer = (Chronometer) findViewById(R.id.chronometerRun);
	    
	    track = new Track();
	    
	    setUpMapIfNeeded();
	    
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
        
        lock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					gotoLocation(myLocation);
				}
			}
		});
        
        Intent intent = getIntent();
	    long runID = intent.getLongExtra("runID", -1);
	    if(runID != -1) {
	    	String[] cols = {TrackTable.COLUMN_XML, TrackTable.COLUMN_USERID};
			Cursor c = getContentResolver().query(TrackContentProvider.CONTENT_URI, cols, TrackTable.COLUMN_ID+"="+runID, null, null);
			if(c != null && c.moveToFirst()) {
				int index = c.getColumnIndex(TrackTable.COLUMN_XML);
				passedTrack = new Track(c.getString(index));
				//Toast.makeText(getApplicationContext(), c.getString(index), Toast.LENGTH_LONG).show();
			}
			c.close();
			name = passedTrack.getName();
			track.setName(name);
		    setTitle(name);
		    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Track.LocationToLatLng(passedTrack.getFirstLocation()),12));
		    PolylineOptions poly = passedTrack.getPolyLineOptions().color(getResources().getColor(R.color.transparentRun));
		    mMap.addPolyline(poly);
	    } else {
	    	gotoLocation(myLocation);
	    }
	    startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(timerRunning) {
					stopTimer();
					Intent intent = new Intent(getApplicationContext(), SaveRun.class);
					intent.putExtra("track", track.getXml(name));
					startActivity(intent);
					onDestroy();
				} else {
					startTimer(true);
				}
			}
		});
	    
	}
	
	private void startTimer(boolean fromZero) {
		startButton.setText(R.string.stop);
		timerRunning = true;
		if(fromZero) {
			chronometer.setBase(SystemClock.elapsedRealtime());
			chronometer.start();
		}
	}
	
	private void stopTimer() {
		chronometer.stop();
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
        
        mMap.setMyLocationEnabled(true);
        
        UiSettings settings = mMap.getUiSettings();
        
        settings.setZoomControlsEnabled(false);
        settings.setRotateGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
        settings.setMyLocationButtonEnabled(false);
    }
	
	@Override
	protected void onDestroy() {
		locationManager.removeUpdates(locationListener);
		super.onStop();
	}

	private void gotoLocation(Location location) {
		if(myLocation != null)
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Track.LocationToLatLng(location), 16));
	}
}
