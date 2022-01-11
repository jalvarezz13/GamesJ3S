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
		response = doGet("/games/joinGame/Las damas", sesU).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		JSONObject matchU = new JSONObject(response);
		
		// {Check if the parameters are correctly set}
		assertEquals("CheckersMatch", matchU.getString("game"));
		assertFalse(matchU.getBoolean("ready"));
		assertEquals(1, matchU.getJSONArray("players").length());
		
		
		response = doGet("games/joinGame/Tres en raya", this.sessionAna).andReturn().getResponse().getContentAsString();
		JSONObject jsoPartidaAna = new JSONObject(response);
		
		assertTrue(jsoPartidaPepe.getString("id").equals(jsoPartidaAna.getString("id")));
		assertTrue(jsoPartidaAna.getBoolean("ready"));
		
		System.out.println(jsoPartidaAna.toString());
		String matchId = jsoPartidaAna.getString("id");
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
