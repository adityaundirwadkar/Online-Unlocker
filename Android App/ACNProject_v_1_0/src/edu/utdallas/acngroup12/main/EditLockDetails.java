/**
 * 
 */
package edu.utdallas.acngroup12.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.acnproject_v_1_0.R;

import edu.utdallas.acngroup12.datastructure.JSONParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author ADITYA
 *
 */
public class EditLockDetails extends Activity {

	/**
	 * 
	 */
	private EditText lockComments;
	private CheckBox setCurrentLocation;
	private Button saveButton;
	private Button cancelButton;
	private ProgressDialog progressDialog;
	private AlertDialogSaveButton alert = new AlertDialogSaveButton();
	private String lockID, comments, latitude, longitude;
	final String TAG_LOCKID = "lockID";
	final String TAG_COMMENTS = "comments";
	private int successStatus = -1;
	final Context mContext = this;
	private LocationInfo homeLocation = null;
		
	final String TAG_SUCCESS = "success";
	final String TAG_RESPONSE = "message";
	final String TAG_HOME_LONGITUDE = "home_longitude";
	final String TAG_HOME_LATITUDE = "home_latitude";
	final String TAG_DBLOCKINFO = "dblockinfo";	
	final String postMethod = "POST";
	
	public EditLockDetails() {
		// TODO Auto-generated constructor stub
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.edit_lock_details);
	    // TODO Auto-generated method stub
	    this.lockID = getIntent().getStringExtra(TAG_LOCKID);
	    this.comments = getIntent().getStringExtra(TAG_COMMENTS);
	    
	    
	    lockComments = (EditText) findViewById(R.id.editLockComments);
	    setCurrentLocation = (CheckBox) findViewById(R.id.addCurrentLoc);
	    saveButton = (Button) findViewById(R.id.saveButton);
	    cancelButton = (Button) findViewById(R.id.cancelButton);
	    
	    
	    //Set the values for comment EditText
	    lockComments.setText(this.comments);
	    //Make all the elements invisible untill ASYNC Task is completed!
	    setCurrentLocation.setVisibility(View.INVISIBLE);
	    lockComments.setVisibility(View.INVISIBLE);
	    saveButton.setVisibility(View.INVISIBLE);
	    cancelButton.setVisibility(View.INVISIBLE);
	    //Call AsyncTask to retrieve the information from database!
	    GetLockDetails getLockDetails = new GetLockDetails();
	    getLockDetails.execute(this.lockID);
	    //Save Button Listener
	    saveButtonListener();
	    //Cancel Button Listener
	    cancelButtonListener();
	    //setCurrentLocation check box listener.
	    setCurrentLocationListener();
	}
	
	public void saveButtonListener(){
		//Save the information in the database
	    saveButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				showAlertBox();				
			}	    	
	    });
	}
	
	public void cancelButtonListener(){
		//cancel and load the previous activity i.e. UserInfo
	    cancelButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}	    	
	    });
	}
	
	//Listener for Check Box
	public void setCurrentLocationListener(){
		setCurrentLocation.setOnClickListener(new OnClickListener() {			 
			  @Override
			  public void onClick(View v) {
				  if (((CheckBox) v).isChecked()) {					  
					  //get current location details.
					  homeLocation = new LocationInfo(EditLockDetails.this);
					  double homeLatitude = homeLocation.getLatitude();  // get current latitude from LocationInfo
					  double homeLongitude = homeLocation.getLongitude(); // get current longitude from LocationInfo
					  Toast.makeText(EditLockDetails.this,"Once you set home location, it can not be undone!", Toast.LENGTH_LONG).show();
				  }
				  else{
					  homeLocation = null;
				  }
			  }
			});
	}
	
	//Display Alert Box!
	public void showAlertBox(){
		//Display an alert box with Save and Cancel options
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		//Set title
		alertDialogBuilder.setTitle("Save Details");
		// set dialog message
		alertDialogBuilder.setMessage("Are you sure?");
		alertDialogBuilder.setCancelable(false);
    	alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	//Call ASYNC Task to save the details.
            	double homeLatitude, homeLongitude;
            	UpdateLockDetails updateLockDetails = new UpdateLockDetails();
            	String userComments = lockComments.getText().toString();
            	if(!(homeLocation == null)){
            		homeLatitude = homeLocation.getLatitude();  // get current latitude from LocationInfo
            		homeLongitude = homeLocation.getLongitude(); // get current longitude from LocationInfo
            		updateLockDetails.execute(lockID, userComments,Double.toString(homeLongitude), Double.toString(homeLatitude));
            	}
            	else{
            		updateLockDetails.execute(lockID, userComments,null, null);
            	}
            	dialog.cancel();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	//Cancel the Dialog box
            	dialog.cancel();
            }
        });
        // create alert dialog
    	AlertDialog alertDialog = alertDialogBuilder.create();
    	// show alert
    	alertDialog.show(); 
	}
	
	class GetLockDetails extends AsyncTask<String, String, HashMap<String, String>> {

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EditLockDetails.this,"Please wait...", "Retrieving data ...", true);
        }
		
		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			//final String webServerURL = "http://10.0.2.2:80/android/db_get_lock_location_info.php";
			final String webServerURL = "http://acngroup12.utdallas.edu/android/db_get_lock_location_info.php";
			JSONArray JSONArray = null;
			JSONParser JSONParser = new JSONParser();
			JSONObject jsonObject;
			// JSON Node names
			
			List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
			int i=0;			
		    httpParams.add(new BasicNameValuePair("lockid", params[0]));
        	try {
        		//Getting JSON string from URL
    		    jsonObject = JSONParser.makeHttpRequest(webServerURL, postMethod, httpParams);
    		    //Check your log for JSON response
                Log.d("GET LOCK LOCATION INFO", jsonObject.toString());
                HashMap<String, String> result = new HashMap<String, String>();
                result.put(TAG_SUCCESS, jsonObject.getString(TAG_SUCCESS));
                
                //successStatus = jsonObject.getInt(TAG_SUCCESS);
				JSONArray = jsonObject.getJSONArray(TAG_DBLOCKINFO);				
				while(i < JSONArray.length()){
                	JSONObject c = JSONArray.getJSONObject(i);
                	// Storing each JSON item in variable
                	comments = c.getString(TAG_COMMENTS);
                	longitude  = c.getString(TAG_HOME_LONGITUDE);
                	latitude = c.getString(TAG_HOME_LATITUDE);
                	//Add it to HashMap to give it to postResult method
                	result.put(TAG_COMMENTS, comments);
                	result.put(TAG_HOME_LONGITUDE, longitude);
                	result.put(TAG_HOME_LATITUDE, latitude);               	
                	i++;
                }
				if(longitude.equalsIgnoreCase("1") && latitude.equalsIgnoreCase("1")){
					result.put("CHECKED", "YES"); 					
				}
				else {
					result.put("CHECKED", "NO");
				}
				return result;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new HashMap<String, String>();
			}
		}

		@Override
        protected void onPostExecute(HashMap<String, String> result) {
			try{Thread.sleep(2000);}catch(Exception e){}
			progressDialog.dismiss();
			String dbComments = result.get(TAG_COMMENTS);
			lockComments.setText(dbComments);
			
			if((result.get("CHECKED")).equalsIgnoreCase("YES")){
				setCurrentLocation.setVisibility(View.VISIBLE);
			}
			else if((result.get("CHECKED")).equalsIgnoreCase("NO")){
				setCurrentLocation.setVisibility(View.INVISIBLE);
				//Set the home location as null
				homeLocation = null;
			}			
			lockComments.setVisibility(View.VISIBLE);
			saveButton.setVisibility(View.VISIBLE);
			cancelButton.setVisibility(View.VISIBLE);
        }
	}
	
	class UpdateLockDetails extends AsyncTask<String, String, HashMap<String, String>> {
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EditLockDetails.this,"Please wait...", "Updating Record..", true);
        }
		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			final String webServerURL;
			//final String webServerURL = "http://acngroup12.utdallas.edu/android/db_get_lock_location_info.php";
			//JSONArray JSONArray = null;
			JSONParser JSONParser = new JSONParser();
			JSONObject jsonObject;
			// JSON Node names
			
			final String postMethod = "GET";
			List<NameValuePair> httpParams = new ArrayList<NameValuePair>();			
			//UpdateLockDetails.execute(lockID, comments, longitude, latitude);
			httpParams.add(new BasicNameValuePair(TAG_LOCKID, params[0]));
			httpParams.add(new BasicNameValuePair(TAG_COMMENTS, params[1]));
			if(!(params[2] == null) && !(params[3] == null)){
				httpParams.add(new BasicNameValuePair(TAG_HOME_LONGITUDE, params[2]));
				httpParams.add(new BasicNameValuePair(TAG_HOME_LATITUDE, params[3]));
				//webServerURL = "http://10.0.2.2:80/android/db_set_lock_location_info.php";
				webServerURL = "http://acngroup12.utdallas.edu/android/db_set_lock_location_info.php";				
			} else {
				//webServerURL = "http://10.0.2.2:80/android/db_set_lock_comments_info.php";
				webServerURL = "http://acngroup12.utdallas.edu/android/db_set_lock_comments_info.php";
			}			
			try {
        		//Getting JSON string from URL
    		    jsonObject = JSONParser.makeHttpRequest(webServerURL, "GET", httpParams);
    		    //Check your log for JSON response
                Log.d("Set LOCK LOCATION INFO", jsonObject.toString());
				HashMap<String, String> result = new HashMap<String, String>();
				//Add values into the hashmap
				result.put(TAG_SUCCESS, jsonObject.getString(TAG_SUCCESS));
				result.put(TAG_RESPONSE, jsonObject.getString(TAG_RESPONSE));				
				return result;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new HashMap<String, String>();
			}			
		}
		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			try{Thread.sleep(2000);}catch(Exception e){}
			progressDialog.dismiss();
			String success = result.get(TAG_SUCCESS);
			String message = result.get(TAG_RESPONSE);
			finish();				
			Toast.makeText(EditLockDetails.this, message, Toast.LENGTH_LONG).show();
			/*if(success.equalsIgnoreCase("1")){
				//Update was successful
				finish();				
				Toast.makeText(EditLockDetails.this,"Record Updated successfully!", Toast.LENGTH_LONG).show();
			}
			else{
				//Update failed.
				finish();
				Toast.makeText(EditLockDetails.this,"We could not connect to Server! Please try again! \n" +message, Toast.LENGTH_LONG).show();
			}*/
		}
	}
}
