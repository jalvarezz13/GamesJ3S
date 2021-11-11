package edu.uclm.esi.tys2122.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import edu.uclm.esi.tys2122.dao.UserRepository;
import edu.uclm.esi.tys2122.model.Game;
import edu.uclm.esi.tys2122.model.User;
import edu.uclm.esi.tys2122.websockets.WrapperSession;

@Component
public class Manager {
	
	/* Attributes */
	
	@Autowired
	private UserRepository userRepo;
	
	private Vector<Game> games;
	
	private JSONObject configuration;

	private ConcurrentHashMap<String, HttpSession> httpSessions;

	private ConcurrentHashMap<String, WrapperSession> ajedrezSessionsPorHttp;

	private ConcurrentHashMap<String, WrapperSession> ajedrezSessionsPorWs;
	
	/* Constructors */
	
	private Manager() {
		this.games = new Vector<>();
		this.httpSessions = new ConcurrentHashMap<>();
		this.ajedrezSessionsPorHttp = new ConcurrentHashMap<>();
		this.ajedrezSessionsPorWs = new ConcurrentHashMap<>();
		try {
			loadParameters();
		} catch (Exception e) {
			System.err.println("Error al leer el fichero parametros.txt: " + e.getMessage());
			System.exit(-1);
		}
	}
	
	/* Singleton */
	
	private static class ManagerHolder {
		static Manager singleton = new Manager();
	}
	
	@Bean
	public static Manager get() {
		return ManagerHolder.singleton;
	}
	
	/* Functions */
	
	public Game findGame(String gameName) {
		for (Game game : this.games)
			if (game.getName().equals(gameName))
				return game;
		return null;
	}
	
	public void add(Game game) {
		this.games.add(game);
	}
	
	public void add(HttpSession session) {
		this.httpSessions.put(session.getId(),session);
	}

	public void add(WrapperSession wrapperSession, String httpSessionId) {
		HttpSession httpSession = this.httpSessions.get(httpSessionId);
		String userId = httpSession.getAttribute("userId").toString();
		User user = userRepo.findById(userId).get();
		user.setSession(wrapperSession);
		wrapperSession.setHttpSession(httpSession);
		this.ajedrezSessionsPorHttp.put(httpSessionId, wrapperSession);
		this.ajedrezSessionsPorWs.put(wrapperSession.getWsSession().getId(), wrapperSession);
		
	}
	
	/* Utilities */
	
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

	/* Getters And Setters */
	
	public Vector<Game> getGames() {
		return games;
	}
	
	public void setGames(Vector<Game> games) {
		this.games = games;
	}

	public JSONObject getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(JSONObject configuration) {
		this.configuration = configuration;
	}
	
	public ConcurrentHashMap<String, HttpSession> getHttpSessions() {
		return httpSessions;
	}

	public void setHttpSessions(ConcurrentHashMap<String, HttpSession> httpSessions) {
		this.httpSessions = httpSessions;
	}

	public ConcurrentHashMap<String, WrapperSession> getAjedrezSessionsPorHttp() {
		return ajedrezSessionsPorHttp;
	}

	public void setAjedrezSessionsPorHttp(ConcurrentHashMap<String, WrapperSession> ajedrezSessionsPorHttp) {
		this.ajedrezSessionsPorHttp = ajedrezSessionsPorHttp;
	}

	public ConcurrentHashMap<String, WrapperSession> getAjedrezSessionsPorWs() {
		return ajedrezSessionsPorWs;
	}

	public void setAjedrezSessionsPorWs(ConcurrentHashMap<String, WrapperSession> ajedrezSessionsPorWs) {
		this.ajedrezSessionsPorWs = ajedrezSessionsPorWs;
	}

}
