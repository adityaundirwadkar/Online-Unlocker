import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.net.*;

import com.socketprogramming.datastructure.Message;

public class SocketPI extends Thread {

	private int port;
	private int MAX_BUF_SIZE=1000;
	private Message message;
	private String hostServer;
	private SocketChannel hostChannel;
	private ByteBuffer byteBuffer;
	private String lockID;
	
	public SocketPI(String hostServer, int port, String lockID) {
		// TODO Auto-generated constructor stub
		this.hostServer = hostServer;
		this.port = port;
		this.hostChannel = null;
		this.byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
		this.lockID = lockID;
		this.message = new Message(this.lockID, -1, "RaspberryPI", null, null);
	}
	
	public void startCommunicating(){
		try{
			//Prepare for initial request!
			this.message = new Message(this.lockID, 0, "RaspberryPI", null, "LOCKED");
			
			this.hostChannel = SocketChannel.open();
			this.hostChannel.connect(new InetSocketAddress(hostServer, port));
			this.hostChannel.socket().setSoTimeout(15);
			System.out.println("Connected to Server : " +hostChannel.getRemoteAddress());
			//Process message object and put it in bytebuffer
			//Message newmessage = new Message(this.lockID, "RaspberryPI", "Locked", "Locked");
			
			System.out.println("Sending Initial Request \n" +this.message.toString());
			
			//ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
			byteBuffer.clear();
			byteBuffer.put(Message.serialize(this.message));
			byteBuffer.flip();
			//Write the data to channel
			while(byteBuffer.hasRemaining()) {
				this.hostChannel.write(byteBuffer);
			}
			//this.hostChannel.close();
			while(true){
				this.hostChannel.configureBlocking(true);
				byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
				byteBuffer.rewind();
				byteBuffer.clear();
				//Create a new thread by reading incoming data.
				this.hostChannel.read(byteBuffer);
				Message recievedMessage  = (Message)Message.deserialize(byteBuffer);
				System.out.println("Recieved connection from : " +recievedMessage.getSender());
				//System.out.println("LockID : " +recievedMessage.getLockID()+ " Current Lock Status : " +recievedMessage.getSoftLockStatus());
				System.out.println(recievedMessage.toString());
				//Change the status at hardware level!
				
				//Send response!!
				byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
				byteBuffer.rewind();
				byteBuffer.clear();
				if(recievedMessage.getRequestID() > 0){
					try {
						recievedMessage.setSender("RaspberryPI");
						recievedMessage.setHardLockStatus(recievedMessage.getSoftLockStatus());
						System.out.println("Sending Response \n" +recievedMessage.toString());
						
						
						this.byteBuffer.put(Message.serialize(recievedMessage));
						this.byteBuffer.flip();
						while(byteBuffer.hasRemaining()) {
							this.hostChannel.write(byteBuffer);
						}
						//End processing
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			}		
		}
		catch(ConnectException ce){
			System.out.println("Unable to connect to server!");
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to cast incoming object to Message.!");
			e.printStackTrace();
		}

	}
	
	public void run(){
		System.out.println("Starting client!!");
		this.startCommunicating();		
	}
	
	public static void main(String[] args){
		//java SocketPI localhost 1234 LOCK_3 LOCKED
		Thread socketClient = new SocketPI(args[0], Integer.parseInt(args[1]), args[2], args[3]);
		socketClient.start();
	}
}