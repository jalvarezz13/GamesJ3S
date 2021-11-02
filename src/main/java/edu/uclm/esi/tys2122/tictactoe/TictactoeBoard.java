package edu.uclm.esi.tys2122.tictactoe;

import edu.uclm.esi.tys2122.model.Board;

public class TictactoeBoard extends Board {
	private int[][] squares;
	
	public TictactoeBoard() {
		this.squares = new int[3][3];
		for (int i=0; i<3; i++)
			for (int j=0; j<3; j++)
				this.squares[i][j] = 0;
	}
	
	public int[][] getSquares() {
		return squares;
	}
}
