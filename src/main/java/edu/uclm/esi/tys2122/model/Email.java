package edu.uclm.esi.tys2122.model;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;

import edu.uclm.esi.tys2122.http.Manager;

public class Email {
	private final Properties properties = new Properties();

	public void send(String destinatario, String subject, String body) {
		JSONObject emailDefaultData = (JSONObject) Manager.get().getConfiguration().getJSONObject("email");

		String smtpHost = (String) emailDefaultData.get("host");
		String startTTLS = (String) emailDefaultData.get("startTTLS");
		String port = (String) emailDefaultData.get("port");
		String sender = (String) emailDefaultData.get("sender");
		String serverUser = (String) emailDefaultData.get("serverUser");
		String userAutentication = (String) emailDefaultData.get("auth");
		String pwd = (String) emailDefaultData.get("pwd"); // PONER LA CONTRASEÑA
		String fallback = (String) emailDefaultData.get("fallback");

		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.starttls.enable", startTTLS);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.mail.sender", sender);
		properties.put("mail.smtp.user", serverUser);
		properties.put("mail.smtp.auth", userAutentication);
		properties.put("mail.smtp.socketFactory.port", port);
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", fallback);

		Runnable r = new Runnable() {
			@Override
			public void run() {
				Authenticator auth = new AutentificadorSMTP(sender, pwd);
				Session session = Session.getInstance(properties, auth);

				MimeMessage msg = new MimeMessage(session);
				try {
					msg.setSubject(subject);
					msg.setText(body);
					msg.setFrom(new InternetAddress(sender));
					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
					Transport.send(msg);
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		};
		new Thread(r).start();
	}

	private class AutentificadorSMTP extends javax.mail.Authenticator {
		private String sender;
		private String pwd;

		public AutentificadorSMTP(String sender, String pwd) {
			this.sender = sender;
			this.pwd = pwd;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(sender, pwd);
		}
	}
}
