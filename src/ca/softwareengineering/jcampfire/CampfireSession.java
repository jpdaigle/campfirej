package ca.softwareengineering.jcampfire;

import static ca.softwareengineering.jcampfire.impl.Constants.CF_DOMAIN;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import ca.softwareengineering.jcampfire.http.HtmlParserCallback;
import ca.softwareengineering.jcampfire.http.SimpleClient;
import ca.softwareengineering.jcampfire.http.HtmlParserCallback.HtmlAnchor;
import ca.softwareengineering.jcampfire.http.SimpleClient.RTYPE;
import ca.softwareengineering.jcampfire.http.SimpleClient.Request;
import ca.softwareengineering.jcampfire.impl.Constants;

/**
 * A session to the Campfire server and the main API entry point.
 * 
 * <p>
 * Currently, once a session is opened, it is never closed or logged out to
 * avoid polluting rooms with a bunch of "Entered the room" / "Left the room"
 * messages when doing several executions in a row (e.g. from a continuous
 * integration system). This behaviour is likely to change in the future, and a
 * logout() method implemented.
 * 
 * <p>
 * SSL support is not yet implemented because I don't have an SSL account (I
 * just have the mini free one), but it should be trivial.
 */
public class CampfireSession {

	final String _subdomain, _user, _password;
	boolean _loggedIn;
	String _cachedLobbyStr;
	List<CampfireRoom> _cachedRooms;
	protected SimpleClient _client;

	/**
	 * Creates a new CampfireSession.
	 * 
	 * @param subdomain
	 *            The subdomain to login to (SUBDOMAIN.campfirenow.com)
	 * @param user
	 *            Email address to use for logging in
	 * @param password
	 *            Password to use for logging in
	 */
	public CampfireSession(String subdomain, String user, String password) {
		_subdomain = subdomain;
		_user = user;
		_password = password;
		_cachedRooms = new ArrayList<CampfireRoom>();
	}

	/**
	 * Checks whether this session has been connected. Once connect() has been
	 * called and returned successfully, we are assumed 'connected' even if no
	 * connections to the server are active. Each HTTP request makes a new
	 * connection.
	 */
	public boolean connected() {
		return _loggedIn;
	}

	/**
	 * Connect and login.
	 */
	public void connect() throws CampfireException {
		if (connected())
			return;

		_client = new SimpleClient();
		Request req = new Request();
		req.address = baseUrl();

		try {
			_client.doRequest(req);
		} catch (IOException ioex) {
			throw new CampfireException("Connect failed.", ioex);
		}

		req = new Request();
		req.address = baseUrl() + Constants.CF_LOGIN_URL;
		req.rtype = RTYPE.POST;
		req.formFields.put(Constants.CF_LOGIN_EMAIL, _user);
		req.formFields.put(Constants.CF_LOGIN_PASSWORD, _password);

		try {
			String resp = _client.doRequest(req);
			// Look for magic string "Lobby"
			if (resp.matches(".*Lobby.*")) {
				_loggedIn = true;
			} else {
				throw new CampfireException("Login failed, could not find 'Lobby' in response.");
			}
			_cachedLobbyStr = resp;
		} catch (IOException ioex) {
			throw new CampfireException("Login failed.", ioex);
		}
	}

	/**
	 * Gets the list of all rooms accessible on this Campfire subdomain.
	 */
	public List<CampfireRoom> getRooms() throws CampfireException {
		if (!_loggedIn && _cachedLobbyStr == null)
			connect();

		if (_cachedRooms.size() == 0) {
			StringReader r = new StringReader(_cachedLobbyStr);
			ParserDelegator parser = new ParserDelegator();
			ParserCallback callback = new HtmlParserCallback();
			try {
				parser.parse(r, callback, true);
			} catch (IOException e) {
				throw new CampfireException("HTML parsing error", e);
			}
			List<HtmlAnchor> lobbyAnchors = ((HtmlParserCallback) callback).anchors;
			for (HtmlAnchor htmlAnchor : lobbyAnchors) {
				if (htmlAnchor.href.matches(".*/room/.*")) {
					try {
						CampfireRoom room = new CampfireRoom(htmlAnchor.href, htmlAnchor.text, this);
						_cachedRooms.add(room);
					} catch (CampfireException cex) {
						// Print and keep going, we consider this a non-fatal
						// problem because other rooms may be loadable.
						System.err.println("Error loading room: " + cex.getMessage());
					}
				}
			}
		}
		return _cachedRooms;
	}

	/**
	 * Gets a <code>CampfireRoom</code> object by its display name.
	 * 
	 * @param name
	 *            The display name to search for.
	 * @return The first room found matching the given name, or
	 *         <code>null</code> if not found.
	 * @throws CampfireException
	 */
	public CampfireRoom getRoomByName(String name) throws CampfireException {
		List<CampfireRoom> rooms = getRooms();
		for (CampfireRoom r : rooms) {
			if (r.name.compareToIgnoreCase(name) == 0)
				return r;
		}
		return null;
	}

	@Override
	public String toString() {
		return String.format("%s@%s", _user, _subdomain);
	}

	private final String baseUrl() {
		return "http://" + _subdomain + "." + CF_DOMAIN + "/";
	}

}
