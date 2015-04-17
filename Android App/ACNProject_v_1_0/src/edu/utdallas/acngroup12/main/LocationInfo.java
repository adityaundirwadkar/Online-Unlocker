package edu.utdallas.acngroup12.main;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class LocationInfo extends Service implements LocationListener {

    Context mContext;    
    boolean isGPSEnabled = false; // flag for GPS status
    boolean isNetworkEnabled = false; // flag for network status
    boolean canGetLocation = false; // flag for GPS status
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 15; // The minimum distance to change Updates in meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // The minimum time between updates in milliseconds
    protected LocationManager locationManager; // Declaring a Location Manager
 
    public LocationInfo(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

	public Location getLocation() {
    	String context = Context.LOCATION_SERVICE;
        try {
            locationManager = (LocationManager) mContext.getSystemService(context);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); // getting GPS status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // getting network status
 
            if (!isGPSEnabled && !isNetworkEnabled)
                Log.d("LocationInfo", "no network provider found");
            else {
            	Log.d("LocationInfo", "network provider located");
                this.canGetLocation = true;
                
                if (isNetworkEnabled) {	//get location from Network Provider
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("LocationInfo", "Network enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("LocationInfo", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
     
    
    public void stopUsingGPS(){  //Stop using GPS listenerCalling this function will stop using GPS in your app
        if(locationManager != null){
            locationManager.removeUpdates(LocationInfo.this);
        }       
    }
     
    public double getLatitude(){  // Function to get latitude
        if(location != null){
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    public double getLongitude(){  //Function to get longitude
        if(location != null){
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
     
    public boolean canGetLocation() {  //Function to check GPS/wifi enabled
        return this.canGetLocation;
    }
     
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
 
    @Override
    public void onLocationChanged(Location location) {
    }
 
    @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
