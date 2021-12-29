package edu.uclm.esi.tys2122.model;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.esi.tys2122.websockets.WrapperSession;

@Entity
@Table(indexes = { @Index(unique = true, columnList = "email"), @Index(unique = true, columnList = "name"), })
public class User {

	/* Attributes */
	@Id
	@Column(length = 36)
	private String id;

	@NotBlank(message = "El correo electrónico no puede estar vacio.")
	@Column(length = 100)
	private String email;

	@NotBlank(message = "El nombre de usuario no puede estar vacío.")
	@Column(length = 40)
	private String name;

	@NotBlank(message = "La contraseña no puede estar vacía.")
	private String pwd;

	@Column(length = 36)
	private String cookie;

	private String type;
	
	private String token;	

	@Transient
	private WrapperSession session;

	/* Constructors */

	public User() {
		this.id = UUID.randomUUID().toString();
	}

	// temporal constructor
	public User(@NotBlank String name, @NotBlank String email) {
		this.id = UUID.randomUUID().toString();
		this.email = email;
		this.name = name;
		this.pwd = org.apache.commons.codec.digest.DigestUtils.sha512Hex(UUID.randomUUID().toString());
		this.type = "temporal";
	}

	// google constructor
	public User(@NotBlank String id, @NotBlank String name, @NotBlank String email) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.pwd = org.apache.commons.codec.digest.DigestUtils.sha512Hex(UUID.randomUUID().toString());
		this.type = "google";
	}

	/* Functions */

	public void sendMessage(JSONObject jso) throws IOException {
		WebSocketSession wsSession = this.session.getWsSession();
		wsSession.sendMessage(new TextMessage(jso.toString()));
	}

	/* Getters And Setters */

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String userName) {
		this.name = userName;
	}

	@JsonIgnore
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	@JsonIgnore
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	@JsonIgnore
	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	@JsonIgnore
	public WrapperSession getSession() {
		return session;
	}

	public void setSession(WrapperSession session) {
		this.session = session;
	}
	
	@JsonIgnore
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}
