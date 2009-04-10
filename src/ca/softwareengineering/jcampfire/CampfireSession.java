package ca.softwareengineering.jcampfire;

import static ca.softwareengineering.jcampfire.impl.Constants.CF_DOMAIN;

import java.io.IOException;

import ca.softwareengineering.jcampfire.http.SimpleClient;
import ca.softwareengineering.jcampfire.http.SimpleClient.Request;

public class CampfireSession {

	final String _subdomain, _user, _password;

	public CampfireSession(String subdomain, String user, String password) {
		_subdomain = subdomain;
		_user = user;
		_password = password;
	}

	public void connect() throws CampfireException {
		SimpleClient client = new SimpleClient();
		Request req = new Request();
		req.setAddress(baseUrl());

		try {
			String resp = client.doRequest(req);
			System.out.println("RESP RESP RESP\n" + resp);
		} catch (IOException ioex) {
			throw new CampfireException("Connect failed.", ioex);
		}
	}

	@Override
	public String toString() {
		return String.format("%s@%s", _user, _subdomain);
	}

	String baseUrl() {
		return "http://" + _subdomain + "." + CF_DOMAIN + "/";
	}

}
