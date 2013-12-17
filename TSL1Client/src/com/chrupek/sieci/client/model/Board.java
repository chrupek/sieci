package com.chrupek.sieci.client.model;

import java.util.List;
import java.util.ArrayList;

/**
 * This class represents board.
 * @author chrupek
 */
public class Board {
	
    /**
     * Size of the board.
     */
	private int size;
    /**
     * Board itself.
     */
	private List<List<Cell>> board;
	
	/**
	 * @param size
	 */
	public Board(int size){
		this.size = size;
		board = new ArrayList<List<Cell>>();
		makeBorad();
	}
	
    /**
     * Default constructor. Need to set {@link size}
     * and call (@link makeBoard}.
     */
	public Board(){
		this.size = 0;
		board = new ArrayList<List<Cell>>();
	}
	
	/**
	 * initializes board
	 */
	public void makeBorad(){
		for(int i=0;i<size;++i){
			List<Cell> row = new ArrayList<Cell>();
				for(int j=0;j<size;++j){
					Cell c = new Cell();
					if(j==size-1){
						c.thisIsLastCell(i+1);
					}
					row.add(c);
					
				}	
			board.add(row);
		}
	}
	
	/**
	 * Draws board to String and returns it.
     * @return String board 
	 */
	public String drawBoard(){
            String drewBoard="";
            boolean horizontal = false;
            drewBoard = drawHeader(drewBoard);
            for(List<Cell> row: board){
                for(int i=0; i<row.size(); ++i){
                        if(!horizontal){
                               drewBoard = drewBoard + row.get(i).getHorizontalBorder();
                                if(i==size-1){
                                        drewBoard = drewBoard+"\n";
                                        horizontal = true;
                                        i = -1; // dlatego, że sparwdzany jest warunek a potem przed wejściem do pętli wykonywane jest ++i
                                }
                        }
                        else{
                                drewBoard = drewBoard +row.get(i).getVerticalBorder();
                                if(i==size-1){
                                        drewBoard = drewBoard+"\n";
                                        horizontal = false;
                                }
                        }	
                }
            }
            return drewBoard;
	}
	
    /**
     * Draws header (columns numbers)
     * and returns it for further processing.
     * @param s
     * @return String
     */
	private String drawHeader(String s) {
		for(int i=0; i<size; ++i){
			s = s + "    "+(i+1)+" ";
			if(i==size-1)
				s=s+"\n";
		}
		return s;
	}

	/**
	 * @param row
	 * @param column
	 * @return Cell
	 */
	private Cell getCell(int row, int column) {
		return board.get(row).get(column);
	}
	/**
	 * 
	 * @param symbol
	 * @param row
	 * @param column
	 */
	public void putSymbol(String symbol, int row, int column){
		Cell c = getCell(row, column);
		c.setSymbol(symbol);
	}

	/**
	 * Returns symbol bind to specific cell
	 * @param row
	 * @param column
	 * @return String
	 */
	public String getSymbol(int row, int column) {
		Cell c = getCell(row, column);
		return c.getSymbol();
	}
}

