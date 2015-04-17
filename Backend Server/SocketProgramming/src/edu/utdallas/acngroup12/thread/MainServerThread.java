package edu.utdallas.acngroup12.thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import edu.utdallas.acngroup12.datastructure.*;

public class MainServerThread extends Thread{
	private String serverName;
	private boolean receive; 
	private ServerSocketChannel newServerSocketChannel;
	private ByteBuffer byteBuffer;
	private int port = -1;
	private int MAX_BUF_SIZE = 1000;	
	private Message recievedMessage;
	//<LockID, SocketChannel for LockID>
	private HashMap<String, SocketChannel> raspberryPISocketChannels;
	private HashMap<String, Integer> requestIDMap;
		
	public MainServerThread(String serverName,int port) throws IOException{
		this.serverName = serverName;
		this.port = port; 
		this.receive =true;
		this.recievedMessage = null;
		this.newServerSocketChannel = null;
		this.raspberryPISocketChannels = new HashMap<String, SocketChannel>();
		this.requestIDMap = new HashMap<String, Integer>();
		this.byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
	}
	
	public void initiateServer(){
		try {
			this.newServerSocketChannel = ServerSocketChannel.open();
			this.newServerSocketChannel.socket().bind(new InetSocketAddress(this.serverName,this.port));
			this.newServerSocketChannel.socket().setSoTimeout(500);
			this.newServerSocketChannel.configureBlocking(false);
			System.out.println("Starting Server at port: " +this.port);
			
			//We just started server so set is_online status of every node as NO.
			DatabaseConnection databaseConnection = new DatabaseConnection();
			int updateStatus = databaseConnection.setPIOnlineStatus("NO");
			if(updateStatus > 0){
				//Connected to database and update was successful.
				System.out.println("Updated the status of each lock successfully.");
				this.receive = true;
			}
			else{
				//Some error has occurred please check database connectivity!
				System.out.println("Some error has occurred please check database connectivity!");
				this.receive = false;
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(this.receive){
			try{
				SocketChannel incomingConnection = newServerSocketChannel.accept();
				if(incomingConnection == null){
					//System.out.println("Waiting for incoming connections..!");
				}
				else{
					System.out.println("Recieved a new connection..!");
					this.byteBuffer.rewind();
					this.byteBuffer.clear();
					//Create a new thread by reading incoming data.
					incomingConnection.read(byteBuffer);
					this.recievedMessage = null;
					this.recievedMessage  = (Message)Message.deserialize(byteBuffer);
					System.out.println("Recieved connection from : " +recievedMessage.getSender());
					//System.out.println("LockID : " +recievedMessage.getLockID()+ " Current Lock SoftStatus : " +recievedMessage.getSoftLockStatus()+ " Current Lock HardStatus : " +recievedMessage.getHardLockStatus());
					System.out.println(this.recievedMessage.toString());
					//Process the connections depending upon the sender.
					//Sender is a mobile host.
					if(this.recievedMessage.getSender().equalsIgnoreCase("MobileHost")){
						//Start the RaspberryPI thread.
						Thread newRaspberryPIThread;
						if(this.raspberryPISocketChannels.get(this.recievedMessage.getLockID()) == null){
							newRaspberryPIThread = new MobileRequestThread(incomingConnection, null, this.recievedMessage, 0);
						}
						else{
							newRaspberryPIThread = new MobileRequestThread(incomingConnection, this.raspberryPISocketChannels.get(this.recievedMessage.getLockID()), this.recievedMessage, this.getRequestIDMap(this.recievedMessage.getLockID()));
							this.setRequestIDMap(this.recievedMessage.getLockID(), (this.getRequestIDMap(this.recievedMessage.getLockID())+1));
						}				
						newRaspberryPIThread.start();
					}
					//Sender is a Raspberry PI
					else if(this.recievedMessage.getSender().equalsIgnoreCase("RaspberryPI")){
						//PI just came online so set its IS_ONLINE as YES!
						DatabaseConnection databaseConnection = new DatabaseConnection();
						HashMap<String, String> newPIOnlineStatus = new HashMap<String, String>();
						newPIOnlineStatus.put("LOCKID",(this.recievedMessage.getLockID()));
						newPIOnlineStatus.put("IS_ONLINE","YES");
						int updateStatus = databaseConnection.setPIOnlineStatus(newPIOnlineStatus);
						System.out.println("Update status: " +updateStatus);
						
						if(this.raspberryPISocketChannels.get(this.recievedMessage.getLockID()) == null){
							System.out.println("Lock "+this.recievedMessage.getLockID()+ " now online!");
							this.raspberryPISocketChannels.put(this.recievedMessage.getLockID(), incomingConnection);
							this.setRequestIDMap(this.recievedMessage.getLockID(), 1);
							//Respond to PI's connection status! 
							Thread RaspberryPIRequestThread = new RaspberryPIRequestThread(recievedMessage, incomingConnection);
							RaspberryPIRequestThread.start();						
							System.out.println("Server Status: " +incomingConnection.isConnected());
						}
						else{
							System.out.println("Lock "+recievedMessage.getLockID()+ " went offline and back online!");
							System.out.println("Server Status: " +incomingConnection.isOpen());							
							this.raspberryPISocketChannels.put(this.recievedMessage.getLockID(), incomingConnection);
							int newRequestID = this.getRequestIDMap(this.recievedMessage.getLockID());
							this.setRequestIDMap(this.recievedMessage.getLockID(), (newRequestID+20));
							//Respond to PI's connection status!
							Thread RaspberryPIRequestThread = new RaspberryPIRequestThread(recievedMessage, incomingConnection);
							RaspberryPIRequestThread.start();
							
						}
					}									
					System.out.println("Currently "+this.raspberryPISocketChannels.size()+ " lock(s) are online!!");
				}
			}
			catch (NullPointerException npe) {
				// TODO Auto-generated catch block		
				npe.printStackTrace();
				
			}
			catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to cast incoming object to Message.!");
			e.printStackTrace();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Shutting down the server!!!");
	}
	
	public void run(){
		this.initiateServer();
	}

	public String getServerName() {
		return serverName;
	}

	public boolean isReceive() {
		return receive;
	}

	public int getPort() {
		return port;
	}

	public Message getRecievedMessage() {
		return recievedMessage;
	}

	public HashMap<String, SocketChannel> getRaspberryPISocketChannels() {
		return raspberryPISocketChannels;
	}
	
	public SocketChannel getRaspberryPISocketChannels(String lockID) {
		return raspberryPISocketChannels.get(lockID);
	}

	public HashMap<String, Integer> getRequestIDMap() {
		return requestIDMap;
	}
	
	public Integer getRequestIDMap(String lockID) {
		return requestIDMap.get(lockID);
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setReceive(boolean receive) {
		this.receive = receive;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setRecievedMessage(Message recievedMessage) {
		this.recievedMessage = recievedMessage;
	}

	public void setRaspberryPISocketChannels(
			HashMap<String, SocketChannel> raspberryPISocketChannels) {
		this.raspberryPISocketChannels = raspberryPISocketChannels;
	}
	
	public void setRaspberryPISocketChannels(String lockID, SocketChannel socketChannel) {
		this.raspberryPISocketChannels.put(lockID, socketChannel);
	}

	public void setRequestIDMap(HashMap<String, Integer> requestIDMap) {
		this.requestIDMap = requestIDMap;
	}
	
	public void setRequestIDMap(String lockID, int requestID) {
		this.requestIDMap.put(lockID, requestID);
	}
}