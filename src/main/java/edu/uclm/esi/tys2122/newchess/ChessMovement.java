package edu.uclm.esi.tys2122.newchess;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ChessMovement {
	@Id
	@Column(length = 36)
	private String id;
	private Integer pos;
	@Column(length = 10)
	private String white, black;
	@ManyToOne(cascade = CascadeType.ALL)
	private ChessMatch chessMatch;
	private String comment;
	
	public ChessMovement() {
		this.id=UUID.randomUUID().toString();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public String getWhite() {
		return white;
	}

	public void setWhite(String white) {
		this.white = white;
	}

	public String getBlack() {
		return black;
	}

	public void setBlack(String black) {
		this.black = black;
	}

	public void setChessMatch(ChessMatch chessMatch) {
		this.chessMatch = chessMatch;
	}
	
	public ChessMatch getChessMatch() {
		return chessMatch;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
}
