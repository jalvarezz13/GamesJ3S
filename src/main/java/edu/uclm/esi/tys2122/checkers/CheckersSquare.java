package edu.uclm.esi.tys2122.checkers;

import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.esi.tys2122.model.Square;

public class CheckersSquare extends Square {
	private int[] id = new int[2];
	private String color;
	private boolean upperBorder, leftBorder, rightBorder, bottomBorder, coronate;
	private CheckersPiece piece;

	/* Constructors */

	public CheckersSquare() {
		super();
		this.upperBorder = false;
		this.leftBorder = false;
		this.rightBorder = false;
		this.bottomBorder = false;
		this.coronate = false;
		this.piece = null;

	}

	public CheckersSquare(int[] id, String color, boolean upperBorder, boolean leftBorder, boolean rightBorder, boolean bottomBorder, boolean coronate, CheckersPiece piece) {
		super();
		this.id = id;
		this.color = color;
		this.upperBorder = false;
		this.leftBorder = false;
		this.rightBorder = false;
		this.bottomBorder = false;
		this.coronate = coronate;
		this.piece = piece;
	}

	/* Functions */
	
	@JsonIgnore
	public Vector<int[]> getPossibleMovements() {
		Vector<int[]> auxPossibles = new Vector<int[]>();
		if (piece.getType() == "pawn") {
			if (piece.getColor().equals("BLANCO")) {
				if (leftBorder) {
					int[] auxId = { this.getId()[0] + 1, this.getId()[1] + 1 };
					auxPossibles.add(auxId);
				} else if (rightBorder) {
					int[] auxId = { this.getId()[0] + 1, this.getId()[1] - 1 };
					auxPossibles.add(auxId);
				} else {
					int[] auxId1 = { this.getId()[0] + 1, this.getId()[1] - 1 };
					auxPossibles.add(auxId1);
					int[] auxId2 = { this.getId()[0] + 1, this.getId()[1] + 1 };
					auxPossibles.add(auxId2);
				}
			} else {
				if (leftBorder) {
					int[] auxId = { this.getId()[0] - 1, this.getId()[1] + 1 };
					auxPossibles.add(auxId);
				} else if (rightBorder) {
					int[] auxId = { this.getId()[0] - 1, this.getId()[1] - 1 };
					auxPossibles.add(auxId);
				} else {
					int[] auxId1 = { this.getId()[0] - 1, this.getId()[1] - 1 };
					auxPossibles.add(auxId1);
					int[] auxId2 = { this.getId()[0] - 1, this.getId()[1] + 1 };
					auxPossibles.add(auxId2);
				}
			}
		} else {
			// TODO
		}
		return auxPossibles;
	}

	/* Getters And Setters */

	public int[] getId() {
		return id;
	}

	public void setId(int[] id) {
		this.id = id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isUpperBorder() {
		return upperBorder;
	}

	public void setUpperBorder(boolean upperBorder) {
		this.upperBorder = upperBorder;
	}

	public boolean isLeftBorder() {
		return leftBorder;
	}

	public void setLeftBorder(boolean leftBorder) {
		this.leftBorder = leftBorder;
	}

	public boolean isRightBorder() {
		return rightBorder;
	}

	public void setRightBorder(boolean rightBorder) {
		this.rightBorder = rightBorder;
	}

	public boolean isBottomBorder() {
		return bottomBorder;
	}

	public void setBottomBorder(boolean bottomBorder) {
		this.bottomBorder = bottomBorder;
	}

	public boolean isCoronate() {
		return coronate;
	}

	public void setCoronate(boolean coronate) {
		this.coronate = coronate;
	}

	public CheckersPiece getPiece() {
		return piece;
	}

	public void setPiece(CheckersPiece piece) {
		this.piece = piece;
	}

}
