package com.chrupek.sieci.serwer.core;

import java.net.Socket;
import java.util.*;

public class Connection extends Thread
{
	/**
	 *Responsible for communication with client
	*/
	Socket client;
	/**
	 * Clients name
	 */
	public String playerName;
	/**
	 * Clients symbol
	 */
	public String playerSymbol;
	/**
	 * Clients number
	 */
	public int playerID;
	/**
	 * Sender thread
	 */
	private Sender sender;
	/**
	 * Reciver thread
	 */
	private Reciver receiver;
	/**
	 * Wakes up sender
	 */
	public void notifySender()
	{
			sender.wakeUp();
	}
	/**
	 * Adds a messsage to toSend and wakes up sender
	 * @param message String you want to send
	 * @throws InterruptedException 
	 */
	public void send(String message)
	{
		this.toSend.add(message);
		this.notifySender();
		
	}
	
	/**
	 * Wakes up reciver and gets message.
	 */
	public void notifyReciver() {
		receiver.wakeUp();
	}
	
	/**
	 * Returns message from received and wakes up receiver
	 * @return message from client
	 * @throws InterruptedException 
	 */
	/*public String getMessage()
	{	
		String message = null;
		this.notifyReciver();
		if(!received.isEmpty()){
			message = received.getLast();
			return message;
		}
		else return null;

	}*/
	/**
	 * List of messages to send
	 */
	public LinkedList<String> toSend;
	/**
	 * List of received messages
	 */
	public LinkedList<String> received;
	/**
	 * Constructor of Connection class
	 *
	 * @param socket Socket used for communication with client
	 * @param name Clients name
	 */
	public Connection(Socket socket, String name, int id)
	{
		try
		{
			this.playerName = name;
			this.playerID = id;
			toSend = new LinkedList<String>(  Collections.synchronizedList( new LinkedList<String>() ) );
			received = new LinkedList<String>(  Collections.synchronizedList( new LinkedList<String>() ) );
			client = socket;
			receiver = new Reciver(client,received);
			sender = new Sender(client, toSend);
			new Thread(receiver, "Reciver " + playerName).start();
			new Thread(sender, "Sender " + playerName).start();
		}
		catch(Exception e) {e.printStackTrace();}
	}
	/**
	 * It ends the sender thread
	 */
	public void endSender()
	{
		sender.exit();
	}
}
