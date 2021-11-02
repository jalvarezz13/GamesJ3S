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
@Table(indexes = {
	@Index(unique = true, columnList = "email"),
	@Index(unique = true, columnList = "name"),
})
public class User {
	@Id
	@Column(length = 36)
	private String id;
	@NotBlank 
	private String email;
	@NotBlank
	private String name;
	@NotBlank
	private String pwd;
	private String picture;
	private Long confirmationDate;
	
	@Transient
	private WrapperSession session;
	
	public User() {
		this.id = UUID.randomUUID().toString();
	}

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

	public String getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		byte[] b64 = Base64.getEncoder().encode(picture);
		this.picture = new String(b64);	
	}
	
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	@JsonIgnore
	public Long getConfirmationDate() {
		return confirmationDate;
	}
	
	public void setConfirmationDate(Long confirmationDate) {
		this.confirmationDate = confirmationDate;
	}

	public void setSession(WrapperSession wrapperSession) {
		this.session = wrapperSession;
	}
	
	@JsonIgnore
	public WrapperSession getSession() {
		return session;
	}

	public void sendMessage(JSONObject jso) throws IOException {
		WebSocketSession wsSession = this.session.getWsSession();
		wsSession.sendMessage(new TextMessage(jso.toString()));
	}
}
