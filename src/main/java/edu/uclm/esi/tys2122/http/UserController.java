package edu.uclm.esi.tys2122.http;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tys2122.dao.UserRepository;
import edu.uclm.esi.tys2122.model.Email;
import edu.uclm.esi.tys2122.model.User;
import edu.uclm.esi.tys2122.services.UserService;

@RestController
@RequestMapping("user")
public class UserController extends CookiesController {

	/* Attributes */

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepo;

	/* Routes */

	@GetMapping(value = "/checkCookie")
	public String checkCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = super.findCookie(request.getCookies());
		if (cookie != null) {
//			super.incrementarContador(request, response);
			User user = userService.doLogin(cookie.getValue());
			if (user != null) {
				userService.insertLogin(user, request.getRemoteAddr(), cookie);
				request.getSession().setAttribute("user", user);
				return "games";
//				try {
//					response.sendRedirect("http://localhost/?ojr=games");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
		return null;
	}

	@PostMapping(value = "/login")
	public void login(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> credenciales) {
		JSONObject jso = new JSONObject(credenciales);
		if (jso.optString("type").length() > 0) {
			googleLogin(request, response, jso);
		} else {
			classicLogin(request, response, jso);
		}

	}

	private void googleLogin(HttpServletRequest request, HttpServletResponse response, JSONObject jso) {
		// TODO Auto-generated method stub

	}

	private void classicLogin(HttpServletRequest request, HttpServletResponse response, JSONObject jso) {
		String name = jso.getString("name");
		String pwd = jso.getString("pwd");
		String ip = request.getRemoteAddr();

		User user = userService.doLogin(name, pwd, ip);

		Cookie cookie = readOrCreateCookie(request, response);
		user.setCookie(cookie.getValue());
		userRepo.save(user);
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
				auxEmail.send(email, (String) emailDefaultData.get("registerMsgTopic"),
						(String) emailDefaultData.get("registerMsgContent"));
				break;
			case "recovery":
				String auxToken = UUID.randomUUID().toString();
				auxEmail.send(email, (String) emailDefaultData.get("recoveryMsgTopic"),
						(String) emailDefaultData.get("recoveryMsgContent") + (String) applicationData.get("home") + "/" + (String) applicationData.get("changePassword") + "/" + auxToken);
				userRepo.setTokenByEmail(auxToken, email);
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
		String picture = jso.optString("picture");
		if (!pwd1.equals(pwd2))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Error: las contraseñas no coinciden");
		if (pwd1.length() < 4)
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Error: la contraseña debe tener al menos cuatro caracteres");

		try {
			User user = new User();
			user.setName(userName);
			user.setEmail(email);
			user.setPwd(pwd1);

			userService.save(user);

			sendEmail(credenciales, "register");

			return "Registro completado exitosamente";
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Error: El usuario ya existe");
		}
	}

	@DeleteMapping("/remove/{userId}")
	public void remove(@PathVariable String userId) {
		System.out.println("Borrar el usuario con id " + userId);
	}

	@GetMapping("/validateAccount/{tokenId}")
	public void validateAccount(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String tokenId) {
		userService.validateToken(tokenId);
		// Ir a la base de datos, buscar el token con ese tokenId en la tabla, ver que
		// no ha caducado
		// y actualizar la confirmationDate del user
		System.out.println(tokenId);
		try {
			response.sendRedirect(Manager.get().getConfiguration().getString("home"));
		} catch (IOException e) {

		}
	}

}
