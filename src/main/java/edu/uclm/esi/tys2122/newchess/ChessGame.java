package edu.uclm.esi.tys2122.newchess;

import edu.uclm.esi.tys2122.model.Game;
import edu.uclm.esi.tys2122.model.Match;

public class ChessGame extends Game {
	
	public ChessGame() {
		super();
	}

	@Override
	public Match newMatch() {
		return new ChessMatch();
	}
}
