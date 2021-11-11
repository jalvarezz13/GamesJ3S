package edu.uclm.esi.tys2122.checkers;

import java.security.SecureRandom;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.json.JSONObject;

import edu.uclm.esi.tys2122.model.Board;
import edu.uclm.esi.tys2122.model.Match;
import edu.uclm.esi.tys2122.model.User;

@Entity
public class CheckersMatch extends Match {
	/* Attributes */
	
	@OneToOne
	private User winner;
	@OneToOne
	private User looser;
	private boolean draw;

	/* Functions */
	
	@Override
	protected Board newBoard() {
		return new CheckersBoard();
	}

	@Override
	protected void checkReady() {
		this.ready = this.players.size() == 2;
		if (this.ready)
			this.playerWithTurn = new SecureRandom().nextBoolean() ? this.players.get(0) : this.players.get(1);
	}

	@Override
	public void move(String userId, JSONObject jsoMovimiento) throws Exception {

	}
	
	/* Getters And Setters */
	
	public User getWinner() {
		return winner;
	}

	public void setWinner(User winner) {
		this.winner = winner;
	}

	public User getLooser() {
		return looser;
	}

	public void setLooser(User looser) {
		this.looser = looser;
	}

	public boolean isDraw() {
		return draw;
	}

	public void setDraw(boolean draw) {
		this.draw = draw;
	}

}
