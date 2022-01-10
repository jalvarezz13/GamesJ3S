package edu.uclm.esi.tys2122.model;

import java.util.List;
import java.util.Vector;

public abstract class Game {

	/* Attributes */

	private String name;
	private List<Match> pendingMatches;
	private List<Match> playingMatches;

	/* Constructors */

	public Game() {
		this.pendingMatches = new Vector<>();
		this.playingMatches = new Vector<>();
	}

	public Game(String name) {
		super();
		this.name = name;
	}

	/* Functions */

	public abstract Match newMatch();

	/* Getters And Setters */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Match> getPendingMatches() {
		return pendingMatches;
	}

	public void setPendingMatches(List<Match> pendingMatches) {
		this.pendingMatches = pendingMatches;
	}

	public List<Match> getPlayingMatches() {
		return playingMatches;
	}

	public void setPlayingMatches(List<Match> playingMatches) {
		this.playingMatches = playingMatches;
	}
	
	public void closeMatchesByUser(User user) {		
		for(Match m : this.pendingMatches) {
			if(m.getPlayers().get(0).getId().equals(user.getId()))
				this.pendingMatches.remove(m);
		}
		
		for(Match m : this.playingMatches) {
			if(m.getPlayers().get(0).getId().equals(user.getId()) || m.getPlayers().get(1).getId().equals(user.getId())) {
				if(m.getWinner() == null)
					m.closeMatchByUser(user);
			}
		}
		
	}


}
