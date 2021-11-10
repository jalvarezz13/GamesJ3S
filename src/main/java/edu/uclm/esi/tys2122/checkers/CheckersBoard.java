package edu.uclm.esi.tys2122.checkers;

import edu.uclm.esi.tys2122.model.Board;

public class CheckersBoard extends Board {
	private String[][] squares;
	
	public CheckersBoard() {
		this.squares = new String[8][8];
		for (int i=0; i<8; i++)
			for (int j=0; j<8; j++)
				this.squares[i][j] = "*";
	}
	
	public String[][] getSquares() {
		return squares;
	}
}
