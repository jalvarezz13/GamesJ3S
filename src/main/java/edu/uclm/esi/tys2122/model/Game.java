package edu.uclm.esi.tys2122.model;

import java.util.List;
import java.util.Vector;

public abstract class Game {
	private String name;
	
	private List<Match> pendingMatches;
	private List<Match> playingMatches;
	
	public Game() {
		this.pendingMatches = new Vector<>();
		this.playingMatches = new Vector<>();
	}

	public Game(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Match> getPendingMatches() {
		return pendingMatches;
	}
	
	public List<Match> getPlayingMatches() {
		return playingMatches;
	}
	
	public abstract Match newMatch();

}
