package edu.uclm.esi.tys2122.model;

import java.io.IOException;
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
	
	/* Attributes */
	
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
	protected boolean ready;
	
	/* Constructors */
	
	public Match() {
		this.id = UUID.randomUUID().toString();
		this.players = new Vector<>();
		this.board = newBoard();
	}
	
	/* Functions */
	
	public void addPlayer(User user) {
		// We still add the same player two times
		this.players.add(user);
		checkReady();
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void notifyNewState(String userId) {
		JSONObject jso = new JSONObject();
		jso.put("type", "BOARD");
		// jso.put("board", this.board.toJSON());
		
		for (User player : this.players) {
			if(!player.getId().equals(userId))
				try {
					player.sendMessage(jso);
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	/* Abstract Functions */
	
	protected abstract void checkReady();
	
	protected abstract Board newBoard();
	
	public abstract void move(String userId, JSONObject jso) throws Exception;
	
	/* Getters And Setters */
	
	public String getGame() {
		return this.getClass().getSimpleName();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Transient
	public Vector<User> getPlayers() {
		return players;
	}
	
	public void setPlayers(Vector<User> players) {
		this.players = players;
	}
	
	public User getPlayerWithTurn() {
		return playerWithTurn;
	}
	
	public void setPlayerWithTurn(User playerWithTurn) {
		this.playerWithTurn = playerWithTurn;
	}

	public Board getBoard() {
		return board;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	public void setReady(boolean ready) {
		this.ready = ready;
	}

}
