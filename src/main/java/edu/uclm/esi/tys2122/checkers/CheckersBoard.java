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
		int counter = 12;
		boolean vuelta = false;

		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				this.squares[i][j] = new CheckersSquare();

				int[] id = { i, j };
				this.squares[i][j].setId(id);

				if (i % 2 == 0) {
					this.squares[i][j].setColor(squareColorPair);
					squareColorPair = squareColorPair == "NEGRO" ? "BLANCO" : "NEGRO";
				} else {
					this.squares[i][j].setColor(squareColorOdd);
					squareColorOdd = squareColorOdd == "NEGRO" ? "BLANCO" : "NEGRO";
				}

				if (i == 0) {
					this.squares[i][j].setUpperBorder(true);
				}

				if (i == squares.length - 1) {
					this.squares[i][j].setBottomBorder(true);
				}
				
				if (j == 0) {
					this.squares[i][j].setLeftBorder(true);
				}
				
				if (j == squares.length - 1) {
					this.squares[i][j].setRightBorder(true);
				}

				if (i == 0 || i == squares.length - 1) {
					this.squares[i][j].setCoronate(true);
				}

				if (this.squares[i][j].getColor().equals("NEGRO") && i != 3 && i != 4) {
					CheckersPiece auxPiece = new CheckersPiece();
					auxPiece.setColor(pieceColor);
					pieceColor = counter == 1 ? "NEGRO" : pieceColor;

					auxPiece.setId(counter);
					
					if(counter > 1 && !vuelta) {
						counter --;
					}else if (vuelta){
						counter ++;
					}else{
						vuelta = true;
					}

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
