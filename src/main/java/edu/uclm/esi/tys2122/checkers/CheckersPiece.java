package edu.uclm.esi.tys2122.checkers;

import edu.uclm.esi.tys2122.model.Piece;

public class CheckersPiece extends Piece {
	private int id;
	private String color;
	private String type;
	private boolean alive;
	
	/* Constructors */
	
	public CheckersPiece() {
		super();
		this.type = "pawn";
		this.alive = true;
	}
	
	public CheckersPiece(int id, String color, CheckersSquare square) {
		super();
		this.id = id;
		this.color = color;
		this.type = "pawn";
		this.alive = true;
	}
	
	/* Functions */
	
	public void promote() {
		this.type = "queen";
	}
	
	public void die() {
		this.alive = false;
	}
	
	/* Getters And Setters */
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
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
		
	public boolean isAlive() {
		return alive;
	}
	
}
