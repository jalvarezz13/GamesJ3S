package edu.uclm.esi.tys2122.http;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tys2122.model.Email;
import edu.uclm.esi.tys2122.model.User;
import edu.uclm.esi.tys2122.services.UserService;

@RestController
@RequestMapping("user")
public class UserController extends CookiesController {

	/* Attributes */

	@Autowired
	private UserService userService;

	/* Routes */

	@GetMapping(value = "/checkCookie")
	public String checkCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = super.findCookie(request.getCookies());
		if (cookie != null) {
			User user = userService.doLogin(cookie.getValue());
			if (user != null) {
				userService.insertLogin(user, request.getRemoteAddr(), cookie);
				request.getSession().setAttribute("user", user);
				return "games";
			}
		}
		return null;
	}

	@PostMapping(value = "/login")
	public void login(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> credenciales) {
		JSONObject jso = new JSONObject(credenciales);
		if (jso.optString("type").equals("google")) {
			googleLogin(request, response, jso);
		} else {
			classicLogin(request, response, jso);
		}

	}

	private void googleLogin(HttpServletRequest request, HttpServletResponse response, JSONObject jso) {
		String name = jso.getString("name");
		String email = jso.getString("email");
		String id = jso.getString("id");
		String ip = request.getRemoteAddr();

		Optional<User> userDB = Manager.get().getUserRepo().findById(id);
		if (userDB.isPresent()) {
			User user = userDB.get();
			doLogin(request, response, ip, user);
		} else {
			User userByEmail = Manager.get().getUserRepo().findByEmail(email);
			if (userByEmail != null) {
				doLogin(request, response, ip, userByEmail);
			} else {
				User newUser = new User(id, name, email);
				doLogin(request, response, ip, newUser);
			}
		}
	}

	private void classicLogin(HttpServletRequest request, HttpServletResponse response, JSONObject jso) {
		String name = jso.getString("name");
		String pwd = jso.getString("pwd");
		String ip = request.getRemoteAddr();
		User user = userService.doLogin(name, pwd, ip);
		doLogin(request, response, ip, user);
	}

	private void doLogin(HttpServletRequest request, HttpServletResponse response, String ip, User user) {
		Cookie cookie = readOrCreateCookie(request, response);
		user.setCookie(cookie.getValue());
		Manager.get().getUserRepo().save(user);
		userService.insertLogin(user, ip, cookie);
		request.getSession().setAttribute("user", user);
	}

	@PostMapping(value = "/resetPassword")
	public String resetPassword(HttpServletRequest request, @RequestBody Map<String, Object> credenciales) {
		sendEmail(credenciales, "recovery");
		return "Te hemos enviado un mensaje para recuperar tu contraseña";
	}

	@PostMapping(value = "/changePassword")
	public String resetPasswordToken(HttpServletRequest request, @RequestBody Map<String, Object> credenciales) {
		try {
			JSONObject jso = new JSONObject(credenciales);
			String token = jso.getString("token");
			String newPass = jso.getString("newPass");
			String newPass2 = jso.getString("newPass2");

			if (!newPass.equals(newPass2))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden");

			if (newPass.length() < 4)
				throw new ResponseStatusException(HttpStatus.CONFLICT, "La contraseña debe tener al menos cuatro caracteres");

			newPass = org.apache.commons.codec.digest.DigestUtils.sha512Hex(newPass);

			User user = null;
			try {
				user = Manager.get().getUserRepo().findByToken(token);
				Manager.get().getUserRepo().updatePwdById(newPass, user.getId());
				Manager.get().getUserRepo().deleteTokenAfterUse(user.getId());
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay solicitud de cambio de contraseña para esta cuenta");
			}

		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ambos campos son obligatorios");
		}

		return "Se ha recuperado tu contraseña satisfactoria";
	}

	private void sendEmail(Map<String, Object> credenciales, String type) {
		JSONObject applicationData = Manager.get().getConfiguration();
		JSONObject emailDefaultData = Manager.get().getConfiguration().getJSONObject("email");

		JSONObject jso = new JSONObject(credenciales);
		String email = jso.getString("email");

		if (userService.findUserByEmail(email)) {
			Email auxEmail = new Email();
			switch (type) {
			case "register":
				auxEmail.send(email, (String) emailDefaultData.get("registerMsgTopic"), (String) emailDefaultData.get("registerMsgContent"));
				break;
			case "recovery":
				String auxToken = UUID.randomUUID().toString();
				auxEmail.send(email, (String) emailDefaultData.get("recoveryMsgTopic"), (String) emailDefaultData.get("recoveryMsgContent") + (String) applicationData.get("home") + "/" + (String) applicationData.get("changePassword") + "/" + auxToken);
				Manager.get().getUserRepo().setTokenByEmail(auxToken, email);
				break;
			}
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No existe ningún usuario con ese correo");
		}
	}

	@PutMapping("/register")
	@ResponseBody
	public String register(@RequestBody Map<String, Object> credenciales) {
		JSONObject jso = new JSONObject(credenciales);
		String userName = jso.optString("userName");
		String email = jso.optString("email");
		String pwd1 = jso.optString("pwd1");
		String pwd2 = jso.optString("pwd2");
		String type = "normal";

		if (userName.equals("") || email.equals("") || pwd1.equals("") || pwd2.equals(""))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No puede quedar ningún campo vacío");
		if (!pwd1.equals(pwd2))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Las contraseñas no coinciden");
		if (pwd1.length() < 4)
			throw new ResponseStatusException(HttpStatus.CONFLICT, "La contraseña debe tener al menos cuatro caracteres");

		pwd1 = org.apache.commons.codec.digest.DigestUtils.sha512Hex(pwd1);

		try {
			User user = new User();
			user.setName(userName);
			user.setEmail(email);
			user.setPwd(pwd1);
			user.setType(type);

			userService.save(user);

			sendEmail(credenciales, "register");

			return "Registro completado exitosamente";
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
		}
	}
}
