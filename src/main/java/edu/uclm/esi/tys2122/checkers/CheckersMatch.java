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
	@OneToOne
	private User winner;
	@OneToOne
	private User looser;
	private boolean draw;

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

	public String getSquare(Integer x, Integer y) {
		CheckersBoard board = (CheckersBoard) this.getBoard();
		return board.getSquares()[x][y];
	}

	public void setSquare(Integer x, Integer y, String value) {
		CheckersBoard board = (CheckersBoard) this.getBoard();
		board.getSquares()[x][y] = value;
	}

	@Override
	public void move(String userId, JSONObject jsoMovimiento) throws Exception {
		if (this.winner == null) // crear metodo filled con todas las condiciones para dar por terminada una partida
			throw new Exception("La partida ya terminó");

		if (!this.getPlayerWithTurn().getId().equals(userId))
			throw new Exception("No es tu turno");

		/**
		 * COMPLETAR Integer x = jsoMovimiento.getInt("x"); Integer y =
		 * jsoMovimiento.getInt("y");
		 * 
		 * if (this.getSquare(x, y)!=0) throw new Exception("Casilla ocupada");
		 * 
		 * int value = this.getPlayerWithTurn()==this.getPlayers().get(0) ? 1 : 2;
		 * this.setSquare(x, y, value);
		 * 
		 * checkWinner();
		 **/

		if (this.winner == null) // metodo filled or Anadir con && mas condiciones de fin de partida
			this.draw = true;
		else {
			this.playerWithTurn = this.getPlayerWithTurn() == this.getPlayers().get(0) ? this.getPlayers().get(1) : this.getPlayers().get(0);
		}
	}

	private void checkWinner() {
		CheckersBoard board = (CheckersBoard) this.getBoard();
		String[][] squares = board.getSquares();
		int blancas = 0, negras = 0;
		
 		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				if (squares[i][j]=="B")
					blancas ++;
		 		if (squares[i][j]=="N")
					negras ++;
		 		if (squares[i][j]==" ");
			}
		}
 		if(blancas != negras && blancas == 0) {
 			this.winner = this.players.get(0); //tenemos que saber que jugador va a ser quien
 			this.looser = this.players.get(1);
  		} else if(blancas != negras && blancas == 0) {
 			this.winner = this.players.get(1);
 			this.looser = this.players.get(0);
 		}

	}
	
	

	public void setWinner(User winner) {
		this.winner = winner;
	}

	public void setLooser(User looser) {
		this.looser = looser;
	}

	public void setDraw(boolean draw) {
		this.draw = draw;
	}

	public User getWinner() {
		return winner;
	}

	public User getLooser() {
		return looser;
	}

	public boolean isDraw() {
		return draw;
	}
}
