package edu.uclm.esi.tys2122;

import java.util.List;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class Ajedrez {

	public static void main(String[] args) {
		Board board = new Board();
		
		Move move = new Move(Square.E2, Square.E3);
		List<Move> moves = board.legalMoves();
		System.out.println(moves);
		System.out.println(moves.contains(move));
		board.doMove(move, true);
		System.out.println(board.toString());
	}

}
