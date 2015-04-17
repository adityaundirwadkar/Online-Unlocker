package edu.utdallas.acngroup12.thread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import edu.utdallas.acngroup12.datastructure.*;

public class MobileRequestThread extends Thread {

	private SocketChannel raspberryPISocketChannel;
	private SocketChannel mobileHostSocketChannel;
	private Message incomingMessage;
	private Message outgoingMessage;
	private DatabaseConnection databaseConnection;
	private ByteBuffer byteBuffer;
	private int MAX_BUF_SIZE = 1000;
	private HashMap<String, String> lockStatusMap;
	private int requestID;
	private String identity;
	private int radiusAllowed = 500; //Radius in Feet.
	
	public MobileRequestThread(SocketChannel mobileHostSocketChannel, SocketChannel raspberryPISocketChannel, Message incomingMessage, int requestID) {
		// TODO Auto-generated constructor stub
		this.raspberryPISocketChannel = raspberryPISocketChannel;
		this.mobileHostSocketChannel  = mobileHostSocketChannel;
		this.incomingMessage		  = incomingMessage;
		this.databaseConnection       = new DatabaseConnection();
		this.byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
		this.lockStatusMap = new HashMap<String, String>();
		this.requestID = requestID;
		this.identity = "Server";
	}
	
	public boolean sendMessage(SocketChannel outboundSocketChannel, Message outgoingMessage){
		this.byteBuffer.rewind();
		this.byteBuffer.clear();
		try {
			this.byteBuffer.put(Message.serialize(this.outgoingMessage));
			this.byteBuffer.flip();
			while(byteBuffer.hasRemaining()) {
				outboundSocketChannel.write(byteBuffer);
			}
			//End processing
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}			
	}
	
