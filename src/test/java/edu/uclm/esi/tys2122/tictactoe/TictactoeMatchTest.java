package edu.uclm.esi.tys2122.tictactoe;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import edu.uclm.esi.tys2122.MvcTestCase;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class TictactoeMatchTest extends MvcTestCase {
	
	private JSONObject jsoPepe, jsoAna, jsoLucas;
	
	private HttpSession sessionPepe, sessionAna, sessionLucas;
	
	@BeforeEach
	public void setUp() {
		jsoPepe = new JSONObject();
		jsoPepe.put("userName", "pepe"); jsoPepe.put("email", "pepe@pepe.com"); 
		jsoPepe.put("pwd1", "pepe123"); jsoPepe.put("pwd2", "pepe123"); 
		
		jsoAna = new JSONObject();
		jsoAna.put("userName", "ana"); jsoAna.put("email", "ana@ana.com"); 
		jsoAna.put("pwd1", "ana123"); jsoAna.put("pwd2", "ana123");
		
		jsoLucas = new JSONObject();
		jsoLucas.put("userName", "lucas"); jsoLucas.put("email", "lucas@lucas.com"); 
		jsoLucas.put("pwd1", "lucas123"); jsoLucas.put("pwd2", "lucas123");
	}
	
	@Test
	public void test() throws Exception {
		doPut("/user/register", null, jsoPepe);
		doPut("/user/register", null, jsoAna);
		doPut("/user/register", null, jsoLucas);
		
		this.sessionPepe = doPost("/user/login", null, "name", "pepe", "pwd", "pepe123").
				andReturn().getRequest().getSession();
		this.sessionAna = doPost("/user/login", null, "name", "ana", "pwd", "ana123").
				andReturn().getRequest().getSession();
		this.sessionLucas = doPost("/user/login", null, "name", "lucas", "pwd", "lucas123").
				andReturn().getRequest().getSession();
		
		MvcResult result= doGet("/games/getGames", this.sessionPepe).
				andReturn();
		String response = result.getResponse().getContentAsString();
		JSONArray jsaGames = new JSONArray(response);
		assertTrue(jsaGames.length()==2);
		
		result = doGet("/games/joinGame/Tres en raya", this.sessionPepe).andReturn();
		response = result.getResponse().getContentAsString();
		JSONObject jsoPartidaPepe = new JSONObject(response);
		assertFalse(jsoPartidaPepe.getBoolean("ready"));
		
		
		result = doGet("games/joinGame/Tres en raya", this.sessionAna).andReturn();
		response = result.getResponse().getContentAsString();
		JSONObject jsoPartidaAna = new JSONObject(response);
		
		assertTrue(jsoPartidaPepe.getString("id").equals(jsoPartidaAna.getString("id")));
		assertTrue(jsoPartidaAna.getBoolean("ready"));
		
		System.out.println(jsoPartidaAna.toString());
		String matchId = jsoPartidaAna.getString("id");
		
		HttpSession sessionWithTurn = 
				jsoPartidaAna.getJSONObject("playerWithTurn").getString("name").equals("pepe") ?
						this.sessionPepe : this.sessionAna;
		
		result = doPost("games/move", sessionWithTurn, 
			"matchId", matchId, 
			"x", 1, "y", 1).andReturn();
		
		JSONObject jsoPartida = new JSONObject(result.getResponse().getContentAsString());
		JSONArray jsaBoard = jsoPartida.getJSONObject("board").getJSONArray("squares");
		System.out.println(jsaBoard);
		assertTrue(jsaBoard.getJSONArray(1).getInt(1)== (sessionWithTurn == sessionPepe ? 1 : 2));
		
		sessionWithTurn = changeTurn(sessionWithTurn);
		doPost("games/move", sessionWithTurn, 
			"matchId", matchId, 
			"x", 0, "y", 0).andExpect(status().isOk());
		
		sessionWithTurn = changeTurn(sessionWithTurn);
		doPost("games/move", sessionWithTurn, 
				"matchId", matchId, 
				"x", 0, "y", 2).andExpect(status().isOk());
		
		sessionWithTurn = changeTurn(sessionWithTurn);
		doPost("games/move", sessionWithTurn, 
				"matchId", matchId, 
				"x", 1, "y", 0).andExpect(status().isOk());
		
		sessionWithTurn = changeTurn(sessionWithTurn);
		result = doPost("games/move", sessionWithTurn, 
				"matchId", matchId, 
				"x", 2, "y", 0).andExpect(status().isOk()).andReturn();
		
		jsoPartida = new JSONObject(result.getResponse().getContentAsString());
		assertFalse(jsoPartida.getBoolean("draw"));
		assertNotNull(jsoPartida.getJSONObject("winner"));
		assertNotNull(jsoPartida.getJSONObject("looser"));
	}
	
	private HttpSession changeTurn(HttpSession session) {
		if (session==this.sessionPepe)
			return this.sessionAna;
		return this.sessionPepe;
	}
}
