package edu.uclm.esi.tys2122.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import edu.uclm.esi.tys2122.model.Game;

@Component
public class Manager {
	
	private Vector<Game> games;
	
	private JSONObject configuration;

	private Manager() {
		this.games = new Vector<>();
		try {
			loadParameters();
		} catch (Exception e) {
			System.err.println("Error al leer el fichero parametros.txt: " + e.getMessage());
			System.exit(-1);
		}
	}
	
	private static class ManagerHolder {
		static Manager singleton=new Manager();
	}
	
	@Bean
	public static Manager get() {
		return ManagerHolder.singleton;
	}

	public JSONObject getConfiguration() {
		return configuration;
	}
	
	private void loadParameters() throws IOException {
		this.configuration = read("./parametros.txt");
	}
	
	private JSONObject read(String fileName) throws IOException {
		 ClassLoader classLoader = getClass().getClassLoader();
		 try (InputStream fis = classLoader.getResourceAsStream(fileName)) {
			byte[] b = new byte[fis.available()];
			fis.read(b);
			String s = new String(b);
			return new JSONObject(s);
		 }
	}
	
	public JSONArray readFileAsJSONArray(String fileName) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		 try (InputStream fis = classLoader.getResourceAsStream(fileName)) {
			byte[] b = new byte[fis.available()];
			fis.read(b);
			String s = new String(b);
			return new JSONArray(s);
		 }
	}

	public void clearGames() {
		this.games.clear();
	}

	public void add(Game game) {
		this.games.add(game);
	}
	
	public Vector<Game> getGames() {
		return games;
	}

	public Game findGame(String gameName) {
		for (Game game : this.games)
			if (game.getName().equals(gameName))
				return game;
		return null;
	}
}
