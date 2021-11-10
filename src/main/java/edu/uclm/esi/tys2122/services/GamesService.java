package edu.uclm.esi.tys2122.services;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import edu.uclm.esi.tys2122.http.Manager;
import edu.uclm.esi.tys2122.model.Game;
import edu.uclm.esi.tys2122.model.Match;

@Service
public class GamesService {
	
	/* Attributes */
	
	private ConcurrentHashMap<String, Match> matches;
	
	/* Constructors */
	
	public GamesService() throws Exception {
		this.matches = new ConcurrentHashMap<>();
		JSONArray jsonReadGame = Manager.get().readFileAsJSONArray("games.txt");
		JSONObject tempGame;
		String name, clazz;
		Game game;
		Manager.get().clearGames();
		for (int i=0; i<jsonReadGame.length(); i++) {
			tempGame = jsonReadGame.getJSONObject(i);
			name = tempGame.getString("name");
			clazz = tempGame.getString("clazz");
			game = (Game) Class.forName(clazz).newInstance();
			game.setName(name);
			Manager.get().add(game);
		}
	}
	
	/* Functions */
	
	public Match newMatch(Class<? extends Match> clazz) throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}
	
	public Match getMatch(String matchId) {
		return this.matches.get(matchId);
	}

	public void put(Match match) {
		this.matches.put(match.getId(), match);
	}

	public List<Game> getGames() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return Manager.get().getGames();
	}

}
