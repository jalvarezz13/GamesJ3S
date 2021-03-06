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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import edu.uclm.esi.tys2122.dao.UserRepository;
import edu.uclm.esi.tys2122.websockets.WrapperSession;

@Entity
@Table(name = "battle")
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
	
	@Transient
	@Autowired
	protected UserRepository userRepo;

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
			if (!player.getId().equals(userId))
				try {
					player.sendMessage(jso);
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Notifica a los usuarios de la partida excepto a @param users
	 * 
	 * @param type  Tipo de actualizacion de la match {READY, UPDATE, FINISH}
	 * @param users Usuarios a los que no se notifica la actualizacion
	 */
	public void notifyOponents(String type, User... users) {
		JSONObject jso = new JSONObject();
		jso.put("type", type);
		jso.put("matchId", this.id);
		byte[] payload = jso.toString().getBytes();
		TextMessage msg = new TextMessage(payload);

		for (User user : this.players) {
			if (isUser(user, users))
				continue;
			Runnable r = new Runnable() {
				@Override
				public void run() {
					WrapperSession ws = user.getSession();
					WebSocketSession wss = ws.getWsSession();
					try {
						wss.sendMessage(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			};
			new Thread(r).start();
		}
	}

	private boolean isUser(User user, User[] users) {
		for (int i = 0; i < users.length; i++) {
			if (user == users[i])
				return true;
		}
		return false;
	}

	/* Abstract Functions */

	protected abstract void checkReady();

	protected abstract Board newBoard();

	public abstract void move(String userId, JSONObject jso) throws Exception;

	public abstract User getWinner();
	
	public abstract User getLooser();

	public abstract void closeMatchByUser(User user);

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
