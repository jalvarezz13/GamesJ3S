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
import org.springframework.web.socket.WebSocketSession;

import edu.uclm.esi.tys2122.dao.BattleRepository;
import edu.uclm.esi.tys2122.dao.UserRepository;
import edu.uclm.esi.tys2122.model.Game;
import edu.uclm.esi.tys2122.model.User;
import edu.uclm.esi.tys2122.websockets.WrapperSession;

@Component
public class Manager {

	/* Attributes */

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BattleRepository battleRepo;

	private Vector<Game> games;

	private JSONObject configuration;

	private ConcurrentHashMap<String, HttpSession> httpSessions;

	private ConcurrentHashMap<String, WrapperSession> matchSessionsByHttp;

	private ConcurrentHashMap<String, WrapperSession> matchSessionsByWs;
	
	private Vector<WebSocketSession> chatSessions;

	/* Constructors */

	private Manager() {
		this.games = new Vector<>();
		this.chatSessions = new Vector<>();
		this.httpSessions = new ConcurrentHashMap<>();
		this.matchSessionsByHttp = new ConcurrentHashMap<>();
		this.matchSessionsByWs = new ConcurrentHashMap<>();
		try {
			loadParameters();
			deleteTemporalUsers();
		} catch (Exception e) {
			System.err.println("Error al leer el fichero parametros.txt: " + e.getMessage());
			System.exit(-1);
		}
	}

	private void deleteTemporalUsers() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(30000);
						userRepo.deleteTemporalUserDB();
						Thread.sleep(24 * 60 * 60 * 1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
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
		this.httpSessions.put(session.getId(), session);
	}

	public void add(WrapperSession wrapperSession, String httpSessionId) {
		HttpSession httpSession = this.httpSessions.get(httpSessionId);
		User user = (User) httpSession.getAttribute("user");
		user.setSession(wrapperSession);
		wrapperSession.setHttpSession(httpSession);
		this.matchSessionsByHttp.put(httpSessionId, wrapperSession);
		this.matchSessionsByWs.put(wrapperSession.getWsSession().getId(), wrapperSession);
	}

	public void closeMatchesByWs(String id) {
		User user = (User) matchSessionsByWs.get(id).getHttpSession().getAttribute("user");

		for (Game g : games) { // 2 iteraciones (2 juegos: TicTacToe y Checkers)
			g.closeMatchesByUser(user);
		}

	}
	
	public void addChatSession(WebSocketSession wsSession) {
		this.chatSessions.add(wsSession);
	}

	/* Utilities */

	public Vector<WebSocketSession> getChatSessions() {
		return chatSessions;
	}

	public void setChatSessions(Vector<WebSocketSession> chatSessions) {
		this.chatSessions = chatSessions;
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

	public UserRepository getUserRepo() {
		return userRepo;
	}

	public BattleRepository getBattleRepo() {
		return battleRepo;
	}

	public ConcurrentHashMap<String, WrapperSession> getMatchSessionsPorHttp() {
		return matchSessionsByHttp;
	}

	public void setMatchSessionsPorHttp(ConcurrentHashMap<String, WrapperSession> matchSessionsPorHttp) {
		this.matchSessionsByHttp = matchSessionsPorHttp;
	}

	public ConcurrentHashMap<String, WrapperSession> getMatchSessionsPorWs() {
		return matchSessionsByWs;
	}

	public void setAjedrezSessionsPorWs(ConcurrentHashMap<String, WrapperSession> matchSessionsPorWs) {
		this.matchSessionsByWs = matchSessionsPorWs;
	}

	public void removeChatSession(WebSocketSession wssession) {
		Vector<WebSocketSession> cSessions = this.getChatSessions();
		for(WebSocketSession ws : cSessions) {
			if(ws.getId() == wssession.getId()) {
				cSessions.remove(ws);
				break;
			}
		}
		
	}

}
