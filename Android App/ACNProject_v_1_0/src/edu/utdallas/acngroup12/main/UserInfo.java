package edu.utdallas.acngroup12.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.acnproject_v_1_0.R;

import edu.utdallas.acngroup12.datastructure.JSONParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class UserInfo extends Activity {
    final String TAG_USERNAME = "username";
    final String TAG_PASSWORD = "password";
	String username = null;
	String password = null;
	ListView lockListView;
	private ListView userLockListView;
	private TextView emptyText; 
	private TextView lockDescription;
	private EditText lockID;
	private EditText comments;
	private Button logout;
	private Button addNewLock;
	// Progress Dialog
	private ProgressDialog progressDialog = null;
	private ArrayList<LockInfo> lockInfoData = null;
	Context context;
	private Timer autoUpdate;
	private boolean headerAddedFlag = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        username = getIntent().getStringExtra(TAG_USERNAME);
        password = getIntent().getStringExtra(TAG_PASSWORD);
        
        userLockListView = (ListView)findViewById(R.id.list);
        
        //For empty List
        emptyText = (TextView) findViewById(R.id.emptyText);
        emptyText.setVisibility(View.INVISIBLE);
        lockDescription = (TextView) findViewById(R.id.lockDescription);
        lockDescription.setVisibility(View.INVISIBLE);
        lockID = (EditText) findViewById(R.id.lockID);
        lockID.setVisibility(View.INVISIBLE);
        comments= (EditText) findViewById(R.id.comments);
        comments.setVisibility(View.INVISIBLE);
        
        logout = (Button) findViewById(R.id.logout);
        logout.setVisibility(View.INVISIBLE);
        addNewLock = (Button) findViewById(R.id.addNewLock);
        addNewLock.setVisibility(View.INVISIBLE);
        context = getApplicationContext();
        //new LoadAllLocks().execute(username, password);       
    }
    
   @Override
    public void onResume() {
     super.onResume();
     autoUpdate = new Timer();
     autoUpdate.schedule(new TimerTask() {
      @Override
      public void run() {
       runOnUiThread(new Runnable() {
        public void run() {
        	new LoadAllLocks().execute(username, password);
        }
       });
      }
     }, 0, 10000); // updates each 10 seconds
    }
    
    @Override
    public void onPause() {
     autoUpdate.cancel();
     super.onPause();
    }

    @Override
    public void onBackPressed() {
    	   Log.i("HA", "Finishing");
    	   Intent intent = new Intent(Intent.ACTION_MAIN);
    	   intent.addCategory(Intent.CATEGORY_HOME);
    	   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	   startActivity(intent);
    	   finish();
    }
    
    class LoadAllLocks extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!headerAddedFlag){
            	progressDialog = ProgressDialog.show(UserInfo.this,"Please wait...", "Retrieving data ...", true);
            }
        }
        
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			//final String webServerURL = "http://10.0.2.2:80/android/db_get_lock_info.php";
			final String webServerURL = "http://acngroup12.utdallas.edu/android/db_get_lock_info.php";
			
			JSONArray JSONArray = null;
			JSONParser JSONParser = new JSONParser();
			JSONObject jsonObject;
			// JSON Node names
			final String TAG_SUCCESS = "success";
			final String TAG_DBLOCKINFO = "dblockinfo";
			final String TAG_COMMENTS = "comments";
			final String TAG_LOCKNAME = "lockid";
			final String TAG_STATUS = "status";
			final String TAG_IS_ONLINE = "is_online";
			final String postMethod = "POST";
			int successStatus = -1;
			List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
			//try{Thread.sleep(5000);}catch(Exception e){}
        	// updating UI from Background Thread
        	lockInfoData = new ArrayList<LockInfo>();
        	httpParams.add(new BasicNameValuePair("username", params[0]));
        	httpParams.add(new BasicNameValuePair("password", params[1]));
        	jsonObject = null;
            //getting JSON string from URL
            jsonObject = JSONParser.makeHttpRequest(webServerURL, postMethod, httpParams);
            try{
            	successStatus = jsonObject.getInt(TAG_SUCCESS);
            	Log.d("User login Attempt", jsonObject.toString());
            	if (successStatus == 1){ 
	          		  JSONArray = jsonObject.getJSONArray(TAG_DBLOCKINFO);
	          		  int i=0;
	          		  while(i < JSONArray.length()){
		                      	JSONObject c = JSONArray.getJSONObject(i);
		                      	// Storing each JSON item in variable
		                      	String isOnline = c.getString(TAG_IS_ONLINE);
		                      	if(isOnline.equalsIgnoreCase("YES")){
		                      		String status = c.getString(TAG_STATUS);			                      	
			                      	if (status.equalsIgnoreCase("LOCKED")){
			                      		lockInfoData.add(new LockInfo(true,(c.getString(TAG_LOCKNAME)),(c.getString(TAG_COMMENTS))));
			                      	}
			                      	else{
			                      		lockInfoData.add(new LockInfo(false,(c.getString(TAG_LOCKNAME)),(c.getString(TAG_COMMENTS))));
			                      	}
		                      	}
		                      	else if(isOnline.equalsIgnoreCase("NO")){
		                      		lockInfoData.add(new LockInfo(false,(c.getString(TAG_LOCKNAME)),(c.getString(TAG_COMMENTS) + " (Offline*)")));
		                      	}		                      	                      	
		                        i++;
		              }	          		
          		  }
            	else{
            		//Code to print no locks available
            	}
            }
            catch(Exception e){
            	e.printStackTrace();
            }
			return null;
		}
		
		/**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
        	 	//dismiss the dialog after getting all products
            	progressDialog.dismiss();
        	    runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					LockAdapter adapter = new LockAdapter(UserInfo.this, R.layout.list_view_lock_info,lockInfoData);
					//Add header only once!
					if(!headerAddedFlag){
						View header = (View)getLayoutInflater().inflate(R.layout.list_view_header, null);					
						userLockListView.addHeaderView(header);					    
					    TextView textView1 = (TextView) header.findViewById(R.id.headerText);
					    textView1.setText("Welcome " +username+ ", Please review your information.");
					    headerAddedFlag = true;
					}
	                // updating ListView
				    userLockListView.setAdapter(adapter);
				    if(lockInfoData.size() < 1){
				    	emptyText.setVisibility(View.VISIBLE);
				    	lockDescription.setVisibility(View.VISIBLE);
				        lockID.setVisibility(View.VISIBLE);
				        comments.setVisibility(View.VISIBLE);
				    	addNewLock.setVisibility(View.VISIBLE);
				    	logout.setVisibility(View.VISIBLE);
				    	userLockListView.setVisibility(View.INVISIBLE);
					}
				}            	
            });
         }       
      }
}