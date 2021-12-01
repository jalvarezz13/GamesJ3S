package edu.uclm.esi.tys2122.http;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CookiesController {

	/* Attributes */

	public final String COOKIE_NAME = "cookieGames";
	public final String COOKIE_PATH = "/";

	/* Functions */

	protected Cookie readOrCreateCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
			return createCookie(response);
		Cookie cookie = findCookie(cookies);
		if (cookie == null)
			cookie = createCookie(response);
		return cookie;
	}

	protected Cookie findCookie(Cookie[] cookies) {
		if (cookies == null)
			return null;
		for (Cookie cookie : cookies)
			if (cookie.getName().equals(COOKIE_NAME))
				return cookie;
		return null;
	}

	private Cookie createCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_NAME, UUID.randomUUID().toString());
		cookie.setPath(COOKIE_PATH);
		cookie.setMaxAge(30 * 24 * 60 * 60);
		response.addCookie(cookie);
		return cookie;
	}

//	public void incrementarContador(HttpServletRequest request, HttpServletResponse response) {
//		Cookie[] cookies = request.getCookies();
//		if (cookies == null) {
//			return;
//		}
//		for (Cookie cookie : cookies) {
//			if (cookie.getName().equals("gamesCounter")) {
//				String value = cookie.getValue();
//				int n = Integer.parseInt(value);
//				n = n + 1;
//				cookie.setValue("" + n);
//				response.addCookie(cookie);
//				return;
//			}
//		}
//	}
}
