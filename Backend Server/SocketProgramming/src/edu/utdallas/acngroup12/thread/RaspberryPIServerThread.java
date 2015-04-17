package edu.utdallas.acngroup12.thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import edu.utdallas.acngroup12.datastructure.Message;

public class RaspberryPIServerThread extends Thread {

	private int port;
	private int MAX_BUF_SIZE=1000;
	private Message message;
	private String hostServer;
	private SocketChannel clientChannel;
	private ByteBuffer byteBuffer;
	private String lockID;
	private String identity;
	
	public RaspberryPIServerThread(String hostServer, int port, String lockID, double longitude, double latitude) {
		// TODO Auto-generated constructor stub
		this.hostServer = hostServer;
		this.port = port;
		this.clientChannel = null;
		this.byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
		this.lockID = lockID;
		this.identity = "RaspberryPI";
		this.message = new Message(this.lockID, 0, this.identity, null, null);
		this.message.setLongitude(longitude);
		this.message.setLatitude(latitude);		
	}
	
	public void startCommunicating(){
		while(true){
				try {
					//initial message to web server
					this.message.setRequestID(0);
					this.message.setSender(this.identity);
					this.message.setSoftLockStatus(null);
					this.message.setHardLockStatus("LOCKED");
					
					this.clientChannel = SocketChannel.open();
					this.clientChannel.connect(new InetSocketAddress(hostServer, port));
					this.clientChannel.socket().setSoTimeout(15);
					//Server is up and running..!
					System.out.println("Connected to Server : " +this.clientChannel.getRemoteAddress());
					System.out.println("Sending Initial Request \n" +this.message.toString());
					byteBuffer.clear();
					byteBuffer.put(Message.serialize(this.message));
					byteBuffer.flip();
					//Write the data to channel
					while(byteBuffer.hasRemaining()) {
						this.clientChannel.write(byteBuffer);
					}
					while(true){
						this.clientChannel.configureBlocking(true);
						byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
						byteBuffer.rewind();
						byteBuffer.clear();
						this.clientChannel.read(byteBuffer);
						Message recievedMessage = null;
						try {
							recievedMessage = (Message)Message.deserialize(byteBuffer);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("Recieved connection from : " +recievedMessage.getSender());
						//System.out.println("LockID : " +recievedMessage.getLockID()+ " Current Lock Status : " +recievedMessage.getSoftLockStatus());
						System.out.println(recievedMessage.toString());
						byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
						byteBuffer.rewind();
						byteBuffer.clear();
						if(recievedMessage.getRequestID() > 0){
							recievedMessage.setSender("RaspberryPI");
							recievedMessage.setHardLockStatus(recievedMessage.getSoftLockStatus());
							System.out.println("Sending Response \n" +recievedMessage.toString());
							this.byteBuffer.put(Message.serialize(recievedMessage));
							this.byteBuffer.flip();
							while(byteBuffer.hasRemaining()) {
								this.clientChannel.write(byteBuffer);
							}						
						}
					}
				} catch (IOException ioe) {
					// TODO Auto-generated catch block
					//ioe.printStackTrace();
					//If this exception occurs then server is down try after 5 seconds of sleep
					try {
						System.out.println("Trying to connect to server in 5 seconds..");
						Thread.sleep(5000);
					} catch (InterruptedException ie) {
						// TODO Auto-generated catch block
						ie.printStackTrace();
					}
				}
		}
	}
	
	public void run(){
		System.out.println("Starting client!!");
		this.startCommunicating();		
	}
	
	public static void main(String[] args){
		//java SocketPI localhost 1234 LOCK_3 LOCKED
		//Thread piClient = new RaspberryPIServerThread("acngroup12.utdallas.edu", 2100, "LOCK_3",-96.7506256,32.9862165);
		Thread piClient = new RaspberryPIServerThread("acngroup12.utdallas.edu", 2100, "LOCK_3",-96.837209,32.817840);
		piClient.start();
	}
}
