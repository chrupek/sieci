package com.chrupek.sieci.client.model;

public class Cell {
	
	private String symbol = "";

	private String horizontalBorder = "+----";
	private String verticalBorder = "|       ";
	
	public Cell(){}
	
	/** 
	 * Returns symbol bind to cell
	 * @return char
	 */
	public String getSymbol() {
		return symbol;
	}

	/**  
     * Puts symbol into cell.
	 * @param String symbol
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
		this.verticalBorder = "|   " +this.symbol+"   ";
	}
	
	/** 
	 * This is the last cell in a row so add a right border and row number.
	 */
	public void thisIsLastCell(int rowNm){
		this.horizontalBorder = this.horizontalBorder+ "+";
		this.verticalBorder = this.verticalBorder+ "|  "+Integer.toString(rowNm);
	}

    /**
     * 
     * @return String
     */
	public String getHorizontalBorder() {
		return horizontalBorder;
	}

    /**
     * 
     * @return String
     */
	public String getVerticalBorder() {
		return verticalBorder;
	}
}
