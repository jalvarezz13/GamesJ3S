package edu.uclm.esi.tys2122.junit;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import edu.uclm.esi.tys2122.MvcTestCase;

class CompleteCheckersMatchTest extends MvcTestCase{

	private JSONObject jsoJosue, jsoJuan, jsoSergio, jsoJavier;
	private HttpSession sesJosue, sesJuan, sesSergio, sesJavier;
	
	@BeforeEach
	public void setUp() {
		// No se borran por si se usan luego, si no se vuelven a usar borrarlos
		jsoJosue = new JSONObject();
		jsoJosue.put("userName", "josue"); 
		jsoJosue.put("email", "josue@josue.com"); 
		jsoJosue.put("pwd1", "josue123"); 
		jsoJosue.put("pwd2", "josue123"); 
		
		jsoJuan = new JSONObject();
		jsoJuan.put("userName", "juan"); 
		jsoJuan.put("email", "juan@juan.com"); 
		jsoJuan.put("pwd1", "juan123"); 
		jsoJuan.put("pwd2", "juan123");

		jsoSergio = new JSONObject();
		jsoSergio.put("userName", "sergio");
		jsoSergio.put("email", "sergio@sergio.com");
		jsoSergio.put("pwd1", "sergio123");
		jsoSergio.put("pwd2", "sergio123");

		jsoJavier = new JSONObject();
		jsoJavier.put("userName", "javier");
		jsoJavier.put("email", "javier@javier.com");
		jsoJavier.put("pwd1", "javier123");
		jsoJavier.put("pwd2", "javier123");
	}
	
	@Test
	void test() throws Exception{
		registerTest();
		loginTest();
	}
	
	private void registerTest() throws Exception {
		//Correct Register
		doPut("/user/register", null, jsoJosue).andExpect(status().isOk());
		
		//Incorrect Register {Juan has empty fields}
		doPut("/user/register", null, "name", "juan").andExpect(status().is4xxClientError());	
	}

	private void loginTest() throws Exception {
		//Correct Normal Login
		this.sesJosue = doPost("/user/login", null, "name", "josue", "pwd", "josue123").
				andExpect(status().isOk()).andReturn().getRequest().getSession();
		
		//Incorrect Normal Login {Juan used a bad pwd}
		this.sesJuan = doPost("/user/login", null, "name", "juan", "pwd", "juan").
				andExpect(status().is4xxClientError()).andReturn().getRequest().getSession();
		
		// Correct Google Login
		this.sesSergio = doPost("/user/login", null, "name", "sergio", "email", "sergio@sergio.com", "id","6969-6969","type","google").
				andExpect(status().isOk()).andReturn().getRequest().getSession();
		
	}
	
	private void getGamesTest() throws Exception {
	}
	
	private void playGameTest() throws Exception {
	}

}
