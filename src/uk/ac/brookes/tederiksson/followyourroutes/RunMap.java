package uk.ac.brookes.tederiksson.followyourroutes;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class RunMap extends FragmentActivity {

	private GoogleMap mMap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_runmap);
	    
	    setUpMapIfNeeded();
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
    }

}
