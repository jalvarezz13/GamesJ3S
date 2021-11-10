package edu.uclm.esi.tys2122.checkers;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CheckersMovement {
	@Id
	@Column(length = 36)
	private String id;
	private Integer pos;
	@Column(length = 10)
	private String white, black;
	@ManyToOne(cascade = CascadeType.ALL)
	private CheckersMatch checkersMatch;
	private String comment;

	public CheckersMovement() {
		this.id = UUID.randomUUID().toString();
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

	public void setChessMatch(CheckersMatch chessMatch) {
		this.checkersMatch = chessMatch;
	}

	public CheckersMatch getChessMatch() {
		return checkersMatch;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}
}
