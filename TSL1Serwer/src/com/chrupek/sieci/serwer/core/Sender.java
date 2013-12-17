package com.chrupek.sieci.serwer.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class Sender implements Runnable
{
	/**
	 * Queue of messages to be send
	 */
	private LinkedList<String> toSend;
	/**
	 * Send output stream 
	 */
    private	PrintWriter out;
	/**
	 * Standard input from keyboard
	 */
    private	BufferedReader stdIn;
	/**
	 * Senders loop condition 
	 */
	private boolean running=false;
	/**
	 * Use this.notify(). Synchronization required to have the monitor thread
	 * 
	 */
	public synchronized void wakeUp()
	{
		this.notify();
	}
	/**
	 * Sender thread
	 * @param socket client
	 * @param toSend queue of statements to send
	 */
	Sender(Socket socket, LinkedList<String> toSend)
	{
		try
		{
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			this.toSend = toSend;
			out = new PrintWriter(socket.getOutputStream(), true);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * Method terminating sender thread
	 */
	synchronized void exit()
	{
		running=false;
		this.wakeUp();
		System.out.println("Exit from Sender using exit()");
	}
	/**
	 * method of sender thread
	 */
   	public void run()
    	{
        	String temp;
        	System.out.println("Ready to sending");
		running=true;
        	try 
		{
        		while(running)
        		{
            			synchronized(this)
				{
				while(!toSend.isEmpty())
				{
					temp = toSend.poll();
            		out.println(temp);
            		System.out.println("Server: Send: "+temp);
					if(temp.equals("exit"))
					{
						running=false;
						break;
					}
				}
				if(running)
					this.wait();
				}
        		}

       		} 
		catch (Exception e) {e.printStackTrace();}
		out.close();
		System.out.println("End of sender");
    	}	
}
