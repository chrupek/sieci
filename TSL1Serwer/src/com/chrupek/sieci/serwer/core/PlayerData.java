package com.chrupek.sieci.serwer.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class PlayerData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6725437400250938712L;
	
	private String playerName;
	private String playerSymbol;
	private int playerId;
	private int score;
	private ArrayList<Coordinates> usedCoordinates;	// coordinates used by this player 
	
	public PlayerData(String name, String symbol, int id){
		this.playerName = name;
		this.playerSymbol = symbol;
		this.playerId = id;
		usedCoordinates = new ArrayList<Coordinates>();
	}
	
	/**
	 * Sets new coordinates (row and column) in witch
	 * player had put his symbol
	 * @param x
	 * @param y
	 */
	public void setCoordinates(int x, int y){
		Coordinates c = new Coordinates(x,y);
		usedCoordinates.add(c);
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public String getPlayerSymbol() {
		return playerSymbol;
	}

	public int getPlayerId() {
		return playerId;
	}

	/**
	 * Used when a player leaves in the middle of the game
	 * to clear his symbols one by one. Returns and removes coordinates
	 * of a random symbol of this player
	 * @return 
	 */
	public Coordinates getRandomCoordinates(){
		Random r = new Random();
		int rIndex = r.nextInt(usedCoordinates.size());
		return usedCoordinates.remove(rIndex);
		
	}
	
	/**
	 * @return true if list with
	 * coordinates used by this player is empty.
	 */
	public boolean areUsedCoordsEmpty(){
		return usedCoordinates.isEmpty();
	}
	
	/**
	 * 
	 */
	public void addPoint(){
		score++;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getScore(){
		return score;
	}

}
