package com.dp.plat.support.mail;

import javax.mail.PasswordAuthentication;

public class Authenticator extends javax.mail.Authenticator {
	String userName = null;
	String password = null;

	public Authenticator() {
	}

	public Authenticator(String username, String password) {
		this.userName = username;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, password);
	}
}