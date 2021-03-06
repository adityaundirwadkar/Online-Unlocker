package edu.utdallas.acngroup12.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogSingleButton {
	/**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     * */
	public void showAlertDialog(Context context, String title, String message,
            Boolean status) {
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    	alertDialogBuilder.setTitle(title);
    	// set dialog message
    	alertDialogBuilder.setMessage(message);
    	// Setting OK Button
    	alertDialogBuilder.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.cancel();
            }
        });;
        /*if(status != null){
        	// Setting alert dialog icon
        	alertDialogBuilder.setIcon((status) ? R.drawable.success : R.drawable.failure);
        } */      
    	// create alert dialog
    	AlertDialog alertDialog = alertDialogBuilder.create();
    	// show alert
    	alertDialog.show();    	
    }
}
