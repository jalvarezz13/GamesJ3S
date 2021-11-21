package edu.uclm.esi.tys2122.checkers;

import edu.uclm.esi.tys2122.model.Board;

public class CheckersBoard extends Board {

	/* Attributes */

	private CheckersSquare[][] squares;

	/* Constructors */

	public CheckersBoard() {
		this.squares = new CheckersSquare[8][8];
		String squareColorOdd = "BLANCO";
		String squareColorPair = "NEGRO";
		String pieceColor = "BLANCO";
		int counter = 1;

		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				this.squares[i][j] = new CheckersSquare();

				int[] id = { i, j };
				this.squares[i][j].setId(id);

				if (i%2 == 0) {
					this.squares[i][j].setColor(squareColorPair);
					squareColorPair = squareColorPair == "NEGRO" ? "BLANCO" : "NEGRO";
				} else {
					this.squares[i][j].setColor(squareColorOdd);
					squareColorOdd = squareColorOdd == "NEGRO" ? "BLANCO" : "NEGRO";
				}


				if (i == 0 || i == squares.length - 1 || j == 0 || j == squares.length - 1) {
					this.squares[i][j].setBorder(true);
				}

				if (i == 0 || i == squares.length - 1) {
					this.squares[i][j].setCoronate(true);
				}

				if (this.squares[i][j].getColor().equals("NEGRO") && i != 3 && i != 4) {
					CheckersPiece auxPiece = new CheckersPiece();
					auxPiece.setColor(pieceColor);
					pieceColor = counter == 12 ? "NEGRO" : pieceColor;
										
					auxPiece.setId(counter);
					counter = counter < 12 ? counter+1 : 1;
					
					this.squares[i][j].setPiece(auxPiece);
				}
			}
		}
	}

	/* Setters And Getters */

	public CheckersSquare[][] getSquares() {
		return squares;
	}

	public void setSquares(CheckersSquare[][] squares) {
		this.squares = squares;
	}

}
