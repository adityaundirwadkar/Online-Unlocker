package edu.utdallas.acngroup12.datastructure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class Message implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int MAX_BUF_SIZE=1000;
	private String lockID; //Represents the lock
	private int requestID; //Represents the request that being processed!
	private String sender; //Represent the senderID
	private String hardLockStatus, softLockStatus; //Represents the states of the said field.
	private double longitude, latitude;
	
//	private String lockStatus;
	
	
	public Message(String lockID, int requestID, String sender, String softLockStatus, String hardLockStatus){
		this.setLockID(lockID);
//		this.setLockStatus(lockStatus);
		this.setSender(sender);
		this.setHardLockStatus(hardLockStatus);
		this.setSoftLockStatus(softLockStatus);
		this.setRequestID(requestID);
	}
	
	
	public static byte[] serialize(Object obj) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(obj);
	    return out.toByteArray();
	}
	
	public static Object deserialize(ByteBuffer parabyteBuffer) throws IOException, ClassNotFoundException {
		parabyteBuffer.position(0);
		parabyteBuffer.limit(MAX_BUF_SIZE);
		byte[] bufArr = new byte[parabyteBuffer.remaining()];
		parabyteBuffer.get(bufArr);		
		ByteArrayInputStream in = new ByteArrayInputStream(bufArr);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return is.readObject();
	}


	/**
	 * @return the lockID
	 */
	public String getLockID() {
		return lockID;
	}


	/**
	 * @param lockID the lockID to set
	 */
	public void setLockID(String lockID) {
		this.lockID = lockID;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}


	/**
	 * @param sender the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}


	/**
	 * @return the hardLockStatus
	 */
	public String getHardLockStatus() {
		return hardLockStatus;
	}


	/**
	 * @param hardLockStatus the hardLockStatus to set
	 */
	public void setHardLockStatus(String hardLockStatus) {
		this.hardLockStatus = hardLockStatus;
	}


	/**
	 * @return the softLockStatus
	 */
	public String getSoftLockStatus() {
		return softLockStatus;
	}


	/**
	 * @param softLockStatus the softLockStatus to set
	 */
	public void setSoftLockStatus(String softLockStatus) {
		this.softLockStatus = softLockStatus;
	}
	
	/**
	 * @return the requestID
	 */
	public int getRequestID() {
		return requestID;
	}


	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}


	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}


	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}


	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public String toString(){
		String stringState;		
		stringState= "LID: " +getLockID() + " | RID: " +getRequestID() +" | Sender: " +getSender() + " | SoftStatus: " +getSoftLockStatus() + " | HardStatus: " +getHardLockStatus();
		return stringState;
	}
}