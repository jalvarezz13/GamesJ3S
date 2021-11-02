package edu.uclm.esi.tys2122.model;

import java.util.UUID;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.JSONObject;

@Entity
@Table(name = "partida")
public abstract class Match {
	@Id
	@Column(length = 36)
	private String id;
	
	@Transient
	private Board board;
	
	@Transient
	protected Vector<User> players;
	
	@Transient
	protected User playerWithTurn; 
	
	@Transient
	protected User winner, looser;
	protected boolean draw;
	
	@Transient
	protected boolean ready;
	
	public Match() {
		this.id = UUID.randomUUID().toString();
		this.players = new Vector<>();
		this.board = newBoard();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Board getBoard() {
		return board;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}

	// TODO : no se puede añadir dos veces el mismo jugador
	public void addPlayer(User user) {
		this.players.add(user);
		checkReady();
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public User getPlayerWithTurn() {
		return playerWithTurn;
	}
	
	@Transient
	public Vector<User> getPlayers() {
		return players;
	}
	
	public String getGame() {
		return this.getClass().getSimpleName();
	}

	
	
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

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	protected abstract void checkReady();

	protected abstract Board newBoard();

	public abstract void move(String userId, JSONObject jso) throws Exception;

}
