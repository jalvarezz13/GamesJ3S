package edu.uclm.esi.tys2122.checkers;

import edu.uclm.esi.tys2122.model.Square;

public class CheckersSquare extends Square{
	private String id;
	private String color;
	private String type;
	private CheckersPiece piece;
	
	/* Constructors */
	
	public CheckersSquare() {
		super();
		
	}
	
	public CheckersSquare(String id, String color, String type, CheckersPiece piece) {
		super();
		this.id = id;
		this.color = color;
		this.type = type;
		this.piece = piece;
	}
	
	/* Functions */
	
	
	
	/* Getters And Setters */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CheckersPiece getPiece() {
		return piece;
	}

	public void setPiece(CheckersPiece piece) {
		this.piece = piece;
	}

}
