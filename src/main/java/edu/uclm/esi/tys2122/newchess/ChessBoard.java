package edu.uclm.esi.tys2122.newchess;

import edu.uclm.esi.tys2122.model.Board;

public class ChessBoard extends Board {
	private static final int TORRE_BLANCA = 1;
	private static final int CABALLO_BLANCO = 2;
	
	private static final int TORRE_NEGRA = -TORRE_BLANCA;
	private static final int CABALLO_NEGRO = -CABALLO_BLANCO;
	
	private int[][] squares;
	
	public ChessBoard() {
		this.squares = new int[8][8];
		for (int i=0; i<8; i++)
			for (int j=0; j<8; j++)
				this.squares[i][j] = 0;
		
		this.squares[0][0] = TORRE_BLANCA;
		this.squares[0][7] = TORRE_BLANCA;
		this.squares[0][1] = CABALLO_BLANCO;
		this.squares[0][6] = CABALLO_BLANCO;
		
		this.squares[7][0] = TORRE_NEGRA;
		this.squares[7][7] = TORRE_NEGRA;
		this.squares[7][1] = CABALLO_NEGRO;
		this.squares[7][6] = CABALLO_NEGRO;
	}
	
	public int[][] getSquares() {
		return squares;
	}
}
