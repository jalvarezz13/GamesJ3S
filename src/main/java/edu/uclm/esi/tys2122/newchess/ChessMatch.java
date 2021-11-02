package edu.uclm.esi.tys2122.newchess;

import javax.persistence.Entity;

import org.json.JSONObject;

import edu.uclm.esi.tys2122.model.Board;
import edu.uclm.esi.tys2122.model.Match;

@Entity
public class ChessMatch extends Match {
	public ChessMatch() {
		super();
	}

	@Override
	protected Board newBoard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void checkReady() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(String userId, JSONObject jso) {
		// TODO Auto-generated method stub
		
	}
}
