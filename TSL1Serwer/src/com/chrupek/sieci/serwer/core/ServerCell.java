package com.chrupek.sieci.serwer.core;

public class ServerCell {
	
	private String symbol = null;
	
	public ServerCell(){}
	
	/** 
	 * Returns symbol bind to cell
	 * @return char
	 */
	public String getSymbol() {
		return symbol;
	}

	/**  
	 * @param symbol
	 * puts symbol into cell
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