	public Message readMessage(SocketChannel inboundSocketChannel){
		this.byteBuffer.rewind();
		this.byteBuffer.clear();
		try {
			inboundSocketChannel.read(this.byteBuffer);
			return (Message)Message.deserialize(byteBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Message(null, 0, null, null, null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Message(null, 0, null, null, null);
		}
		
	}
	public void processMobileHostRequest(){
		//If RaspberryPI connection is absent.
		if(this.raspberryPISocketChannel == null ){
			this.setPIOnlineStatus(this.incomingMessage.getLockID());
			//Send Lock offline message to Mobile Host.
			this.outgoingMessage = new Message(incomingMessage.getLockID(), this.requestID, this.identity, "Offline", "Lock Currently Offline.. Please try again Later.");
			this.sendMessage(this.mobileHostSocketChannel, this.outgoingMessage);
		}
		else {
			System.out.println("Longitude: "+incomingMessage.getLongitude()+" Latitude: "+incomingMessage.getLatitude());
			HashMap<String, String> isOperationAllowed = new HashMap<String, String>();		
			isOperationAllowed = this.databaseConnection.getLockProximity(incomingMessage.getLongitude(), incomingMessage.getLatitude(), incomingMessage.getLockID(), this.radiusAllowed);
			if(isOperationAllowed.get("CAN_UNLOCK").equalsIgnoreCase("NO")){
				this.outgoingMessage = new Message(incomingMessage.getLockID(), this.requestID, this.identity, "Offline", "You are not in the proximity of 50ft. of the Lock..! Unable to unlock");
				this.sendMessage(this.mobileHostSocketChannel, this.outgoingMessage);
			} else if(isOperationAllowed.get("CAN_UNLOCK").equalsIgnoreCase("YES")){
				
				this.outgoingMessage = new Message(incomingMessage.getLockID(), this.requestID, this.identity, null, null);
				//Retrieve current status from database.
				this.lockStatusMap = this.databaseConnection.getLockStatus(incomingMessage.getLockID());
				//If the current lock Soft Status differs from the new requested one.!
				if(!(incomingMessage.getSoftLockStatus().equalsIgnoreCase(this.lockStatusMap.get("SOFT_LOCK_STATUS"))) && (!this.lockStatusMap.get("LOCKID").equalsIgnoreCase("LOCK NOT FOUND!"))){
					//Put this values in this.outgoingMessage
					
					//this.outgoingMessage.setSoftLockStatus(this.lockStatusMap.get("SOFT_LOCK_STATUS"));
					//Set the status of SoftState of outgoing message to that of incoming message
					this.outgoingMessage.setSoftLockStatus(this.incomingMessage.getSoftLockStatus());
					
					//this.outgoingMessage.setHardLockStatus(this.lockStatusMap.get("HARD_LOCK_STATUS"));
					//Send this message to Raspberry PI and then confirm with Mobile Host.
					boolean requestToPI = this.sendMessage(this.raspberryPISocketChannel, this.outgoingMessage);
					if(requestToPI){
						//Successful processing at the PI, by reading the response from the PI.
						Message responseFromPI = this.readMessage(this.raspberryPISocketChannel);
						
						if(!(responseFromPI.getSender() == null)){
							try{
								if((responseFromPI.getHardLockStatus().equalsIgnoreCase(this.outgoingMessage.getSoftLockStatus()))){
									//No exception, respond to mobile host with the same response.
									try{
										//Update the database record.
										this.lockStatusMap.put("HARD_LOCK_STATUS", responseFromPI.getHardLockStatus());
										this.lockStatusMap.put("SOFT_LOCK_STATUS", responseFromPI.getHardLockStatus());
										this.databaseConnection.setLockStatus(lockStatusMap);							
										this.outgoingMessage.setHardLockStatus(responseFromPI.getHardLockStatus());
										@SuppressWarnings("unused")
										boolean responseFromMobile = this.sendMessage(this.mobileHostSocketChannel, this.outgoingMessage);
									}
									catch (Exception e){
										this.setPIOnlineStatus(this.incomingMessage.getLockID());
										this.outgoingMessage.setHardLockStatus("Offline");
										@SuppressWarnings("unused")
										boolean responseFromMobile = this.sendMessage(this.mobileHostSocketChannel, this.outgoingMessage);
									}
								}						
							} catch (NullPointerException npe){
								this.setPIOnlineStatus(this.incomingMessage.getLockID());
								this.outgoingMessage.setHardLockStatus("Offline");
								@SuppressWarnings("unused")
								boolean responseFromMobile = this.sendMessage(this.mobileHostSocketChannel, this.outgoingMessage);
							}											
						}
						else{
							this.setPIOnlineStatus(this.incomingMessage.getLockID());
							this.outgoingMessage.setHardLockStatus("Offline");
							@SuppressWarnings("unused")
							boolean responseFromMobile = this.sendMessage(this.mobileHostSocketChannel, this.outgoingMessage);
						}
					}
					else{
						this.setPIOnlineStatus(this.incomingMessage.getLockID());
						this.outgoingMessage = new Message(incomingMessage.getLockID(), this.requestID, this.identity, incomingMessage.getSoftLockStatus(), "Offline");
						this.sendMessage(this.mobileHostSocketChannel, this.outgoingMessage);
					}
				}
				else{
					this.outgoingMessage = new Message(incomingMessage.getLockID(), this.requestID, this.identity, incomingMessage.getSoftLockStatus(), "Unable to complete request!");
					this.sendMessage(this.mobileHostSocketChannel, this.outgoingMessage);
				}
			}
		}
		
	}
	
	public int setPIOnlineStatus(String lockID){
		HashMap<String, String> newPIOnlineStatus = new HashMap<String, String>();
		newPIOnlineStatus.put("LOCKID",(lockID));
		newPIOnlineStatus.put("IS_ONLINE","NO");
		int updateStatus = this.databaseConnection.setPIOnlineStatus(newPIOnlineStatus);
		System.out.println("Update status: " +updateStatus);
		return updateStatus;
	}
	
	public void run(){
		System.out.println("Creating a new thread for MobileHost request!");
		this.processMobileHostRequest();
	}
}