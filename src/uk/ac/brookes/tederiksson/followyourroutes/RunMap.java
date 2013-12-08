package uk.ac.brookes.tederiksson.followyourroutes;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.ProgressDialog;
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
	private Button startButton;
	private Chronometer chronometer;
	private long currentTime = 0;
	private boolean timerRunning = false;
	private ProgressDialog findingLocation;
	
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
			findingLocation.dismiss();
			if(isBetterLocation(location))
				myLocation = location;
			if(lock.isChecked()) {
				gotoLocation(myLocation);
			}
			if(timerRunning) {
				track.addPoint(myLocation);
				mMap.addPolyline(track.getPolyLineOptions().color(Color.RED));
			}
		}
		
		private boolean isBetterLocation(Location location) {
			if(myLocation == null) {
				return true;
			}
			final long TEN_SECONDS = 10000;
			long timeDelta = location.getTime() - myLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > TEN_SECONDS;
			boolean isSignificantlyOlder = timeDelta < -TEN_SECONDS;
			boolean isNewer = timeDelta > 0;
			if(isSignificantlyNewer) {
				return true;
			} else if (isSignificantlyOlder) {
				return false;
			}
			
			int accuracyDelta = (int) (location.getAccuracy() - myLocation.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isMuchLessAccurate = accuracyDelta > 200;
			boolean isFromSameProvider = isSameProvider(myLocation.getProvider(), location.getProvider());
			if(isMoreAccurate) {
				return true;
			} else if (isNewer && !isLessAccurate) {
				return true;
			} else if (isNewer && !isMuchLessAccurate && isFromSameProvider) {
				return true;
			}
			return false;
		}
		
		protected boolean isSameProvider(String p1, String p2) {
			if(p1 == null)
				return p2 == null;
			return p1.equals(p2);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_runmap);
	    
	    findingLocation = new ProgressDialog(RunMap.this);
	    findingLocation.setMessage("Finding your location");
	    findingLocation.show();
	    findingLocation.setCancelable(false);
	    findingLocation.setCanceledOnTouchOutside(false);
	    
	    lock = (CheckBox) findViewById(R.id.checkBoxLockCamera);
	    startButton = (Button) findViewById(R.id.buttonStart);
	    chronometer = (Chronometer) findViewById(R.id.chronometerRun);
	    
	    track = new Track();
	    
	    setUpMapIfNeeded();
	    
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
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
	    	lock.setChecked(true);
	    }
	    startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("RunMap", "Added");
				//myLocation.setTime()
				
				if(timerRunning) {
					Log.d("RunMap", "Stop run");
					locationManager.removeUpdates(locationListener);
					stopTimer();
					track.addPoint(myLocation);
					Intent intent = new Intent(getApplicationContext(), SaveRun.class);
					intent.putExtra("track", track.getXml());
					startActivity(intent);
				} else {
					Log.d("RunMap", "Start run");
					startTimer(true);
					track.addPoint(myLocation);
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
