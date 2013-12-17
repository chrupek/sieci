package com.chrupek.sieci.serwer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chrupek.sieci.serwer.core.Connection;
import com.chrupek.sieci.serwer.core.Coordinates;
import com.chrupek.sieci.serwer.core.ServerBoard;
import com.chrupek.sieci.serwer.core.PlayerData;

public class Server{
	
	
	/**
	 * Error message
	 */
	private final String CORDINATE_ALREADY_USED = "0-10";
	/**
	 * Error message
	 */
	private final String INVALID_COORDINATES = "012";

	private final String EXIT = "-1";
	private final String YOUR_TURN = "001";
	private final String OK = "010";
	private final String WAITING_FOR = "011";
	private final String PLAYER_NAME_PREFIX = "013";
	private final String COORIDANTES_TO_OTHER_PLAYERS = "022";
	private final String DELETE_THIS_SYMBOLS = "023";
	private final String SCORE = "024";
	private final String NEW_PLAYER = "099";
	
	
	private final String[] symbols =
		{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R",
		"S","T","U","V","W","X","Y","Z"};
		
	private int port;
	private int size;
	private int symNum;
	private int playerID=0;
	private int symbolIndex=0;
	private int currentPlayerIndex=-1;
	private ServerBoard board;
	private ServerSocket serverSocket;
	
	/**
	 *  Map that holds list of players (both active and leavers), key: player ID
	 */
	private HashMap<Integer, PlayerData> playerList; 
	private List<Integer> leaversIds;
	
	/**
	 * List of connected players/clients
	 */
	private LinkedList<Connection> clients = new LinkedList<Connection>();
	
	/**
	 * coordinates on board that already had been used
	 */
	private List<String> usedCoords = new LinkedList<String>();
	
	public Server(int listenigPort, int boardSize, int winnigSymbolNum){
		port = listenigPort;
		size = boardSize;
		symNum = winnigSymbolNum;
		board = new ServerBoard(size);
		playerList = new HashMap<Integer, PlayerData>();
		leaversIds = new ArrayList<Integer>();
		init();
	}
	
	/**
	 * Initializes socket and game
	 */
	public void init(){
		String incoming = null;
		int currentIndex = 0;
		BufferedReader input;
		Socket connectionSocket;
		boolean rulesSend = false;
		try{
			serverSocket = new ServerSocket(port);
		
			System.out.println("Server is running at port " +port);
			
			while(true){
				try{
					if(serverSocket.isClosed()){
						serverSocket = new ServerSocket(port);
						connectionSocket = serverSocket.accept();
						input = new BufferedReader(new InputStreamReader(connectionSocket.
							   getInputStream()));
					}
					
					//are listening to
					connectionSocket = serverSocket.accept();
					
					
					//Read the request from the client from the socket interface
					//into the buffer.
					input = new BufferedReader(new InputStreamReader(connectionSocket.
							getInputStream()));
					
					/* 
					 * magic number
					 * 26 is the number of letters in Latin alphabet 
					 */
					if(clients.size()>=26){
						PrintStream os = new PrintStream(connectionSocket.getOutputStream());
			            os.println(EXIT);
			            os.close();
			            System.out.println("Closing socket");
			            connectionSocket.close();
					}

					incoming = input.readLine();

					if(incoming.matches(PLAYER_NAME_PREFIX+".+")){
						incoming = incoming.substring(incoming.indexOf(";")+1);
						Connection c = new Connection(connectionSocket, incoming, ++playerID);
						c.playerSymbol = symbols[symbolIndex];
						++symbolIndex;
						PlayerData p = new PlayerData(c.playerName, c.playerSymbol, c.playerID);
						playerList.put(c.playerID, p);
						clients.add(c);
						incoming=null;
					}
					
					if(clients.size()>=2){
						if(!rulesSend){
							sendRulesToPlayers();
							rulesSend = true;
						}
						playTheGame(nextPlayer());
					}
					
					if(clients.size()>2){
						// add new player and send him game rules
						String tempSymbol=getNewSymbol();
						Connection player = clients.get(currentIndex);
					
						clients.get(currentIndex).playerSymbol = tempSymbol;
						
						addPlayer(clients.get(currentIndex));
						player.send("#"+Integer.toString(size));
						player.send("#"+Integer.toString(symNum));
						player.send("#"+Integer.toString(player.playerID));
						player.send("#"+tempSymbol);
						sendMessageToPlayers(NEW_PLAYER+";"+player.playerName+";"+player.playerID+";"+player.playerSymbol, player);
					}
					currentIndex++;
				}catch(Exception e){
					System.out.println("Error: EXCEPTION");
					System.out.println("Error: CAUSE: "+e.getCause());
					e.printStackTrace();
				}
			}
		
		}catch(IOException e){
			System.out.println("ERROR: \n"+e.getMessage());
		}
	}
	
