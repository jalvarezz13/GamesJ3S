package edu.uclm.esi.tys2122.tictactoe;

import edu.uclm.esi.tys2122.model.Game;
import edu.uclm.esi.tys2122.model.Match;

public class TictactoeGame extends Game {
	
	public TictactoeGame() {
		super();
	}

	@Override
	public Match newMatch() {
		return new TictactoeMatch();
	}
}
