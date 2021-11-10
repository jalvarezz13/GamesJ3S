package edu.uclm.esi.tys2122.checkers;

import edu.uclm.esi.tys2122.model.Game;
import edu.uclm.esi.tys2122.model.Match;

public class CheckersGame extends Game {
	
	public CheckersGame() {
		super();
	}

	@Override
	public Match newMatch() {
		return new CheckersMatch();
	}
}
