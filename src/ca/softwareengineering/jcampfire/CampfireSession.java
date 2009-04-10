package ca.softwareengineering.jcampfire;

public class CampfireSession {

	public final String CF_DOMAIN = "campfirenow.com";
	
	final String _subdomain, _user, _password;
	
	public CampfireSession(String subdomain, String user, String password) {
		_subdomain = subdomain;
		_user = user;
		_password = password;
	}
}
