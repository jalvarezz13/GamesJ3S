package edu.uclm.esi.tys2122.junit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Random;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uclm.esi.tys2122.MvcTestCase;

class JupiterTest extends MvcTestCase{
	
	@Test
	void JupTest() throws Exception{
		JSONObject u = registerTest();
		HttpSession sesU = loginTest(u);
		resetPasswordTest(u);
	}

	private JSONObject registerTest() throws Exception {
		JSONObject u = genRandPerson("normal");
		
		//Correct Register {Everything is correct}
		doPut("/user/register", null, 
				"userName", u.getString("name"),
				"email", u.getString("email"),
				"pwd1", u.getString("pwd"),
				"pwd2", u.getString("pwd")
				).andExpect(status().isOk());
		
		//Incorrect Register {Empty fields}
		doPut("/user/register", null, 
				"userName", u.getString("name")
				).andExpect(status().is4xxClientError());
		
		//Incorrect Register {Not matching passwords}
		doPut("/user/register", null, 
				"userName", u.getString("name"),
				"email", u.getString("email"),
				"pwd1", u.getString("pwd"),
				"pwd2", " "
				).andExpect(status().is4xxClientError());
		
		//Incorrect Register {The user already exists}
		doPut("/user/register", null, 
				"userName", u.getString("name"),
				"email", u.getString("email"),
				"pwd1", u.getString("pwd"),
				"pwd2", u.getString("pwd")
				).andExpect(status().is4xxClientError());
		
		return u;
	}

	private HttpSession loginTest(JSONObject u) throws Exception {
		JSONObject g = genRandPerson("google");
		
		//Correct Normal Login {Everything is correct}
		HttpSession sesU = doPost("/user/login", null, 
				"name", u.getString("name"), 
				"pwd", u.getString("pwd")
				).andExpect(status().isOk())
				.andReturn().getRequest().getSession();
		
		// Correct Google Login {Everything is correct}
		doPost("/user/login", null, 
				"name", g.getString("name"), 
				"email", g.getString("email"), 
				"id", g.getString("id"),
				"type", g.getString("type")
				).andExpect(status().isOk());
		
		//Incorrect Normal Login {Incorrect password}
		doPost("/user/login", null, 
				"name", u.getString("name"), 
				"pwd", " "
				).andExpect(status().is4xxClientError());
		
		return sesU;
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
