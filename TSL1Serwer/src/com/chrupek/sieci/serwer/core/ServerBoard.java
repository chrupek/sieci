package com.chrupek.sieci.serwer.core;

import java.util.List;
import java.util.ArrayList;

public class ServerBoard {
	
	private int size;
	private List<List<ServerCell>> board;
	private String listOfCoordsToDelete="";	// list of coordinates to be deleted if player got a score 
	
	/**
	 * @param size
	 */
	public ServerBoard(int size){
		this.size = size;
		board = new ArrayList<List<ServerCell>>();
		makeBorad();
	}
	
	public ServerBoard(){
		this.size = 0;
		board = new ArrayList<List<ServerCell>>();
	}
	
	/**
	 * initializes board
	 */
	public void makeBorad(){
		for(int i=0;i<size;++i){
			List<ServerCell> row = new ArrayList<ServerCell>();
				for(int j=0;j<size;++j){
					ServerCell c = new ServerCell();
					row.add(c);
					
				}	
			board.add(row);
		}
	}

	/**
	 * @param row
	 * @param column
	 * @return Cell
	 */
	
	private ServerCell getCell(int row, int column) {
		return board.get(row).get(column);
	}
	/**
	 * 
	 * @param symbol
	 * @param row
	 * @param column
	 */
	public void putSymbol(String symbol, int row, int column){
		ServerCell c = getCell(row, column);
		c.setSymbol(symbol);
	}

	/**
	 * Returns symbol bind to specific cell
	 * @param row
	 * @param column
	 * @return symbol that is on filed on board
	 */
	public String getSymbol(int row, int column) {
		ServerCell c = getCell(row, column);
		return c.getSymbol();
	}
	
	/**
	 *
	 * @return
	 */
	public String getListOfCoordsToDelete() {
		return listOfCoordsToDelete;
	}

	/**
	 * Checks if player with symbol <B>symbol</B> got a score.
	 * @return true if player is the winner.
	 * @param row
	 * @param column
	 * @param number of symbols to win
	 * @param symbol we are checking
	 */
	
	// TODO refactor
	public boolean checkForScore(int row, int column, int winSymCount, String symbol){
	
		int gotSymbols=1;			// founded symbols, we already have one symbol at this time
		int i=0, j=0;				// index
		
		//listOfCoordsToDelete = row+"%"+column+";";
		
		// check horizontally first
		if(column<size-1){			// this is not the last column
			i=column+1;				// start from cell in next column
			while(i<size){	 		// check ahead in this row, may reach end of the board
				if(symbol.equalsIgnoreCase(getCell(row, i).getSymbol())){
					listOfCoordsToDelete = listOfCoordsToDelete+row+"%"+i+";";
					gotSymbols++;
				}
				else i=size+1;		/* we found symbol of another player in this row
									   so there is no point in checking any further.
									   we jump off the loop 
									*/
					
				if(gotSymbols == winSymCount){	//player win
					listOfCoordsToDelete = row+"%"+column+";"+listOfCoordsToDelete;
					return true;
				}
				i++;
			}
		}
		if(column>0){ // if column is 0 then we were starting from the beginning in the previous loop
			i=column-1;  // now we are checking backwards
			while(i>=0){
				if(symbol.equalsIgnoreCase(getCell(row, i).getSymbol())){
					listOfCoordsToDelete = listOfCoordsToDelete+row+"%"+i+";";
					gotSymbols++;
				}
				else i=-1;			/* The same as before,  
										we found symbol of another player in this row
									  	so there is no point in checking any further.
									  	we jump off the loop 
									*/
				
				if(gotSymbols == winSymCount){	//player win
					listOfCoordsToDelete = row+"%"+column+";"+listOfCoordsToDelete;
					return true;
				}
				i--;
			}
		}
		
		
		gotSymbols=1; // no luck 
		//listOfCoordsToDelete = row+"%"+column+";";	// lets clear the unnecessary coordinates
		
		// now check vertically
		if(row<size-1){		// this is not the last row
			i=row+1;		// start from the row below this one
			while(i<size){
				if(symbol.equalsIgnoreCase(getCell(i, column).getSymbol())){
					listOfCoordsToDelete = listOfCoordsToDelete+row+"%"+i+";";
					gotSymbols++;
				}
				else i=size+1;		/* 
									  the same case as in horizontal checking
									*/
				if(gotSymbols == winSymCount){	//player win
					listOfCoordsToDelete = row+"%"+column+";"+listOfCoordsToDelete;
					return true;
				}
				i++;
			}
		}
		if(row>0){ 			// as before if row is 0 then we were starting from the beginning in the previous loop
			i=row-1;  		// now we are checking backwards
			while(i>=0){
				if(symbol.equalsIgnoreCase(getCell(i, column).getSymbol())){
					listOfCoordsToDelete = listOfCoordsToDelete+i+"%"+column+";";
					gotSymbols++;
				}
				else i=-1;			/* 
									  the same case as in horizontal checking
									*/
				if(gotSymbols == winSymCount){	//player win
					listOfCoordsToDelete = row+"%"+column+";"+listOfCoordsToDelete;
					return true;
				}
				i--;
			}
		}
		
		
		gotSymbols=1;
		//listOfCoordsToDelete = row+"%"+column+";";	// lets clear the unnecessary coordinates
		
		// now check diagonally
		if(row<size-1 && column<size-1){
			// check forward
			i=row+1;
			j=column+1;
			while(i<size || j<size){	
				if(symbol.equalsIgnoreCase(getCell(i, j).getSymbol())){
					listOfCoordsToDelete = listOfCoordsToDelete+row+"%"+i+";";
					gotSymbols++;
				}
				else {			// symbol of another player occurred
					i=size+1;	
					j=size+1;
				}
				if(gotSymbols == winSymCount){	//player win
					listOfCoordsToDelete = row+"%"+column+";"+listOfCoordsToDelete;
					return true;
				}
				i++;
				j++;
			}
		}
		//check backwards
		if(row>0 && column>0){
			i=row-1;
			j=column-1;
			while(i>=0 || j>=0){	
				if(symbol.equalsIgnoreCase(getCell(i, j).getSymbol())){
					listOfCoordsToDelete = listOfCoordsToDelete+row+"%"+i+";";
					gotSymbols++;
				}
				else {			// symbol of another player occurred
					i=-1;	
					j=-1;
				}
				if(gotSymbols == winSymCount){	//player win
					listOfCoordsToDelete = row+"%"+column+";"+listOfCoordsToDelete;
					return true;
				}
				i--;
				j--;
			}
		}
		
		// none of above conditions were met 
		listOfCoordsToDelete="";
		return false;
	}

	public void resetListOfCoordsToDelete() {
		listOfCoordsToDelete = "";
		
	}
}

