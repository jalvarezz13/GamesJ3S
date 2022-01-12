package edu.uclm.esi.tys2122.websockets;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import edu.uclm.esi.tys2122.http.Manager;

@Component
public class WebSocketGenerico extends TextWebSocketHandler {

	@Override
	public void afterConnectionEstablished(WebSocketSession wsSession) throws Exception {
		wsSession.setBinaryMessageSizeLimit(1000 * 1024 * 1024);

		HttpHeaders headers = wsSession.getHandshakeHeaders();
		List<String> cookies = headers.get("cookie");
		String cookie = cookies.get(0);
		String[] tokens = cookie.split(";");
		String httpSessionId = null;

		for (String token : tokens) {
			if (token.contains("JSESSIONID")) {
				int posIgual = token.indexOf('=');
				httpSessionId = token.substring(posIgual + 1).trim();
			}
		}
		WrapperSession matchSession = new WrapperSession(wsSession);
		Manager.get().add(matchSession, httpSessionId);

	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		session.setBinaryMessageSizeLimit(1000 * 1024 * 1024);

		byte[] payload = message.getPayload().array();
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		exception.printStackTrace();
	}

	@SuppressWarnings("unused")
	private void send(WebSocketSession session, Object... typesAndValues) {
		JSONObject jso = new JSONObject();
		int i = 0;
		while (i < typesAndValues.length) {
			jso.put(typesAndValues[i].toString(), typesAndValues[i + 1]);
			i += 2;
		}
		WebSocketMessage<?> wsMessage = new TextMessage(jso.toString());
		try {
			session.sendMessage(wsMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession wssession, CloseStatus status) throws Exception {
		Manager.get().closeMatchesByWs(wssession.getId());

		super.afterConnectionClosed(wssession, status);
	}
}
