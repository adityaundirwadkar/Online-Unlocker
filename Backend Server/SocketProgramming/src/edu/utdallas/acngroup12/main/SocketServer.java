/**
 * 
 */
package edu.utdallas.acngroup12.main;

/**
 * @author Aditya
 *
 */

import java.io.IOException;

import edu.utdallas.acngroup12.thread.*;

public class SocketServer {

	/**
	 * 
	 */
	private MainServerThread newServerThread;
	
	public SocketServer(String serverName, int port) {
		// TODO Auto-generated constructor stub
		try {
			newServerThread = new MainServerThread(serverName,port);
			newServerThread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//run as java SocketServer localhost 1234
		@SuppressWarnings("unused")
		SocketServer newSocketServer = new SocketServer(args[0],Integer.parseInt(args[1]));
		System.out.println("Exiting SocketServer!!");
	}	
}