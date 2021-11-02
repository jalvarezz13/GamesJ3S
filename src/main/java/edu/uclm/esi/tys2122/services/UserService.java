package edu.uclm.esi.tys2122.services;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tys2122.dao.LoginRepository;
import edu.uclm.esi.tys2122.dao.TokenRepository;
import edu.uclm.esi.tys2122.dao.UserRepository;
import edu.uclm.esi.tys2122.model.Login;
import edu.uclm.esi.tys2122.model.Token;
import edu.uclm.esi.tys2122.model.User;

@Service
public class UserService {
	@Autowired
	private LoginRepository loginDAO;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private TokenRepository tokenRepo;
	
	private ConcurrentHashMap<String, User> connectedUsers;
	
	public UserService() {
		this.connectedUsers = new ConcurrentHashMap<>();
	}

	public User doLogin(String name, String pwd, String ip) {
		User user = userRepo.findByNameAndPwd(name, pwd);
		if (user==null) //  || user.getConfirmationDate()==null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales no válidas o cuenta no validada");
		
		this.connectedUsers.put(user.getId(), user);
		return user;
	}

	public void save(User user) {
		userRepo.save(user);
		
		Token token = new Token(user.getEmail());
		tokenRepo.save(token);
		/*Email smtp=new Email();
		smtp.send(user.getEmail(), "Bienvenido al sistema", 
			"Para confirmar, pulse aquí: " +
			"http://localhost/user/validateAccount/" + token.getId());*/

	}

	public void validateToken(String tokenId) {
		Optional<Token> optToken = tokenRepo.findById(tokenId);
		if (optToken.isPresent()) {
			Token token = optToken.get();
			long date = token.getDate();
			long now = System.currentTimeMillis();
			if (now>date+24*60*60*1000)
				throw new ResponseStatusException(HttpStatus.GONE, "Token caducado");
			String email = token.getEmail();
			User user = userRepo.findByEmail(email);
			if (user!=null) {
				user.setConfirmationDate(now);
				userRepo.save(user);
			} else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
		} else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token " + tokenId + " no encontrado");
	}

	public void insertLogin(User user, String ip, Cookie cookie) {
		Login login = new Login();
		login.setEmail(user.getEmail());
		login.setDate(System.currentTimeMillis());
		login.setIp(ip);
		login.setCookieValue(cookie.getValue());
		loginDAO.save(login);
	}

	public User findUser(String userId) {
		return this.connectedUsers.get(userId);
	}

}
