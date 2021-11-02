package edu.uclm.esi.tys2122;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public abstract class MvcTestCase {
	@Autowired
	protected MockMvc mvc;
	
	protected ResultActions doPost(String url, HttpSession session, Object... fieldsAndValues) throws Exception {
		url = normalize(url);
		
		JSONObject jso = new JSONObject();
		for (int i=0; i<fieldsAndValues.length; i=i+2)
			jso.put(fieldsAndValues[i].toString(), fieldsAndValues[i+1]);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.
				post(url).
				contentType("application/json").
				content(jso.toString());
		
		if (session!=null)
			request.session((MockHttpSession) session);
		
		return mvc.perform(request);
	}
	
	protected ResultActions doPost(String url, HttpSession session, JSONObject jso) throws Exception {
		url = normalize(url);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.
				post(url).
				contentType("application/json").
				content(jso.toString());
		
		if (session!=null)
			request.session((MockHttpSession) session);
		
		return mvc.perform(request);
	}
	
	protected ResultActions doPut(String url, HttpSession session, Object... fieldsAndValues) throws Exception {
		url = normalize(url);
		
		JSONObject jso = new JSONObject();
		for (int i=0; i<fieldsAndValues.length; i=i+2)
			jso.put(fieldsAndValues[i].toString(), fieldsAndValues[i+1]);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.
			put(url).
			contentType("application/json").
			content(jso.toString());
		
		return mvc.perform(request);
	}
	
	protected ResultActions doPut(String url, HttpSession session, JSONObject jso) throws Exception {
		url = normalize(url);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.
				put(url).
				contentType("application/json").
				content(jso.toString());
		
		if (session!=null)
			request.session((MockHttpSession) session);
		
		return mvc.perform(request);
	}
	
	protected ResultActions doGet(String url, HttpSession session) throws Exception {
		url = normalize(url);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.
				get(url).
				contentType("application/json");
		
		if (session!=null)
			request.session((MockHttpSession) session);
		
		return mvc.perform(request);
	}
	
	protected ResultActions doGet(String url, HttpSession session, Object... fieldsAndValues) throws Exception {
		url = normalize(url);
		
		JSONObject jso = null;
		if (fieldsAndValues.length>0) {
			jso = new JSONObject();
			for (int i=0; i<fieldsAndValues.length; i=i+2)
				jso.put(fieldsAndValues[i].toString(), fieldsAndValues[i+1]);
		}
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.
				get(url).
				contentType("application/json");
		
		if (session!=null)
			request.session((MockHttpSession) session);
		
		if (jso!=null)
			request.content(jso.toString());
		
		return mvc.perform(request);
	}

	private String normalize(String url) {
		if (url.charAt(0)!='/')
			url = '/' + url;
		return url;
	}


}
