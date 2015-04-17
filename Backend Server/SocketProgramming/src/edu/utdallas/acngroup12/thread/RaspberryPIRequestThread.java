package edu.utdallas.acngroup12.thread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import edu.utdallas.acngroup12.datastructure.*;

public class RaspberryPIRequestThread extends Thread{

	//Data Members!
	private Message incomingMessage;
	private Message outgoingMessage;
	private SocketChannel piSocketChannel;
	private DatabaseConnection databaseConnection;
	private HashMap<String, String> lockStatusMap;
	private ByteBuffer byteBuffer;
	private int MAX_BUF_SIZE = 1000;
	private int processStatus;
	private String identity;
	
	public RaspberryPIRequestThread(Message incomingMessage, SocketChannel piSocketChannel) {
		// TODO Auto-generated constructor stub
		this.incomingMessage = incomingMessage;
		this.databaseConnection = new DatabaseConnection();
		this.piSocketChannel = piSocketChannel;
		this.lockStatusMap = new HashMap<String, String>();
		this.identity = "Server";
		this.setOutgoingMessage(new Message(this.incomingMessage.getLockID(), 0, this.identity, this.incomingMessage.getSoftLockStatus(), this.incomingMessage.getHardLockStatus()));
		this.byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
		this.processStatus = -1;
	}
	
	public boolean sendMessage(SocketChannel outboundSocketChannel, Message outgoingMessage){
		this.byteBuffer.rewind();
		this.byteBuffer.clear();
		try {
			this.byteBuffer.put(Message.serialize(outgoingMessage));
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

	public void processRaspberryPIRequest(){
		//Set the Lock status in database as the new connection arrives
		this.lockStatusMap.put("LOCKID", this.incomingMessage.getLockID());
		this.lockStatusMap.put("SOFT_LOCK_STATUS", this.incomingMessage.getHardLockStatus());
		this.lockStatusMap.put("HARD_LOCK_STATUS", this.incomingMessage.getHardLockStatus());
		//Set current Location
		
		this.processStatus = this.databaseConnection.setLockStatus(this.lockStatusMap);
		if(this.processStatus > 0){
//			this.outgoingMessage.setRequestID(0);
			this.processStatus = this.databaseConnection.setPICoordinates(this.incomingMessage.getLockID(), this.incomingMessage.getLongitude(), this.incomingMessage.getLatitude());
			if(this.processStatus > 0){
				this.outgoingMessage.setSoftLockStatus(this.incomingMessage.getHardLockStatus());
				this.sendMessage(piSocketChannel, outgoingMessage);
			}						
		}
	}
	
	public void run(){
		System.out.println("Creating RaspberryPI Thread.!");
		this.processRaspberryPIRequest();
	}
	/**
	 * @return the incomingMessage
	 */
	public Message getIncomingMessage() {
		return incomingMessage;
	}

	/**
	 * @param incomingMessage the incomingMessage to set
	 */
	public void setIncomingMessage(Message incomingMessage) {
		this.incomingMessage = incomingMessage;
	}

	/**
	 * @return the databaseConnection
	 */
	public DatabaseConnection getDatabaseConnection() {
		return databaseConnection;
	}

	/**
	 * @param databaseConnection the databaseConnection to set
	 */
	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
	 * @return the lockStatusMap
	 */
	public HashMap<String, String> getLockStatusMap() {
		return lockStatusMap;
	}

	/**
	 * @param lockStatusMap the lockStatusMap to set
	 */
	public void setLockStatusMap(HashMap<String, String> lockStatusMap) {
		this.lockStatusMap = lockStatusMap;
	}

	/**
	 * @return the outgoingMessage
	 */
	public Message getOutgoingMessage() {
		return outgoingMessage;
	}

	/**
	 * @param outgoingMessage the outgoingMessage to set
	 */
	public void setOutgoingMessage(Message outgoingMessage) {
		this.outgoingMessage = outgoingMessage;
	}

	/**
	 * @return the piSocketChannel
	 */
	public SocketChannel getPiSocketChannel() {
		return piSocketChannel;
	}

	/**
	 * @param piSocketChannel the piSocketChannel to set
	 */
	public void setPiSocketChannel(SocketChannel piSocketChannel) {
		this.piSocketChannel = piSocketChannel;
	}

	/**
	 * @return the processStatus
	 */
	public int getProcessStatus() {
		return processStatus;
	}

	/**
	 * @param processStatus the processStatus to set
	 */
	public void setProcessStatus(int processStatus) {
		this.processStatus = processStatus;
	}

}
