package edu.uclm.esi.tys2122.websockets;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import edu.uclm.esi.tys2122.http.Manager;

@Component
public class WebSocketGenerico extends TextWebSocketHandler {

	@Override
	public void afterConnectionEstablished(WebSocketSession wsSession) throws Exception {
		wsSession.setBinaryMessageSizeLimit(1000*1024*1024);
		System.out.println(wsSession.getId());
		
		HttpHeaders headers = wsSession.getHandshakeHeaders();
		List<String> cookies = headers.get("cookie");
		
		String cookie = cookies.get(0);
		String[] tokens = cookie.split(";");
		String httpSessionId = null;
		for (String token : tokens) {
			if (token.contains("JSESSIONID")) {
				int posIgual = token.indexOf('=');
				httpSessionId = token.substring(posIgual+1).trim();
			}
		}
		WrapperSession ajedrezSesion = new WrapperSession(wsSession);
		Manager.get().add(ajedrezSesion, httpSessionId);
		
		//saludarDeVezEnCuando(session);
	}
	
	private void saludarDeVezEnCuando(WebSocketSession session) {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						session.sendMessage(new TextMessage("Hola, user agent"));
						Thread.sleep(5000);
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(r).start();
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		session.setBinaryMessageSizeLimit(1000*1024*1024);
		
		byte[] payload = message.getPayload().array();
		System.out.println("La sesión " + session.getId() + " manda un binario de " + payload.length + " bytes");
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		exception.printStackTrace();
	}
	
	private void send(WebSocketSession session, Object... typesAndValues) {
		JSONObject jso = new JSONObject();
		int i=0;
		while (i<typesAndValues.length) {
			jso.put(typesAndValues[i].toString(), typesAndValues[i+1]);
			i+=2;
		}
		WebSocketMessage<?> wsMessage=new TextMessage(jso.toString());
		try {
			session.sendMessage(wsMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
