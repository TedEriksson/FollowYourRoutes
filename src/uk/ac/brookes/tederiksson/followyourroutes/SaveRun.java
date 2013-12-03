package uk.ac.brookes.tederiksson.followyourroutes;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;

public class SaveRun extends FragmentActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_main);
	    
	    Intent intent = getIntent();
	    Log.d("SAVERUN", intent.getStringExtra("track"));
	}

}
