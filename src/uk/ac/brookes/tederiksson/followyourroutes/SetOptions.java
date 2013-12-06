package uk.ac.brookes.tederiksson.followyourroutes;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

public class SetOptions extends Activity {
	
	public final static String prefs = "prefs";
	public final static String autoUpload = "autoupload";
	public final static String userID = "userid";
	public final static String userIDDefault = "11013382";
	public final static boolean autoUploadDefault = true; 
	
	private EditText editTextuserID;
	private Button buttonUploadRuns, buttonSave;
	private ToggleButton toggleAutoUpload;

	private SharedPreferences settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_setoptions);
	    
	    settings = getSharedPreferences(prefs, 0);
	    
	    editTextuserID = (EditText) findViewById(R.id.editTextUserID);
	    
	    editTextuserID.setText(settings.getString(userID, "-1"));
	    
	    toggleAutoUpload = (ToggleButton) findViewById(R.id.toggleButtonAutoUpload);
	    
	    toggleAutoUpload.setChecked(settings.getBoolean(autoUpload, true));
	    
	    buttonSave = (Button) findViewById(R.id.buttonSaveOptions);
	    
	    buttonSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(userID, editTextuserID.getText().toString());
				editor.putBoolean(autoUpload, toggleAutoUpload.isChecked());
				editor.commit();
				finish();
			}
		});
	    
	}

}
