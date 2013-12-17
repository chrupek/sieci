package com.chrupek.sieci.serwer.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

public class Reciver implements Runnable
{
	/**
	 * Reads data from client
	 */
	private BufferedReader in;
	/**
	 * Is responsible for communication with client
	 *  
	 */
	private Socket socket;
	/**
	 * Do not know what will be used for
	 */
	private String message="";
	/**
	 * Queue of received messages
	 */
	LinkedList<String> received;
	/**
	 * Use this.notify(). Synchronization required to have the monitor thread
	 * 
	 */
	public synchronized void wakeUp()
	{
		this.notify();
	}
	
	/**
	 * Constructor of Receiver class
	 * @param soc network socket
	 * @param received queue of received messages
	 */
	Reciver(Socket soc, LinkedList<String> received)
	{
		socket= soc;
		this.received=received;
		try 
		{
			in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
		} 
		catch (Exception e) {e.printStackTrace();}
	}
	/**
	 * thread method
	 */
	public void run()
	{
		String temp;
		System.out.println("Ready to recive from " + socket.getInetAddress().getHostAddress());

		try 
		{
			while(true)
			{
				if((temp=in.readLine())!=null)
				{
					System.out.println("Server: Got: "+temp);
					received.add(temp);
				}
				else
				{
					System.out.println("Receiver ended becouse of connection is broken");
					break;
				}
				if(temp.equals("exit"))
				{
					System.out.println("Exit from reciver after \'exit\'");
					break;
				}
			}
			socket.close();
		}
		catch (IOException ex) { System.out.println("Connection terminated by client");}
		catch (Exception e) {e.printStackTrace();}

		System.out.println("End of receiver");

	}
}