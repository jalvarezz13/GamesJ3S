package edu.uclm.esi.tys2122.checkers;

import java.security.SecureRandom;
import java.util.Vector;

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
		if (this.ready) {
			this.playerWithTurn = new SecureRandom().nextBoolean() ? this.players.get(0) : this.players.get(1);
			super.notifyOponents("MATCH READY");
		}
	}

	@Override
	public void move(String userId, JSONObject jsoMovimiento) throws Exception {

	}

	public CheckersPiece[] getAlivePieces(String userId) {

		String playerColor = this.players.get(0).getId() == userId ? "BLANCO" : "NEGRO";
		CheckersBoard board = (CheckersBoard) this.getBoard();
		CheckersSquare[][] squares = board.getSquares();
		CheckersPiece[] alivePieces = new CheckersPiece[12];
		int pieceCounter = 0;
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[0].length; j++) {
				if (squares[i][j].getPiece() != null && squares[i][j].getPiece().isAlive() && squares[i][j].getPiece().getColor().equals(playerColor)) {
					alivePieces[pieceCounter] = squares[i][j].getPiece();
					pieceCounter++;
				}
			}
		}

		return alivePieces;

	}

	private CheckersSquare getSquareByPiece(String pieceId, String pieceColor) {
		CheckersBoard board = (CheckersBoard) this.getBoard();
		CheckersSquare[][] squares = board.getSquares();
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[0].length; j++) {
				if (squares[i][j].getPiece() != null && squares[i][j].getPiece().getId() == Integer.parseInt(pieceId) && squares[i][j].getPiece().getColor().equals(pieceColor)) {
					return squares[i][j];
				}
			}
		}
		return null;
	}

	public CheckersBoard getPossibleMovements(String pieceId, String pieceColor) {
		CheckersSquare pieceSquare = getSquareByPiece(pieceId, pieceColor);
		Vector<int[]> targetSquares = pieceSquare.getPossibleMovements();

		CheckersBoard board = (CheckersBoard) this.getBoard();
		CheckersSquare[][] squares = board.getSquares();

		CheckersBoard auxBoard = new CheckersBoard();
		CheckersSquare[][] auxSquares = new CheckersSquare[8][8];

		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[0].length; j++) {
				CheckersSquare auxSquare;
				int[] auxId = { i, j };
				if (isTarget(targetSquares, auxId) && squares[i][j].getPiece() == null) {
					auxSquare = new CheckersSquare(squares[i][j].getId(), "VERDE", squares[i][j].isUpperBorder(), squares[i][j].isLeftBorder(), squares[i][j].isRightBorder(), squares[i][j].isBottomBorder(), squares[i][j].isCoronate(), squares[i][j].getPiece());
				}
				else {
					auxSquare = new CheckersSquare(squares[i][j].getId(), squares[i][j].getColor(), squares[i][j].isUpperBorder(), squares[i][j].isLeftBorder(), squares[i][j].isRightBorder(), squares[i][j].isBottomBorder(), squares[i][j].isCoronate(), squares[i][j].getPiece());
				}
				auxSquares[i][j] = auxSquare;
			}
		}
		auxBoard.setSquares(auxSquares);
		return auxBoard;
	}

	private boolean isTarget(Vector<int[]> targetSquares, int[] auxId) {
		for(int i=0; i<targetSquares.size(); i++) {
			if(targetSquares.get(i)[0] == auxId[0] && targetSquares.get(i)[1] == auxId[1])
				return true;
		}
		return false;
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
