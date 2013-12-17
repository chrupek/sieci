package com.chrupek.sieci.client.core;

import com.chrupek.sieci.client.view.ClientWindow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chrupek.sieci.client.model.Board;
import com.chrupek.sieci.client.model.Player;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class Client{
	
	/**
	 * Error message
	 */
	private final String CORDINATE_ALREADY_USED = "0-10";
    /**
	 * Error message
	 */
	private final String INVALID_COORDINATES = "012";
	
    /*
	 * Other messages
	 */
	private final String EXIT = "-1";
	private final String YOUR_TURN = "001";
	private final String OK = "010";
	private final String WAITING_FOR = "011";
	private final String PLAYER_NAME_PREFIX = "013";
	private final String OTHER_PLAYER_MOVE = "022";
	private final String DELETE_THIS_SYMBOLS = "023";
	private final String SCORE = "024";
	private final String NEW_PLAYER = "099";

	private String IP;
	private String port;
    private String playerName;
	
    /**
     * Filed stores info about player.
     */
	public Player me;
    /**
     * Filed stores info about 
     * other players.
     */
	private HashMap<String, Player> others;
	
	private Socket socket;
	private PrintWriter out; 
	private BufferedReader in;
	private Board board;
    
    /**
     * Flag is set to <b>true</b> if
     * connection was made
     */
    private boolean connected = false;
    private boolean closingProgram=false;

	private int boardSize=0;
	private int winningSymNr=0;
	private int selectedRow=-1;
	private int selectedColumn=-1;
	
	/**
	 * It is no longer need becouse input is no
	 * longer read from keyboard
	 */
	private BufferedReader stdin;
    
    private static ClientWindow window;
	
	/** Use this constructor. It initializes
	 * Socket and InetAddress classes
	 * 
	 * @param IP
	 * @param port
	 */
	public Client(String IP, String port, String playerName){
		this.IP = IP;
		this.port = port;
        this.playerName = playerName;
 		me = new Player();
		me.setName(playerName);
		others = new HashMap<String, Player>();
        init();
	}
   
	
	/**
	 * If you insist on using
	 * this constructor, don't forget
	 * to set server IP, server port
	 * and player name 
	 */
	public Client(){
		this.IP=null;
		this.port=null;
		this.me=new Player();
		this.others = new HashMap<String, Player>();
		this.socket = null;
		this.in = null;
		this.out = null;
	}
    
    public void init(){
        stdin = new BufferedReader(new InputStreamReader(System.in));
		try {
			//this.addr = InetAddress.getByName(IP);
			int serverPort = Integer.parseInt(port);
			this.socket = new Socket(IP, serverPort);
			this.out= new PrintWriter(socket.getOutputStream(), true);
	        this.in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            if(socket.isConnected()){
                connected=true;
                window.appendTextToLog("Conected to server.\n");
            }
		} catch (NumberFormatException | IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
    }
	
	/**
	 * Handling the beginning of game.
	 * <ul>
	 * <li> Sending player name
	 * <li> Receiving rules
	 * <li> Preparing board 
	 * </ul>
	 * @throws IOException 
	 */
	public void getRules(){
		try {
            String messageFromServer = null;
            out.println(PLAYER_NAME_PREFIX+";"+me.getName());
            while(me.getSymbol() == null || boardSize == 0 || winningSymNr == 0){     
                messageFromServer = getMessageFromServer();
                if(messageFromServer != null)
                proccesMessage(messageFromServer);  
            }
            board = new Board(boardSize);
            window.drawBoard(board.drawBoard());
            messageFromServer = null;
            while(!YOUR_TURN.equals(messageFromServer)|| !WAITING_FOR.regionMatches(true, 0, messageFromServer, 0, 3)){
                    messageFromServer = getMessageFromServer();
                    if(messageFromServer!=null)
                        playTheGame(messageFromServer);
            }
        } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
	/**
     * Method is processing messeges with rules of the game
     * such as borad size, number of symbols to get a point
     * and player symbol that will represent his moves.
     * @param message 
     */
	public void proccesMessage(String message) {
		String s;
		if(message.matches("#[0-9]+")){
			if(boardSize==0){
				s = message.substring(1);
				boardSize = Integer.parseInt(s);
			}
			else if(winningSymNr==0){
				s = message.substring(1);
				this.winningSymNr = Integer.parseInt(s);
				 window.appendTextToLog("Number of symbols for score are: "+winningSymNr+"\n");
			}
			else{
				s = message.substring(1);
				me.setId(Integer.parseInt(s));
			}
		}
		else if(message.matches("#[A-Z]")){
			me.setSymbol(message.substring(1));
			window.appendTextToLog("Your symbol is: "+me.getSymbol()+"\n");
        }

	}
    /**
     * Method is parsing message from server ,other than rules,
     * that looks like HEADER(;OTHERINFO)+, text in brackets
     * is optional, then sends appropriate response to window
     * and waits for user input.
     * @param message 
     */
	public void playTheGame(String message) throws IOException{
 		String prefix = null;
		int index = message.indexOf(";");
        synchronized(window){
            if(index == -1){
                processShortMessage(message);
            }	
            else{
                prefix = message.substring(0, index);
                message = message.substring(index+1);
                processLongMessage(prefix, message);
            }
       }
    }
    
    private void processShortMessage(String text){
        switch(text){
				case OK:
					board.putSymbol(me.getSymbol(), selectedRow, selectedColumn);
                    selectedColumn = -1;
                    selectedRow = -1;
					window.drawBoard(board.drawBoard());
					break;
					
				case YOUR_TURN:
                    window.setOkButtonEnabled(true);
                    window.appendTextToLog("Your turn.\nAwaiting for row and column number. \n");				
					break;
					
				case INVALID_COORDINATES:
                    selectedColumn = -1;
                    selectedRow = -1;
					window.appendTextToLog("Ivalid coordinates!\nPlease insert valid ones.\n");
					break;
			
				case CORDINATE_ALREADY_USED:                    
					window.appendTextToLog("Coordinates already used!\nPlease insert new ones.\n");
					break;
					
				case SCORE:
					me.addPoint();
					window.appendTextToLog("Congratulations!\nYou got a point.\n");
					window.appendTextToLog("Your score is: "+me.getScore()+"\n");
                    break;
                    
				case EXIT:
					window.appendTextToLog("Server too busy. Try again later\n");
					break;
                    
				default:
					break;
			}
    }
    
    private void processLongMessage(String prefix, String message){
        int index=0;
        String temp;
        switch(prefix){
            case NEW_PLAYER:
                // processing message: playerName;playerID;playerSymbol

                Player newGuy = new Player();

                temp = message;//.substring(index+1);
                index = message.indexOf(";");
                newGuy.setName(temp.substring(0, index));

                temp = temp.substring(index+1);
                index = temp.indexOf(";");
                newGuy.setId(Integer.parseInt(temp.substring(0, index)));

                temp = temp.substring(index+1);
                newGuy.setSymbol(temp);

                others.put(Integer.toString(newGuy.getId()), newGuy);
                break;

            case WAITING_FOR:
                // processing message: playerID
                window.setOkButtonEnabled(false);
                temp = message.substring(index);
                if(others.containsKey(temp))
                    window.appendTextToLog("Wating for player: "+
                    others.get(temp).getName()+"\n");
                else window.appendTextToLog("Wating for other player\n");
                break;

            case EXIT:
                // processing message: playerID
                temp = message;//.substring(index);
                if(others.containsKey(temp)){
                    Player leaver = others.get(temp);
                    window.appendTextToLog("Player "+
                    leaver.getName()+" has leave the game\n");
                    others.remove(temp);
                }
                break;

            case OTHER_PLAYER_MOVE:
                // processing message: playerID;row;column

                Player p;

                temp = message;//.substring(index);
                index = temp.indexOf(";");
                p = others.get(temp.substring(0, index));

                temp = temp.substring(index+1);
                index = temp.indexOf(";");
                selectedRow = Integer.parseInt(temp.substring(0, index));

                temp = temp.substring(index+1);
                selectedColumn = Integer.parseInt(temp.substring(0));
                board.putSymbol(p.getSymbol(), selectedRow, selectedColumn);
                window.drawBoard(board.drawBoard());
                break;

            case SCORE:
                // processing message: playerID

                temp = message;
                if(others.containsKey(temp)){
                    others.get(temp).addPoint();
                    window.appendTextToLog("Player: "+others.get(temp).getName()+
                    " got a point.\nHis score is:" +others.get(temp).getScore()+"\n");
                }
                break;

            case DELETE_THIS_SYMBOLS:
                // processing message: (rowNm%ColNm;)+

                temp = message;//.substring(index);
                Pattern pat = Pattern.compile("([0-9]+)%([0-9]+)");
                Matcher match = pat.matcher(temp);
                int lastMatchIndex=0;
                while(match.find(lastMatchIndex)){
                    selectedRow = Integer.parseInt(match.group(1));
                    selectedColumn = Integer.parseInt(match.group(2));
                    board.putSymbol(" ", selectedRow, selectedColumn);
                    lastMatchIndex = match.end();
                }
                window.drawBoard(board.drawBoard());
                break;
            default:
                    break;
        }
    }

    
    /**
     * Puts symbol on board, calls for redraw
     * of board and sends message about move
     * to server.
     */
    public void makeTurn(){
        if(board.getSymbol(selectedRow, selectedColumn)==null)
            board.putSymbol("?", selectedRow, selectedColumn);
        window.drawBoard(board.drawBoard());
        sendCoordinates();
    }
	/**
     * @Deprecated
     * Gets confirmation from player read from standard input.
	 * It is no longer need becouse input is no
	 * longer read from keyboard
     * @return 
     */
	private boolean getYesNoInput() {
		try {
			if("Y".equalsIgnoreCase(stdin.readLine()))
					return true;
			else if("N".equalsIgnoreCase(stdin.readLine()))
				return false;
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	/**
     * 
	 * Method sets <b>selectedRow</b> and
	 * and <b>selectedColumn</b> parameter
	 * with given parameters
	 * @param int row
	 * @param int column
	 */
	public void setNumberInput(int row, int column){	
        selectedRow = row-1;
        selectedColumn = column-1;
	}
	/**
	 * 
	 * @return IP
	 */
	public String getIP() {
		return IP;
	}
	
	/**
	 * 
	 * @param IP
	 */
	public void setIP(String iP) {
		IP = iP;
	}
	
	/**
	 * 
	 * @return Port number
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * 
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}
    
    /**
     * 
     * @param name 
     */
    public void setPlayerName(String name){
        me.setName(name);
        playerName=name;
    }
    
    /**
     * 
     * @return Player name
     */
    public String getPlayerName(){
        return playerName;
    }  

	/**
	 * Sets socket, DataInputStream and DataOutoputStream connected to this socket
	 * @param socket
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
		try {
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}		
	}
    /**
     * Sets PrintWriter - output stream
     * @param out 
     */
	public void setOut(PrintWriter out) {
		this.out = out;
	}
    /**
     * Sets BufferedReader - input stream
     * @param in 
     */
	public void setIn(BufferedReader in) {
		this.in = in;
	}
	/**
     * 
     * @return board
     */
	public Board getBoard() {
		return board;
	}
    
    /**
     * If connection was made it send message EXIT
     * to server then close all data streams.
     */
    public void closeClient() {
        try{
            if(socket != null){
                if(socket.isConnected()){
                    out.println(EXIT);
                    in.close();
                    out.close();
                    socket.close();
                    stdin.close();
                    closingProgram = true;
                }
                System.exit(0);
            }
        }catch(IOException ex){
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Reads from BufferedReader for server message.
     * @return message from server.
     * @throws IOException 
     */
    public String getMessageFromServer(){
        try {
            return in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Sends info about move to server.
     */
    public void sendCoordinates() {
        out.println(selectedRow+";"+selectedColumn);
    }
    /**
     * @return true if connection was
     * made with success.
     */
    public boolean isConnected() {
        return connected;
    }
    /**
     * 
     * @param connected 
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    /**
     * This method should ensure a smooth switch between the GUI
     * thread and the Client class thread.
     * Does work for now.
     * Magic. From this method Gandalf
     * is taking his powers. Seriously.
     * 
     */
    private void lookAfterThisGame(){
        synchronized(window){
            while(true){
                if(window.isConnectedClicked()){
                    init();
                    getRules();
                    window.setConnectedCliked(false);
                }
                if(connected){
                    try{
                        String message = getMessageFromServer();
                        if(message!=null)
                            playTheGame(message);
                    }catch(IOException ex){
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(closingProgram){
                    break;
                }
            }
        }
    }
    
    /**
     * Main method. All evil was born here.
     * @param args 
     */
    public static void main(String[] args){
        final Client client = new Client();
        window = new ClientWindow(client);
        client.lookAfterThisGame();
    }
}