	/**
	 * Handles communication between player, server and other players
	 * @param player
	 */
	private synchronized void playTheGame(Connection player) {
		String row = null;
		String column = null;
		String got=null;
		Connection activePlayer = player;
		PlayerData p;
		
		while (true){
			synchronized(activePlayer){
				activePlayer.send(YOUR_TURN);
				
				sendMessageToPlayers(WAITING_FOR+";"+activePlayer.playerID, activePlayer);
				while(got==null){
				
					if(leaversIds.contains(activePlayer.playerID)){
						p = playerList.get(activePlayer.playerID);
						Coordinates c = p.getRandomCoordinates();
						board.putSymbol(null, c.x, c.y);
						sendMessageToPlayers(DELETE_THIS_SYMBOLS+";"+c.x+"%"+c.y+";", activePlayer);
						
						// there are no symbols of this player to remove from the board
						if(p.areUsedCoordsEmpty()){
							playerList.remove(activePlayer.playerID);
							leaversIds.remove(activePlayer.playerID);
							p=null;
						}
						activePlayer = nextPlayer();
					}
					
					got = activePlayer.received.pollLast();
					
					/* MAGIC DO NOT TOUCH! */
					//System.out.println("%%"+got);
					

					
					if(got != null){
						int semiColonIndex = got.indexOf(";");
						row = got.substring(0, semiColonIndex);
						column = got.substring(semiColonIndex+1);
						
						if(!row.matches("[0-9]+") || !column.matches("[0-9]+")){
							activePlayer.send(INVALID_COORDINATES);
							got = null;
						}
					
						if(usedCoords.contains(row+";"+column)){
							activePlayer.send(CORDINATE_ALREADY_USED);
							got = null;
							}
						else{
							activePlayer.send(OK);
							sendMessageToPlayers(COORIDANTES_TO_OTHER_PLAYERS+";"+activePlayer.playerID+";"+row+";"+column, activePlayer);
							usedCoords.add(row+";"+column);
							board.putSymbol(activePlayer.playerSymbol, Integer.parseInt(row), Integer.parseInt(column));
							if(board.checkForScore(Integer.parseInt(row), Integer.parseInt(column), symNum, activePlayer.playerSymbol)){
								activePlayer.send(DELETE_THIS_SYMBOLS+";"+board.getListOfCoordsToDelete());
								activePlayer.send(SCORE);
								sendMessageToPlayers(DELETE_THIS_SYMBOLS+";"+board.getListOfCoordsToDelete(), activePlayer);
								sendMessageToPlayers(SCORE+";"+activePlayer.playerID, activePlayer);
								playerList.get(activePlayer.playerID).addPoint();
								removeFromUsedCoords(board.getListOfCoordsToDelete());
								board.resetListOfCoordsToDelete();
								
							}
							
						}
					
						if(EXIT.equalsIgnoreCase(got)){
							sendMessageToPlayers(EXIT+";"+activePlayer.playerID, null);
							leaversIds.add(activePlayer.playerID);
							activePlayer.endSender();
							
						}
					}
					
				}
				got = null;
				activePlayer = null;
				activePlayer = nextPlayer();
			}
		}
		
	}
	
	private void removeFromUsedCoords(String listOfCoordinates){
        Pattern pat = Pattern.compile("([0-9]+)%([0-9]+)");
        Matcher match = pat.matcher(listOfCoordinates);
        int lastMatchIndex=0;
        while(match.find(lastMatchIndex)){
            String row = match.group(1);
            String column = match.group(2);
            board.putSymbol(null, Integer.parseInt(row), Integer.parseInt(column));
            usedCoords.remove(row+";"+column);
            lastMatchIndex = match.end();
        }
	}

	/**
	 * Method returns next player from
	 * connected players.
	 * @return Connection
	 */
	private Connection nextPlayer()
	{
		if(currentPlayerIndex==clients.size()-1)
			currentPlayerIndex=0;
		else ++currentPlayerIndex;	
		return clients.get(currentPlayerIndex);
	}
		
	
	/**
	 * Adds another player
	 * @param player instance of Connection
	 */
	public void addPlayer(Connection player){
		if(player.playerName == null)
			player.playerName = "Gracz "+player.playerID;
		sendMessageToPlayers(NEW_PLAYER+";"+player.playerName+";"+player.playerID+";"+player.playerSymbol, player);
	}

	/**
	 * Sends message to all players except player 
	 * @param message to be send
	 * @param p active player
	 */
	public synchronized void sendMessageToPlayers(String message, Connection p){
		for(Connection player: clients){
			if(player.playerID != p.playerID){
				player.send(message);
			}
		}
	}

	public String getNewSymbol() {
		String s = symbols[symbolIndex];
		++symbolIndex;
		return s;
	}
	
	/**
	 * Sends game rules to first two connected players and info about each other.
	 */
	private void sendRulesToPlayers() {
		Connection player= clients.get(0);
		
		System.out.println("PLAYER ID "+ player.playerID);
		player.send("#"+Integer.toString(size));			
		player.send("#"+Integer.toString(symNum));
		player.send("#"+Integer.toString(player.playerID));
		player.send("#"+player.playerSymbol);

		
		player= clients.get(1);
		
		System.out.println("PLAYER ID "+ player.playerID);
		player.send("#"+Integer.toString(size));			
		player.send("#"+Integer.toString(symNum));
		player.send("#"+Integer.toString(player.playerID));
		player.send("#"+player.playerSymbol);
		
		clients.get(0).send(NEW_PLAYER+";"+clients.get(1).playerName+";"+clients.get(1).playerID+";"+clients.get(1).playerSymbol);
		player.send(NEW_PLAYER+";"+clients.get(0).playerName+";"+clients.get(0).playerID+";"+clients.get(0).playerSymbol);
		player=null;
		//sendMessageToPlayers(NEW_PLAYER+";"+player.playerName+";"+player.playerID+";"+player.playerSymbol, player);
	}
	
}
