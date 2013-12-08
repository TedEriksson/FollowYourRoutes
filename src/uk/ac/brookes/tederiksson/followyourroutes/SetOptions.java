package uk.ac.brookes.tederiksson.followyourroutes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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
	private ProgressDialog dialog;
	private Track track;

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
	    

	    dialog = new ProgressDialog(SetOptions.this);
	    dialog.setMessage("Uploading Track(s)");
	    dialog.setCancelable(false);
	    dialog.setCanceledOnTouchOutside(false);
	    
	    buttonUploadRuns = (Button) findViewById(R.id.buttonUploadNow);
	    buttonUploadRuns.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RunUploader runUploader = new RunUploader();
				runUploader.execute();
			}
		});
	    
	}
	
	private class RunUploader extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if(result)
				Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getApplicationContext(), "Upload failed/No Internet connection", Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(Void...params) {
			String[] cols = {TrackTable.COLUMN_ID, TrackTable.COLUMN_XML};
			String selection = TrackTable.COLUMN_UPLOADED + "= 0";
			Cursor c = getContentResolver().query(TrackContentProvider.CONTENT_URI, cols, selection, null, null);
			boolean uploaded = true;
			if(isConnected()) {
				while(c.moveToNext()) {
					track = new Track(c.getString(c.getColumnIndex(TrackTable.COLUMN_XML)));
					if (ServerHandler.uploadTrack(track.getXml())) {
						ContentValues values = new ContentValues();
						values.put(TrackTable.COLUMN_NAME, track.getName());
						values.put(TrackTable.COLUMN_USERID, track.getUserID());
						values.put(TrackTable.COLUMN_XML, track.getXml());
						values.put(TrackTable.COLUMN_UPLOADED, 1);
						getContentResolver().insert(TrackContentProvider.CONTENT_URI, values);
						
					} else {
						uploaded = false;
					}
				}
			} else {
				uploaded = false;
			}
			
			//Upload
			if (uploaded) {
				deleteNotUploaded();
			}
			
			return uploaded;
		}
		
		private int deleteNotUploaded() {
			return getContentResolver().delete(TrackContentProvider.CONTENT_URI, TrackTable.COLUMN_UPLOADED+" = 0", null);
		}
		
		private boolean isConnected() {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			return (cm.getActiveNetworkInfo() != null);
		}
		
	}

}
