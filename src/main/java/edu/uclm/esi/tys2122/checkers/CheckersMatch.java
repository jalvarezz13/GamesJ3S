package edu.uclm.esi.tys2122.checkers;

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
	private boolean[] possibleMovements = { false, false, false, false };
	private int[][] possibleMovementsXY = new int[4][];

	/* Functions */

	@Override
	protected Board newBoard() {
		return new CheckersBoard();
	}

	@Override
	protected void checkReady() {
		this.ready = this.players.size() == 2;
		if (this.ready) {
			this.playerWithTurn = this.players.get(0);
			super.notifyOponents("MATCH READY");
		}
	}

	@Override
	public void move(String userId, JSONObject movementData) throws Exception {
		if (!this.getPlayerWithTurn().getId().equals(userId))
			throw new Exception("No es tu turno");

		if (!this.ready)
			throw new Exception("Esperando jugadores, aún no puede mover");

		CheckersSquare actualSquare = getSquareByPiece(movementData.getString("pieceId"), movementData.getString("pieceColor"));
		int[] nextSquare = { Integer.parseInt(movementData.getString("movementX")), Integer.parseInt(movementData.getString("movementY")) };

		// DO QUEEN
		CheckersBoard board = (CheckersBoard) this.getBoard();
		CheckersSquare[][] squares = board.getSquares();
		if (movementData.getString("pieceColor").equals("BLANCO")) {
			if (squares[nextSquare[0]][nextSquare[1]].isBottomBorder())
				actualSquare.getPiece().promote();
		} else {
			if (squares[nextSquare[0]][nextSquare[1]].isUpperBorder())
				actualSquare.getPiece().promote();
		}

		changeMatch(actualSquare.getId(), nextSquare);

		this.playerWithTurn = this.getPlayerWithTurn() == this.getPlayers().get(0) ? this.getPlayers().get(1) : this.getPlayers().get(0);

		this.cleanMovements();

		super.notifyOponents("MATCH UPDATE");
		;
	}

	/*
	 * Given a square, move the piece from each to another given too
	 */
	private void changeMatch(int[] actualSquare, int[] nextSquare) {
		CheckersBoard board = (CheckersBoard) this.getBoard();
		CheckersSquare[][] squares = board.getSquares();
		CheckersPiece pieceToMove = squares[actualSquare[0]][actualSquare[1]].getPiece();
		squares[actualSquare[0]][actualSquare[1]].setPiece(null);
		squares[nextSquare[0]][nextSquare[1]].setPiece(pieceToMove);
	}

	/*
	 * Returns all alive pieces of each player. It is used in select of frontend
	 */

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

	/*
	 * Given a piece, find where it is
	 */
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

	/*
	 * Draw green each possible destination square
	 */
	public CheckersMatch getPossibleMovements(String pieceId, String pieceColor) {
		CheckersSquare pieceSquare = getSquareByPiece(pieceId, pieceColor);
		Vector<int[]> targetSquares = getNextMovements(pieceSquare);

		setPossibleMovementsXY(targetSquares);

		boolean[] nextMovements = calculatePossibleMovements(pieceSquare.getId(), targetSquares, pieceColor);

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
				} else {
					auxSquare = new CheckersSquare(squares[i][j].getId(), squares[i][j].getColor(), squares[i][j].isUpperBorder(), squares[i][j].isLeftBorder(), squares[i][j].isRightBorder(), squares[i][j].isBottomBorder(), squares[i][j].isCoronate(), squares[i][j].getPiece());
				}
				auxSquares[i][j] = auxSquare;
			}
		}
		auxBoard.setSquares(auxSquares);

		CheckersMatch auxMatch = new CheckersMatch();
		auxMatch.setBoard(auxBoard);
		auxMatch.setPossibleMovements(nextMovements);
		auxMatch.setPossibleMovementsXY(targetSquares);

		cleanMovements();
		return auxMatch;
	}

	/*
	 * Calculates the next squares following game rules
	 */
	private Vector<int[]> getNextMovements(CheckersSquare actualSquare) {
		CheckersPiece piece = actualSquare.getPiece();
		CheckersBoard board = (CheckersBoard) this.getBoard();
		CheckersSquare[][] squares = board.getSquares();

		Vector<int[]> auxPossibles = new Vector<int[]>();
		if (piece.getType() == "pawn") { // PAWN
			if (piece.getColor().equals("BLANCO")) { // BLANCO
				if (!actualSquare.isRightBorder()) { // leftUp?
					CheckersSquare leftUpSquare = squares[actualSquare.getId()[0] + 1][actualSquare.getId()[1] + 1];
					if (leftUpSquare.getPiece() == null) {
						auxPossibles.add(leftUpSquare.getId());
					} else {
						if (leftUpSquare.getPiece().getColor() != actualSquare.getPiece().getColor()) {
							if (!leftUpSquare.isBorder()) {
								CheckersSquare nextLeftUpSquare = squares[leftUpSquare.getId()[0] + 1][leftUpSquare.getId()[1] + 1];
								if (nextLeftUpSquare.getPiece() == null) {
									auxPossibles.add(nextLeftUpSquare.getId());
								}
							}
						}
					}
				}
				if (!actualSquare.isLeftBorder()) { // rightUp?
					CheckersSquare rightUpSquare = squares[actualSquare.getId()[0] + 1][actualSquare.getId()[1] - 1];
					if (rightUpSquare.getPiece() == null) {
						auxPossibles.add(rightUpSquare.getId());
					} else {
						if (rightUpSquare.getPiece().getColor() != actualSquare.getPiece().getColor()) {
							if (!rightUpSquare.isBorder()) {
								CheckersSquare nextRightUpSquare = squares[rightUpSquare.getId()[0] + 1][rightUpSquare.getId()[1] - 1];
								if (nextRightUpSquare.getPiece() == null) {
									auxPossibles.add(nextRightUpSquare.getId());
								}
							}
						}
					}
				}
			} else { // NEGRO
				if (!actualSquare.isLeftBorder()) { // leftUp?
					CheckersSquare leftUpSquare = squares[actualSquare.getId()[0] - 1][actualSquare.getId()[1] - 1];
					if (leftUpSquare.getPiece() == null) {
						auxPossibles.add(leftUpSquare.getId());
					} else {
						if (leftUpSquare.getPiece().getColor() != actualSquare.getPiece().getColor()) {
							if (!leftUpSquare.isBorder()) {
								CheckersSquare nextLeftUpSquare = squares[leftUpSquare.getId()[0] - 1][leftUpSquare.getId()[1] - 1];
								if (nextLeftUpSquare.getPiece() == null) {
									auxPossibles.add(nextLeftUpSquare.getId());
								}
							}
						}
					}
				}
				if (!actualSquare.isRightBorder()) { // rightUp?
					CheckersSquare rightUpSquare = squares[actualSquare.getId()[0] - 1][actualSquare.getId()[1] + 1];
					if (rightUpSquare.getPiece() == null) {
						auxPossibles.add(rightUpSquare.getId());
					} else {
						if (rightUpSquare.getPiece().getColor() != actualSquare.getPiece().getColor()) {
							if (!rightUpSquare.isBorder()) {
								CheckersSquare nextRightUpSquare = squares[rightUpSquare.getId()[0] - 1][rightUpSquare.getId()[1] + 1];
								if (nextRightUpSquare.getPiece() == null) {
									auxPossibles.add(nextRightUpSquare.getId());
								}
							}
						}
					}
				}
			}
		} else { // CHECKER
			// TODO
		}
		return auxPossibles;
	}

	/*
	 * Calculates the direction according to the current position and the possible
	 * destination squares
	 * TODO: Se puede simplificar (CREO) ya que 0, 1, 2, 3 se hace en sentido de agujas de reloj
	 */
	private boolean[] calculatePossibleMovements(int[] actualSquare, Vector<int[]> nextSquares, String pieceColor) {
		boolean nextMovements[] = { false, false, false, false };
		for (int i = 0; i < nextSquares.size(); i++) {
			if (pieceColor.equals("BLANCO")) {
				if (nextSquares.get(i)[0] > actualSquare[0] && nextSquares.get(i)[1] > actualSquare[1]) {
					nextMovements[0] = true;
				} else if (nextSquares.get(i)[0] > actualSquare[0] && nextSquares.get(i)[1] < actualSquare[1]) {
					nextMovements[1] = true;
				} else if (nextSquares.get(i)[0] < actualSquare[0] && nextSquares.get(i)[1] < actualSquare[1]) {
					nextMovements[2] = true;
				} else if (nextSquares.get(i)[0] < actualSquare[0] && nextSquares.get(i)[1] > actualSquare[1]) {
					nextMovements[3] = true;
				}
			} else {
				if (nextSquares.get(i)[0] < actualSquare[0] && nextSquares.get(i)[1] < actualSquare[1]) {
					nextMovements[0] = true;
				} else if (nextSquares.get(i)[0] < actualSquare[0] && nextSquares.get(i)[1] > actualSquare[1]) {
					nextMovements[1] = true;
				} else if (nextSquares.get(i)[0] > actualSquare[0] && nextSquares.get(i)[1] > actualSquare[1]) {
					nextMovements[2] = true;
				} else if (nextSquares.get(i)[0] > actualSquare[0] && nextSquares.get(i)[1] < actualSquare[1]) {
					nextMovements[3] = true;
				}
			}
		}

		return nextMovements;
	}

	private boolean isTarget(Vector<int[]> targetSquares, int[] auxId) {
		for (int i = 0; i < targetSquares.size(); i++) {
			if (targetSquares.get(i)[0] == auxId[0] && targetSquares.get(i)[1] == auxId[1])
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

	public boolean[] getPossibleMovements() {
		return possibleMovements;
	}

	public void setPossibleMovements(boolean[] possibleMovements) {
		this.possibleMovements = possibleMovements;
	}

	public int[][] getPossibleMovementsXY() {
		return possibleMovementsXY;
	}

	public void setPossibleMovementsXY(Vector<int[]> possibleMovementsXY) {
		for (int i = 0; i < possibleMovementsXY.size(); i++)
			this.possibleMovementsXY[i] = possibleMovementsXY.get(i);
	}

	public void cleanMovements() {
		boolean[] nextMovements = { false, false, false, false };
		this.possibleMovements = nextMovements;

		int[][] possibleMovementsXY = new int[4][];
		this.possibleMovementsXY = possibleMovementsXY;
	}
}
