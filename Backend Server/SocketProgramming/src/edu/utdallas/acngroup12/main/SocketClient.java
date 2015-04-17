package edu.utdallas.acngroup12.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import edu.utdallas.acngroup12.datastructure.Message;

public class SocketClient extends Thread {

	private int port;
	private int MAX_BUF_SIZE=1000;
	private Message message;
	private String hostServer;
	private SocketChannel hostChannel;
	
	public SocketClient(String hostServer, int port) {
		// TODO Auto-generated constructor stub
		this.hostServer = hostServer;
		this.port = port;
		this.hostChannel = null;
	}
	
	public void startCommunicating(){
		try{
			this.hostChannel = SocketChannel.open();
			this.hostChannel.connect(new InetSocketAddress(hostServer, port));
			System.out.println("Connected to Server : " +hostChannel.getRemoteAddress());
			//Process message object and put it in bytebuffer
			message = new Message("lock_2",0, "RaspberryPI", "Locked", "Locked");
			
			ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_BUF_SIZE);
			byteBuffer.clear();
			byteBuffer.put(Message.serialize(message));
			byteBuffer.flip();
			//Write the data to channel
			while(byteBuffer.hasRemaining()) {
				this.hostChannel.write(byteBuffer);
			}
			this.hostChannel.close();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public void run(){
		System.out.println("Starting client!!");
		this.startCommunicating();		
	}
	
	public static void main(String[] args){
		//java SocketClient localhost 1234
		Thread socketClient = new SocketClient(args[0], Integer.parseInt(args[1]));
		socketClient.start();
	}
}
