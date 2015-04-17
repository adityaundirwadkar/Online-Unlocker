package edu.utdallas.acngroup12.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogSaveButton {
	/**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     * */
	public void showAlertDialog(Context context, String title, String message) {
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    	alertDialogBuilder.setTitle(title);
    	// set dialog message
    	alertDialogBuilder.setMessage(message);
    	// Setting OK Button
    	alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	
            }
        });;
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.cancel();
            }
        });;
    	// create alert dialog
    	AlertDialog alertDialog = alertDialogBuilder.create();
    	// show alert
    	alertDialog.show();    	
    }
}
