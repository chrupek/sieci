package com.chrupek.sieci.serwer.core;

import java.io.Serializable;

public class PlayerStats implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2298789358640256213L;
	private int wins=0;
	private int loses=0;
	private int draws=0;
	private int games=0;
	private int score=0;
	
	public int getDraws()
	{
		return draws;
	}

	public int getGames()
	{
		return games;
	}

	public int getLoses()
	{
		return loses;
	}

	public int getScore()
	{
		return score;
	}

	public int getWins()
	{
		return wins;
	}
	private void UpdateScore()
	{
		this.score=5*wins + 2*draws + games;
	}
	public void addWin()
	{
		this.wins++;
		this.games++;
		this.UpdateScore();
	}
	public void addDraw()
	{
		this.draws++;
		this.games++;
		this.UpdateScore();
	}
	public void addLoss()
	{
		this.loses++;
		this.games++;
		this.UpdateScore();
	}
	PlayerStats()
	{

	}
	public String toString()
	{
		StringBuilder result= new StringBuilder();
		result.append("win "+ this.wins+"; ");
		result.append("loses "+ this.loses+"; ");
		result.append("draws "+ this.draws+"; ");
		result.append("score "+ this.score+";");
		return result.toString();
	}
}
