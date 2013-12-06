package uk.ac.brookes.tederiksson.followyourroutes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FindsRuns extends Activity {

	private Button buttonSearchRuns, buttonFindToMy;
	private EditText editTextSearchRuns;
	private TextView textViewNumberFound;
	private Context context;
	
	private ProgressDialog progress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    context = this;
	
	    setContentView(R.layout.activity_findruns);
	    
	    editTextSearchRuns = (EditText) findViewById(R.id.editTextSearchRuns);
	    editTextSearchRuns.setText(getSharedPreferences(SetOptions.prefs, 0).getString(SetOptions.userID, "-1"));
	    
	    buttonSearchRuns = (Button) findViewById(R.id.buttonSearchRuns);
	    buttonFindToMy = (Button) findViewById(R.id.buttonFindToMy);
	    
	    buttonFindToMy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, MyRuns.class));
			}
		});
	    
	    buttonFindToMy.setVisibility(View.GONE);
	    
	    textViewNumberFound = (TextView) findViewById(R.id.noRuns);
	    
	    progress = new ProgressDialog(context);
	    progress.setTitle("Downloading");
	    progress.setMessage("Getting those routes");
	    
	    buttonSearchRuns.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTextSearchRuns.getWindowToken(), 0);
				progress.show();
				RunDownloader runDownloader = new RunDownloader();
				runDownloader.execute(editTextSearchRuns.getText().toString());
			}
		});
	    
	}
	
	private class RunDownloader extends AsyncTask<String, Void, ArrayList<String>> {

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			progress.dismiss();
			if(result == null) {
				textViewNumberFound.setText("No runs found");
			} else {
				textViewNumberFound.setText("Found "+result.size()+" runs");
			}
			buttonFindToMy.setVisibility(View.VISIBLE);
			super.onPostExecute(result);
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			String result = ServerHandler.searchDatabase(params[0]);
			Log.d("result", "|"+result+"|");
			ArrayList<String> list = new ArrayList<String>();
			Collections.addAll(list, result.split(","));
			if(!result.equals("No routes found. ") && !result.equals(" ")) {
				Log.d("FindRuns", "Deleted: "+deleteUploaded());
				for(String runID : list) {
					Track track = new Track(ServerHandler.getXMLByRunID(runID));
					ContentValues values = new ContentValues();
					values.put(TrackTable.COLUMN_NAME, track.getName());
					values.put(TrackTable.COLUMN_USERID, track.getUserID());
					values.put(TrackTable.COLUMN_XML, track.getXml());
					values.put(TrackTable.COLUMN_UPLOADED, 1);
					getContentResolver().insert(TrackContentProvider.CONTENT_URI, values);
				}
			} else {
				if(result.equals(" ")) {
					Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
				}
				list = null;
			}
			
			return list;
			
		}
		
		public int deleteUploaded() {
			return getContentResolver().delete(TrackContentProvider.CONTENT_URI, TrackTable.COLUMN_UPLOADED+" = 1", null);
		}
		
	}

}
