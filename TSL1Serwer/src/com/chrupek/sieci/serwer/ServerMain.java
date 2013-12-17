package com.chrupek.sieci.serwer;

public class ServerMain {

	public static void main(String[] args) throws Exception{
		int listeningPort = Integer.parseInt(args[0]);
		int boardSize = Integer.parseInt(args[1]);
		int winnigSymbolNum = Integer.parseInt(args[2]);
		if(boardSize == 0 ){
			System.out.println("Borad size can't be 0. Please insert valid number:");
			boardSize = System.in.read();
		}
		if(winnigSymbolNum == 0 ){
			System.out.println("Number of symbols needed to win can't be 0. Please insert valid number:");
			winnigSymbolNum = System.in.read();
		}
		new Server(listeningPort, boardSize, winnigSymbolNum);
	}
}
