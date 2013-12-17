package com.chrupek.sieci.client.model;

/**
 * 
 * @author chrupek 
 * This class stores info about player.
 */
public class Player {
	private String name;
	private String symbol;
	private int id;
	private int score;
	
    /**
     * This constructor creates player
     * and sets his name, symbol and id.
     * @param name
     * @param symbol
     * @param id 
     */
	public Player(String name, String symbol, int id){
		this.name = name;
		this.symbol = symbol;
		this.id =id;
		score = 0;
	}
	/**
	 * Default constructor.
     * If used you need to set 
     * player name and symbol.
	 */
	public Player(){
		this.name = null;
		this.symbol = null;
	}
	/**
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return String
	 */
	public String getSymbol() {
		return symbol;
	}
	
	/**
	 * 
	 * @param symbol
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
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
