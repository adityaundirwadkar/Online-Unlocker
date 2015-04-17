package edu.utdallas.acngroup12.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;

import com.example.acnproject_v_1_0.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import edu.utdallas.acngroup12.datastructure.*;

public class LockAdapter extends ArrayAdapter<LockInfo>{
	Context context;
 	int layoutResourceId;   
	ArrayList<LockInfo> data = null;
	private ProgressDialog progressDialog = null;
	AlertDialogSingleButton alert = new AlertDialogSingleButton();
	private LocationInfo mCurrentLocation = null;
	
    public LockAdapter(Context context, int layoutResourceId, ArrayList<LockInfo> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LockInfoHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);           
            holder = new LockInfoHolder();
            holder.lockSwitch = (Switch) row.findViewById(R.id.switch1);
            holder.comments = (TextView)row.findViewById(R.id.comments);
            holder.lockID = (TextView)row.findViewById(R.id.lockID);
            row.setTag(holder);
        }
        else
        {
            holder = (LockInfoHolder)row.getTag();
        }
        LockInfo lockInfo = data.get(position);
 
        holder.lockSwitch.setChecked(lockInfo.status);
        holder.comments.setText(lockInfo.comments);
        holder.lockID.setText(lockInfo.lockID);
        holder.lockID.setVisibility(View.INVISIBLE);
        //If Lock is open then put it in red color
        if(!lockInfo.status){
        	holder.comments.setTextColor(Color.RED);        	        	
        }
        else{
        	holder.comments.setTextColor(Color.BLACK);
        }
        holder.lockSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				// TODO Auto-generated method stub
				/*AlertDialogSingleButton alert = new AlertDialogSingleButton();*/
				final Context context = getContext();
				View v = (View) buttonView.getParent();
				final TextView comments =(TextView) v.findViewById(R.id.comments);
				final TextView lockid =(TextView) v.findViewById(R.id.lockID);
				final Switch lockSwitch = (Switch) v.findViewById(R.id.switch1);
				//Get current Location!
				mCurrentLocation = new LocationInfo(context);
				
				
				if(lockSwitch.isChecked()){
					progressDialog = ProgressDialog.show(context,"Please wait...", "Trying to lock " +lockid.getText().toString(), true);
				}
				else{
					progressDialog = ProgressDialog.show(context,"Please wait...", "Trying to unlock " +lockid.getText().toString(), true);
				}
				
				Thread changeLockStatus = new Thread(new Runnable() {
					
					//String hostServer = "10.0.2.2";
					String hostServer = "acngroup12.utdallas.edu";
					int port = 2100;
					SocketChannel hostChannel;
					Message outgoingMessage;
					Message incomingMessage = null;
					int MAX_BUF_SIZE=1000;
					ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
					String comment = comments.getText().toString();				
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//Display progress dialog
						
						//Set the outgoing message!
						if(lockSwitch.isChecked()){
							//progressDialog = ProgressDialog.show(context,"Please wait...", "Trying to lock " +lockid.getText().toString(), true);
							outgoingMessage = new Message(lockid.getText().toString(), 0, "MobileHost", "LOCKED", null);							
						}
						else{
							//progressDialog = ProgressDialog.show(context,"Please wait...", "Trying to unlock " +lockid.getText().toString(), true);
							outgoingMessage = new Message(lockid.getText().toString(), 0, "MobileHost", "UNLOCKED", null);					
						}
						outgoingMessage.setLongitude(mCurrentLocation.getLongitude());
						outgoingMessage.setLatitude(mCurrentLocation.getLatitude());
						
						//Try connecting with the server!
						try {
							hostChannel = SocketChannel.open();
							hostChannel.connect(new InetSocketAddress(hostServer, port));
							hostChannel.socket().setSoTimeout(15);
							
							//System.out.println("Connected to Server : " +hostChannel.getRemoteAddress());
							byteBuffer.clear();
							byteBuffer.put(Message.serialize(outgoingMessage));
							byteBuffer.flip();
							//Write the data to channel
							while(byteBuffer.hasRemaining()) {
								hostChannel.write(byteBuffer);
							}
							hostChannel.configureBlocking(true);
							byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
							byteBuffer.rewind();
							byteBuffer.clear();
							boolean loopVariable = true;
							//Keep reading until you receive a message in the stream.
							while(loopVariable){
								try{
									hostChannel.configureBlocking(false);
									byteBuffer.rewind();
									byteBuffer.clear();
									hostChannel.read(byteBuffer);
									incomingMessage  = (Message)Message.deserialize(byteBuffer);
									loopVariable = false;
									System.out.println("Recieved connection from : " +incomingMessage.getSender());
									System.out.println(incomingMessage.toString());
									handleMessageFromServer(outgoingMessage, incomingMessage, comment);
								}catch(IOException e){
									loopVariable = true;
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
//							hostChannel.read(byteBuffer);
//							incomingMessage  = (Message)Message.deserialize(byteBuffer);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							outgoingMessage = new Message(lockid.getText().toString(), 0, "MobileHost", "Offline", "Offline");
							incomingMessage = new Message(lockid.getText().toString(), 0, "MobileHost", "Offline", "Offline");
							handleMessageFromServer(outgoingMessage, incomingMessage, comment);
						} catch (UnresolvedAddressException urae){
							outgoingMessage = new Message(lockid.getText().toString(), 0, "MobileHost", "Offline", "Offline");
							incomingMessage = new Message(lockid.getText().toString(), 0, "MobileHost", "Offline", "Offline");
							handleMessageFromServer(outgoingMessage, incomingMessage, comment);
						}
					}
					
					private void handleMessageFromServer(Message sentMessage,Message receivedMessage, String comment){
						android.os.Message handlerMessage = handler.obtainMessage(); 
						Bundle bundle = new Bundle();
						if(sentMessage.getSoftLockStatus().equalsIgnoreCase(receivedMessage.getHardLockStatus())){
							bundle.putString("lockID", lockid.getText().toString());
							bundle.putString("lockStatus", receivedMessage.getHardLockStatus());
						}
						else{
							bundle.putString("lockID", lockid.getText().toString());
							bundle.putString("lockStatus", receivedMessage.getHardLockStatus());
						}
						handlerMessage.setData(bundle);
						handler.sendMessage(handlerMessage);
					}
					
					@SuppressLint("HandlerLeak") 
					Handler handler = new Handler(){
						public void handleMessage(android.os.Message message) {
							@SuppressWarnings("unused")
							String lockID = message.getData().getString("lockID");
							String lockStatus = message.getData().getString("lockStatus");
							progressDialog.dismiss();
							if(lockStatus.equalsIgnoreCase("LOCKED")){
								comments.setTextColor(Color.BLACK);								
							}
							else{
								comments.setTextColor(Color.RED);
								lockSwitch.setChecked(false);
							}
							if(lockStatus.equalsIgnoreCase("LOCKED") || lockStatus.equalsIgnoreCase("UNLOCKED")){
								alert.showAlertDialog(context, "Switch", "Lock for " +comment+" " +lockStatus+ " successfully! " , false);	
							}
							else{
								alert.showAlertDialog(context, "Switch", lockStatus , false);
							}
						}
					};			
				});
				changeLockStatus.start();
			}}); 
        
        
        holder.comments.setOnClickListener(new View.OnClickListener(){
        	@Override
        	   public void onClick(View v) {
        		//AlertDialogSingleButton alert = new AlertDialogSingleButton();
        		final Context context = getContext();
        		View parentView = (View) v.getParent();
        		TextView comments =(TextView) parentView.findViewById(R.id.comments);
        		final TextView lockid =(TextView) parentView.findViewById(R.id.lockID);
        		//Edit Lock Details
        		Intent intent = new Intent(getContext(), EditLockDetails.class);
        		intent.putExtra("lockID", lockid.getText().toString());
        		intent.putExtra("comments", comments.getText().toString());
        		context.startActivity(intent);
        		//alert.showAlertDialog(context, "TextView", "You have clicked on TextView for "+comments.getText().toString()+ ", Need to put code to edit the settings of individual lock!", false);
        		//Call a procedure of ASYNC class.
        	   }
        });
        return row;
    }
   
    static class LockInfoHolder
    {
    	Switch lockSwitch;
    	TextView comments;
    	TextView lockID;
    }
}