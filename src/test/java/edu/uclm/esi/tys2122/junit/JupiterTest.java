package edu.uclm.esi.tys2122.junit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Random;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import static org.junit.jupiter.api.Assertions.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import edu.uclm.esi.tys2122.MvcTestCase;

class JupiterTest extends MvcTestCase{
	
	@Test
	void JupTest() throws Exception{
		JSONObject [] users = registerTest();
		HttpSession [] sessions = loginTest(users[0], users[1]);
		resetPasswordTest(users[0]);
		playGameTest(sessions[0], users[0], sessions[1], users[1]);
	}

	private JSONObject [] registerTest() throws Exception {
		JSONObject [] users = new JSONObject [2];
		JSONObject u = genRandPerson("normal");
		JSONObject u2 = genRandPerson("normal");
		
		// Correct Register {Everything is correct}
		doPut("/user/register", null, 
				"userName", u.getString("name"),
				"email", u.getString("email"),
				"pwd1", u.getString("pwd"),
				"pwd2", u.getString("pwd")
				).andExpect(status().isOk());
		
		// Correct Register {Everything is correct}
		doPut("/user/register", null, 
				"userName", u2.getString("name"),
				"email", u2.getString("email"),
				"pwd1", u2.getString("pwd"),
				"pwd2", u2.getString("pwd")
				).andExpect(status().isOk());
		
		// Incorrect Register {Empty fields}
		doPut("/user/register", null, 
				"userName", u.getString("name")
				).andExpect(status().is4xxClientError());
		
		// Incorrect Register {Not matching passwords}
		doPut("/user/register", null, 
				"userName", u.getString("name"),
				"email", u.getString("email"),
				"pwd1", u.getString("pwd"),
				"pwd2", " "
				).andExpect(status().is4xxClientError());
		
		// Incorrect Register {The user already exists}
		doPut("/user/register", null, 
				"userName", u.getString("name"),
				"email", u.getString("email"),
				"pwd1", u.getString("pwd"),
				"pwd2", u.getString("pwd")
				).andExpect(status().is4xxClientError());
		
		users[0] = u;
		users[1] = u2;
		return users;
	}

	private HttpSession [] loginTest(JSONObject u, JSONObject u2) throws Exception {
		HttpSession [] sessions = new HttpSession [2];
		JSONObject g = genRandPerson("google");
		
		// Correct Normal Login {Everything is correct}
		HttpSession sesU = doPost("/user/login", null, 
				"name", u.getString("name"), 
				"pwd", u.getString("pwd")
				).andExpect(status().isOk())
				.andReturn().getRequest().getSession();
		
		// Correct Normal Login {Everything is correct}
		HttpSession sesU2 = doPost("/user/login", null, 
				"name", u2.getString("name"), 
				"pwd", u2.getString("pwd")
				).andExpect(status().isOk())
				.andReturn().getRequest().getSession();
		
		// Correct Google Login {Everything is correct}
		doPost("/user/login", null, 
				"name", g.getString("name"), 
				"email", g.getString("email"), 
				"id", g.getString("id"),
				"type", g.getString("type")
				).andExpect(status().isOk());
		
		// Incorrect Normal Login {Incorrect password}
		doPost("/user/login", null, 
				"name", u.getString("name"), 
				"pwd", " "
				).andExpect(status().is4xxClientError());
		
		sessions[0] = sesU;
		sessions[1] = sesU2;
		
		return sessions;
	}
	
	private void resetPasswordTest(JSONObject u) throws Exception {
		
		// Correct Reset Password {Everything is correct}
		doPost("/user/resetPassword", null, 
				"email", u.getString("email")
				).andExpect(status().isOk());
		
		// Incorrect Reset Password {No user has that email}
		doPost("/user/resetPassword", null, 
				"email", "error@error.com"
				).andExpect(status().is4xxClientError());
		
	}
	
	private void playGameTest(HttpSession sesU, JSONObject u, HttpSession sesU2, JSONObject u2) throws Exception{
		String response;
		
		// Correct Get Games {Everything is correct}
		response = doGet("/games/getGames", sesU).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		JSONArray jsaGames = new JSONArray(response);
		assertTrue(jsaGames.length() == 2);
		
		// Correct Join Game {Everything is correct}
		response = doGet("/games/joinGame/Las damas&null", sesU).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		JSONObject matchU = new JSONObject(response);
		
		// {Check if the parameters are correctly set}
		assertEquals("CheckersMatch", matchU.getString("game"));
		assertFalse(matchU.getBoolean("ready"));
		assertEquals(1, matchU.getJSONArray("players").length());
		
		// {Check if the parameters are correctly set to begin the match}
		response = doGet("games/joinGame/Las damas&null", sesU2).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		JSONObject matchU2 = new JSONObject(response);
		assertEquals("CheckersMatch", matchU2.getString("game"));
		assertTrue(matchU2.getBoolean("ready"));
		assertEquals(2, matchU2.getJSONArray("players").length());
		assertTrue(matchU.getString("id").equals(matchU2.getString("id")));
		
		// Start moving in the game		
		// {Wrong turn}
		response = doPost("games/move", sesU2, 
				"pieceId", "1", 
				"pieceColor", "NEGRO",
				"movementX", "4",
				"movementY", "2",
				"direction", "RightUp",
				"matchId", matchU2.getString("id"))
				.andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();
		
		// {Impossible move piece}-->es imposible mover, entonces creo k no se puede comprobar
//		response = doPost("games/move", sesU, 
//				"pieceId", "8", 
//				"pieceColor", "BLANCO",
//				"movementX", "",
//				"movementY", "",
//				"direction", "",
//				"matchId", matchU2.getString("id"))
//				.andExpect(status().is4xxClientError())
//				.andReturn().getResponse().getContentAsString();
		
		// {Correct move}
		response = doPost("games/move", sesU, 
				"pieceId", "1", 
				"pieceColor", "BLANCO",
				"movementX", "3",
				"movementY", "5",
				"direction", "RightUp",
				"matchId", matchU2.getString("id"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		matchU = new JSONObject(response);
		assertTrue(matchU.getJSONObject("playerWithTurn").getString("name").equals(u2.get("name")));
	}
	
 	private JSONObject genRandPerson(String type) {
		Random rand = new Random();
		String rndValue = String.valueOf(rand.nextInt(999));
		JSONObject jso = new JSONObject();
		
		String id = UUID.randomUUID().toString();
		String name = "jupiter" + rndValue;
		String email = name + "@" + name + ".com";
		String pwd = name + "pass";

		jso.put("id", id);
		jso.put("name", name);
		jso.put("email", email);
		jso.put("pwd", pwd);
		jso.put ("type", type);
		
		return jso;
	}
}
