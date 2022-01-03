package edu.uclm.esi.tys2122.checkers;

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
	private int[][] possibleMovementsXY = { null, null, null, null };

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

		CheckersSquare actualSquare = getSquareByPiece(movementData.getString("pieceId"),
				movementData.getString("pieceColor"));
		int[] nextSquare = { Integer.parseInt(movementData.getString("movementX")),
				Integer.parseInt(movementData.getString("movementY")) };
		
		CheckersBoard board = (CheckersBoard) this.getBoard();
		CheckersSquare[][] squares = board.getSquares();
		
		//Kill piece
		if(Math.abs(nextSquare[0] - actualSquare.getId()[0]) >= 2 || Math.abs(nextSquare[1] - actualSquare.getId()[1]) >= 2) {
			int [] killedPiecePosition = killPiece(movementData, actualSquare, nextSquare);
			squares[killedPiecePosition[0]][killedPiecePosition[1]].getPiece().die();
		}

		// DO QUEEN
		if (movementData.getString("pieceColor").equals("BLANCO")) {
			if (squares[nextSquare[0]][nextSquare[1]].isBottomBorder())
				actualSquare.getPiece().promote();
		} else {
			if (squares[nextSquare[0]][nextSquare[1]].isUpperBorder())
				actualSquare.getPiece().promote();
		}

		changeMatch(actualSquare.getId(), nextSquare);

		this.playerWithTurn = this.getPlayerWithTurn() == this.getPlayers().get(0) ? this.getPlayers().get(1)
				: this.getPlayers().get(0);

		this.cleanMovements();

		super.notifyOponents("MATCH UPDATE");
		;
	}

	/*
	 * Given the initial and destination squares, calculate the position of the killed piece
	 */
	private int[] killPiece(JSONObject movementData, CheckersSquare actualSquare, int[] nextSquare) {
		int[] positionKilledPiece = new int[2];
		
		if(actualSquare.getPiece().getColor().equals("BLANCO")) {
			switch(movementData.getString("direction")) {
				case "leftUp":
					positionKilledPiece[0] = actualSquare.getId()[0] + 1; 
					positionKilledPiece[1] = actualSquare.getId()[1] + 1;
					break;
				case "rightUp":
					positionKilledPiece[0] = actualSquare.getId()[0] + 1; 
					positionKilledPiece[1] = actualSquare.getId()[1] - 1;
					break;
				case "rightDown":
					positionKilledPiece[0] = actualSquare.getId()[0] - 1; 
					positionKilledPiece[1] = actualSquare.getId()[1] - 1;
					break;
				case "leftDown":
					positionKilledPiece[0] = actualSquare.getId()[0] - 1; 
					positionKilledPiece[1] = actualSquare.getId()[1] + 1;
					break;
			}
		}else { //NEGRO
			switch(movementData.getString("direction")) {
				case "leftUp":
					positionKilledPiece[0] = actualSquare.getId()[0] - 1; 
					positionKilledPiece[1] = actualSquare.getId()[1] - 1;
					break;
				case "rightUp":
					positionKilledPiece[0] = actualSquare.getId()[0] - 1; 
					positionKilledPiece[1] = actualSquare.getId()[1] + 1;
					break;
				case "rightDown":
					positionKilledPiece[0] = actualSquare.getId()[0] + 1; 
					positionKilledPiece[1] = actualSquare.getId()[1] + 1;
					break;
				case "leftDown":
					positionKilledPiece[0] = actualSquare.getId()[0] + 1; 
					positionKilledPiece[1] = actualSquare.getId()[1] - 1;
					break;
			}
		}
		
		return positionKilledPiece;
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
				if (squares[i][j].getPiece() != null && squares[i][j].getPiece().isAlive()
						&& squares[i][j].getPiece().getColor().equals(playerColor)) {
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
				if (squares[i][j].getPiece() != null && squares[i][j].getPiece().getId() == Integer.parseInt(pieceId)
						&& squares[i][j].getPiece().getColor().equals(pieceColor)) {
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
		int[][] targetSquares = getNextMovements(pieceSquare);

		setPossibleMovementsXY(targetSquares);

		boolean[] nextMovements = calculatePossibleMovements(targetSquares);

		CheckersBoard board = (CheckersBoard) this.getBoard();
		CheckersSquare[][] squares = board.getSquares();

		CheckersBoard auxBoard = new CheckersBoard();
		CheckersSquare[][] auxSquares = new CheckersSquare[8][8];

		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[0].length; j++) {
				CheckersSquare auxSquare;
				int[] auxId = { i, j };
				if (isTarget(targetSquares, auxId) && (squares[i][j].getPiece() == null || !squares[i][j].getPiece().isAlive())) {
					auxSquare = new CheckersSquare(squares[i][j].getId(), "VERDE", squares[i][j].isUpperBorder(),
							squares[i][j].isLeftBorder(), squares[i][j].isRightBorder(), squares[i][j].isBottomBorder(),
							squares[i][j].isCoronate(), squares[i][j].getPiece());
				} else {
					auxSquare = new CheckersSquare(squares[i][j].getId(), squares[i][j].getColor(),
							squares[i][j].isUpperBorder(), squares[i][j].isLeftBorder(), squares[i][j].isRightBorder(),
							squares[i][j].isBottomBorder(), squares[i][j].isCoronate(), squares[i][j].getPiece());
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
	private int[][] getNextMovements(CheckersSquare actualSquare) {
		CheckersPiece piece = actualSquare.getPiece();
		CheckersBoard board = (CheckersBoard) this.getBoard();
		CheckersSquare[][] squares = board.getSquares();

		int[][] auxPossibles = new int[4][];
		if (piece.getType() == "pawn") { // PAWN
			if (piece.getColor().equals("BLANCO")) { // BLANCO
				if (!actualSquare.isRightBorder()) { // leftUp?
					CheckersSquare leftUpSquare = squares[actualSquare.getId()[0] + 1][actualSquare.getId()[1] + 1];
					if (leftUpSquare.getPiece() == null || !leftUpSquare.getPiece().isAlive()) {
						auxPossibles[0] = leftUpSquare.getId();
					} else {
						if (leftUpSquare.getPiece().getColor() != actualSquare.getPiece().getColor()) {
							if (!leftUpSquare.isBorder()) {
								CheckersSquare nextLeftUpSquare = squares[leftUpSquare.getId()[0]
										+ 1][leftUpSquare.getId()[1] + 1];
								if (nextLeftUpSquare.getPiece() == null) {
									auxPossibles[0] = nextLeftUpSquare.getId();
								}
							}
						}
					}
				}
				if (!actualSquare.isLeftBorder()) { // rightUp?
					CheckersSquare rightUpSquare = squares[actualSquare.getId()[0] + 1][actualSquare.getId()[1] - 1];
					if (rightUpSquare.getPiece() == null || !rightUpSquare.getPiece().isAlive()) {
						auxPossibles[1] = rightUpSquare.getId();
					} else {
						if (rightUpSquare.getPiece().getColor() != actualSquare.getPiece().getColor()) {
							if (!rightUpSquare.isBorder()) {
								CheckersSquare nextRightUpSquare = squares[rightUpSquare.getId()[0]
										+ 1][rightUpSquare.getId()[1] - 1];
								if (nextRightUpSquare.getPiece() == null) {
									auxPossibles[1] = nextRightUpSquare.getId();
								}
							}
						}
					}
				}
			} else { // NEGRO
				if (!actualSquare.isLeftBorder()) { // leftUp?
					CheckersSquare leftUpSquare = squares[actualSquare.getId()[0] - 1][actualSquare.getId()[1] - 1];
					if (leftUpSquare.getPiece() == null || !leftUpSquare.getPiece().isAlive()) {
						auxPossibles[0] = leftUpSquare.getId();
					} else {
						if (leftUpSquare.getPiece().getColor() != actualSquare.getPiece().getColor()) {
							if (!leftUpSquare.isBorder()) {
								CheckersSquare nextLeftUpSquare = squares[leftUpSquare.getId()[0]
										- 1][leftUpSquare.getId()[1] - 1];
								if (nextLeftUpSquare.getPiece() == null) {
									auxPossibles[0] = nextLeftUpSquare.getId();
								}
							}
						}
					}
				}
				if (!actualSquare.isRightBorder()) { // rightUp?
					CheckersSquare rightUpSquare = squares[actualSquare.getId()[0] - 1][actualSquare.getId()[1] + 1];
					if (rightUpSquare.getPiece() == null || !rightUpSquare.getPiece().isAlive()) {
						auxPossibles[1] = rightUpSquare.getId();
					} else {
						if (rightUpSquare.getPiece().getColor() != actualSquare.getPiece().getColor()) {
							if (!rightUpSquare.isBorder()) {
								CheckersSquare nextRightUpSquare = squares[rightUpSquare.getId()[0]
										- 1][rightUpSquare.getId()[1] + 1];
								if (nextRightUpSquare.getPiece() == null) {
									auxPossibles[1] = nextRightUpSquare.getId();
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
	 * destination squares TODO: Se puede simplificar (CREO) ya que 0, 1, 2, 3 se
	 * hace en sentido de agujas de reloj
	 */
	private boolean[] calculatePossibleMovements(int[][] targetSquares) {
		boolean nextMovements[] = { false, false, false, false };

		for (int i = 0; i < targetSquares.length; i++)
			if (targetSquares[i] != null)
				nextMovements[i] = true;

		return nextMovements;
	}

	private boolean isTarget(int[][] targetSquares, int[] auxId) {
		for (int i = 0; i < targetSquares.length; i++) {
			if (targetSquares[i] != null)
				if (targetSquares[i][0] == auxId[0] && targetSquares[i][1] == auxId[1])
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

	public void setPossibleMovementsXY(int[][] targetSquares) {
		this.possibleMovementsXY = targetSquares;
	}

	public void cleanMovements() {
		boolean[] nextMovements = { false, false, false, false };
		this.possibleMovements = nextMovements;

		int[][] possibleMovementsXY = new int[4][];
		this.possibleMovementsXY = possibleMovementsXY;
	}
}
