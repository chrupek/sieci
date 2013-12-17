package com.chrupek.sieci.serwer.core;

import java.util.ArrayList;

/**
 * Class implements game between to players.
 * This class manages the game in the run() method.
 */
public class Game implements Runnable
{
	/**
	 * Error message
	 */
	private final String CORDINATE_ALREADY_USED = "0-10";
	private final String INVALID_COORDINATES = "012";
	/**
	 * Other messages
	 */
	private final String OK = "010";
	private final String YOUR_TURN = "001";
	private final String WAITING_FOR = "011";
	private final String EXIT = "-1";
	private final String NEW_PLAYER = "099";
	private final String COORIDANTES_TO_OTHER_PLAYERS = "022";
	
	private final String[] symbols = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	private int symbolIndex = 0;
	/**
	 * Board size and winning symbol number
	 */
	private int size, symNum;
	/**
	 * Row and column;
	 */
	private String row = null, column = null;
	/**
	 * Connection with player 1
	 */
	private Connection player1;
	
	/**
	 * Connection with player 2 
	 */
	private Connection player2;
	
	/**
	 * coordinates on board that already had been used
	 */
	private ArrayList<String> used_cords = new ArrayList<String>();
	
	private String s1, s2 = "";
	
	/** 
	 * list of connected players
	 */
	private ArrayList<Connection> players;


	public Game(Connection player1, Connection player2)
	{
		this.player1 = player1;
		if(player1.playerName == null)
			this.player1.playerName = "Gracz 1";
		this.player2 = player2;
		if(player2.playerName == null)
			this.player2.playerName = "Gracz 2";
		players = new ArrayList<Connection>();
		players.add(player1);
		players.add(player2);
	}

	int i = 2;
	/**
	 * Equals <code>true</code> only when there was change on the stack,
	 * manages it method {@link Game#run run}
	 */
	boolean stackChanged = false;
	/**
	 * Equals <code>true</code> when first stack has been initialized,
	 * before that it is <code>false</code>
	 */
	boolean init1 = false;
	/**
	 * Equals <code>true</code> when second stack has been initialized,
	 * before that it is <code>false</code>
	 */
	boolean init2 = false;
	
	/**
	 * Main method that performs the
	 * <ul>
	 * <li> reading communicates from players
	 * <li> processing messages
	 * <li> game initialization
	 * <li> end of the game
	 * <li> checking if stack has not changed
	 * <li> sending messages to players about change of stack state, victory of one of the players,
	 * end of game
	 * </ul>
	 *
	 */
	public void run()
	{
		sendRulesToPlayers();
		Connection activePlayer = players.get(0);
		while(true)
		{
			row = null;
			column = null;
			
			row = activePlayer.received.pollLast();
			System.out.println("%%%%%"+row);
			column = activePlayer.received.pollLast();
			System.out.println("%%%%%"+column);
		
			activePlayer.send(YOUR_TURN);
	
			sendMessageToPlayers(WAITING_FOR+";"+activePlayer.playerID, activePlayer);
			if(row!=null && column != null){ 
				if(!row.matches("[0-9]+") || !column.matches("[0-9]+"))
					activePlayer.send(INVALID_COORDINATES);
			
				if(used_cords.contains(row+";"+column))
					activePlayer.send(CORDINATE_ALREADY_USED);
				else{
					activePlayer.send(OK); 
					used_cords.add(row+";"+column);
					sendMessageToPlayers(COORIDANTES_TO_OTHER_PLAYERS+";"+activePlayer.playerID+";"+row+";"+column, activePlayer);
				}
			}
			if(EXIT.equalsIgnoreCase(activePlayer.received.pollLast())){
				players.remove(activePlayer);
				sendMessageToPlayers(EXIT+";"+activePlayer.playerID, null);
				activePlayer.endSender();
				
			}
			if(row!=null && column!=null)
				activePlayer = nextPlayer(activePlayer);
		}
	}
	private Connection nextPlayer(Connection oldPlayer) {
		int i = players.indexOf(oldPlayer);
		if(i==players.size()-1)
			i=0;
		else ++i;	
		return players.get(i);
	}
	/**
	 * Sends game rules to first two connected players and info about each other.
	 */
	private void sendRulesToPlayers() {
		for(Connection player: players){
			player.send("#"+Integer.toString(size));			
			player.send("#"+Integer.toString(symNum));
			player.send("#"+Integer.toString(player.playerID));
			player.send("#"+symbols[symbolIndex]);
			player.playerSymbol = symbols[symbolIndex];
			++symbolIndex;
			sendMessageToPlayers(NEW_PLAYER+";"+player.playerName+";"+player.playerID+";"+player.playerSymbol, player);
		}
	}

	/**
	 * 
	 * @return Returns list of connected players
	 */
	public ArrayList<Connection> getPlayers() {
		return players;
	}
	
	/**
	 * Sends message to all players except player 
	 * @param message to be send
	 * @param p active player
	 */
	public void sendMessageToPlayers(String message, Connection p){
		for(Connection player: players){
			if(player==p)
				continue;
			player.send(message);
		}
	}

	/**
	 * Adds another player
	 * @param player instance of Connection
	 */
	public void addPlayer(Connection player){
		if(player.playerName == null)
			player.playerName = "Gracz "+player.playerID;
		players.add(player);
		sendMessageToPlayers(NEW_PLAYER+";"+player.playerName+";"+player.playerID+";"+player.playerSymbol, player);
	}

	public void setRules(int size, int symNum) {
		this.size = size;
		this.symNum = symNum;
	}
	public String getNewSymbol() {
		String s = symbols[symbolIndex];
		++symbolIndex;
		return s;
	}
}

