package edu.uclm.esi.tys2122.checkers;

import edu.uclm.esi.tys2122.model.Square;

public class CheckersSquare extends Square {
	private int[] id = new int[2];
	private String color;
	private boolean border, coronate;
	private CheckersPiece piece;

	/* Constructors */

	public CheckersSquare() {
		super();
		this.border = false;
		this.coronate = false;
		this.piece = null;

	}

	public CheckersSquare(int[] id, String color, boolean border, boolean coronate, CheckersPiece piece) {
		super();
		this.id = id;
		this.color = color;
		this.border = border;
		this.coronate = coronate;
		this.piece = piece;
	}

	/* Functions */
	
	

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

	public boolean isBorder() {
		return border;
	}

	public void setBorder(boolean border) {
		this.border = border;
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
