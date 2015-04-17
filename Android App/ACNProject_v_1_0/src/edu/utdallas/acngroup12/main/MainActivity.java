package edu.utdallas.acngroup12.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.acnproject_v_1_0.R;

import edu.utdallas.acngroup12.datastructure.JSONParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import android.os.StrictMode;
//import android.app.ProgressDialog;
//import org.json.JSONException;
//import android.content.Context;
//import java.util.HashMap;
//import org.w3c.dom.Text;

public class MainActivity extends Activity {

	EditText usernameMain;
	EditText passwordMain;
	Button loginMain;
	Button signupMain;
	TextView errorText;
	int successStatus = -1;
	AlertDialogSingleButton alert = new AlertDialogSingleButton();
	private ProgressDialog progressDialog = null;
	 // url to get all products list
   
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_screen);
		//Get values from MainActivity
		usernameMain = (EditText) findViewById(R.id.editLockComments);
		passwordMain = (EditText) findViewById(R.id.passwordField);
		loginMain    = (Button)   findViewById(R.id.loginButton);
		//errorText 	 = (TextView) findViewById(R.id.errorText);
		//Set it invisible unless user provides incorrect username or password!
		//errorText.setVisibility(View.INVISIBLE);
		signupMain = (Button)   findViewById(R.id.signupButton);
		
		//Set focus on username field.
		usernameMain.requestFocus();
		
		//Define action for login button
		loginMain.setOnClickListener(new View.OnClickListener(){
			//Add method to verify username and password.
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String username = usernameMain.getText().toString();
				String password = passwordMain.getText().toString();
				if(username.trim().length() == 0 ){
					alert.showAlertDialog(MainActivity.this, "Login failed..", "Please Enter Username", false);
				}
				else if(password.trim().length() == 0){
					alert.showAlertDialog(MainActivity.this, "Login failed..", "Please Enter Password", false);
				}
				else{	
					new VerifyUser().execute(username,password);
				}		
			}			
		});	
		
		//New User
		signupMain.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),NewUserSignUp.class);
				startActivity(intent);
                //finish();
			}			
		});
    }
    
    @Override
    public void onBackPressed() {
    	   Log.i("HA", "Finishing");
    	   Intent intent = new Intent(Intent.ACTION_MAIN);
    	   intent.addCategory(Intent.CATEGORY_HOME);
    	   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	   startActivity(intent);
    	 }
    
    class VerifyUser extends AsyncTask<String, String, String>{
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,"Please wait...", "Connecting to Server...", true);
        }
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// Building Parameters
			//final String webServerURL = "http://10.0.2.2:80/android/db_verify_user.php";
			final String webServerURL = "http://acngroup12.utdallas.edu/android/db_verify_user.php";
			JSONArray JSONArray = null;
			JSONParser JSONParser = new JSONParser();
			JSONObject jsonObject;
			// JSON Node names
			final String TAG_SUCCESS = "success";
			final String TAG_DBUSERINFO = "dbuserinfo";
			final String TAG_USERNAME = "username";
			final String TAG_PASSWORD = "password";
			final String TAG_CREATED_AT = "created_at";
			final String postMethod = "POST";
			List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
		    httpParams.add(new BasicNameValuePair("username", params[0]));
		    httpParams.add(new BasicNameValuePair("password", params[1]));
		    try{
			    // getting JSON string from URL
			    jsonObject = JSONParser.makeHttpRequest(webServerURL, postMethod, httpParams);
			    	
			    //Check your log for json response
	            Log.d("User login Attempt", jsonObject.toString());            
            	/*int success = json.getInt(TAG_SUCCESS);*/
            	successStatus = jsonObject.getInt(TAG_SUCCESS);
            	/*if (success == 1) {*/
            	if (successStatus == 1){ 
            	    // successfully logged in
            		Intent intent = new Intent(getApplicationContext(),UserInfo.class);
            		JSONArray = jsonObject.getJSONArray(TAG_DBUSERINFO);
                    //Retrieve user details
                    int i=0;
                    //String dbUsername = null,dbPassword = null,dbCreatedAt = null;
                    @SuppressWarnings("unused")
					String dbUsername = null,dbCreatedAt = null;
                    /*while(i < userInfo.length()){*/
                    while(i < JSONArray.length()){
                    	JSONObject c = JSONArray.getJSONObject(i);
                    	// Storing each json item in variable
                    	dbUsername = c.getString(TAG_USERNAME);
                        //dbPassword = c.getString(TAG_PASSWORD);
                        dbCreatedAt = c.getString(TAG_CREATED_AT);
                        i++;
                    }                    
                    intent.putExtra(TAG_USERNAME,dbUsername);
                    intent.putExtra(TAG_PASSWORD,params[1]);
                    startActivity(intent);
                    finish();
                }
            	else{
            		//Put login error
            		//errorText.setText("*Invalid Username or Password!");
            		//errorText.setVisibility(View.VISIBLE);
            	}
            }
		    catch (JSONException jsone){
		    	jsone.printStackTrace();
            	Log.d("Caught null pointer at json", "in JSONException");
		    }
            catch (NullPointerException npe){
            	npe.printStackTrace();
            	Log.d("Caught null pointer", "in null pointer");
            }
            catch (Exception e){
            	e.printStackTrace();
            }            
            return null;
		}
		protected void onPostExecute(String file_url) {
			progressDialog.dismiss();
			if(successStatus == 0){				
				//errorText.setVisibility(View.VISIBLE);
				passwordMain.setText("");
				alert.showAlertDialog(MainActivity.this, "Login failed..", "We weren't able to find a user matching those credentials.", false);
			}
			else if(successStatus == -1){
				passwordMain.setText("");
				alert.showAlertDialog(MainActivity.this, "Login failed..", "We weren't able to connect to the server! Please check your connectivity and try again.", false);
				usernameMain.setText("");
			}
		}
	}    
}