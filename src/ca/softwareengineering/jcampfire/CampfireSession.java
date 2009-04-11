package ca.softwareengineering.jcampfire;

import static ca.softwareengineering.jcampfire.impl.Constants.CF_DOMAIN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
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

public class CampfireSession {

	final String _subdomain, _user, _password;
	boolean _loggedIn;
	String _cachedLobbyStr;
	List<CampfireRoom> _cachedRooms;
	protected SimpleClient _client;
	
	
	public CampfireSession(String subdomain, String user, String password) {
		_subdomain = subdomain;
		_user = user;
		_password = password;
		_cachedRooms = new ArrayList<CampfireRoom>();
	}

	public boolean connected() {
		return _loggedIn;
	}
	
	public void connect() throws CampfireException {
		_client = new SimpleClient();
		Request req = new Request();
		req.address = baseUrl();

		try {
			String resp = _client.doRequest(req);
		} catch (IOException ioex) {
			throw new CampfireException("Connect failed.", ioex);
		}

		req = new Request();
		req.address = baseUrl() + "login";
		req.rtype = RTYPE.POST;
		req.formFields.put("email_address", _user);
		req.formFields.put("password", _password);

		try {
			String resp = _client.doRequest(req);
			// Look for magic string "Lobby"
			if (resp.matches(".*Lobby.*")) {
				_loggedIn = true;
			} else {
				throw new CampfireException("Login failed.");
			}
			_cachedLobbyStr = resp;

			/* DEBUG */
			FileWriter fw = new FileWriter("/tmp/xhtml.html");
			fw.write(_cachedLobbyStr);
			fw.flush();
			fw.close();
		} catch (IOException ioex) {
			throw new CampfireException("Login failed.", ioex);
		}
	}

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
			System.out.println(lobbyAnchors);
			for (HtmlAnchor htmlAnchor : lobbyAnchors) {
				if (htmlAnchor.href.matches(".*/room/.*")) {
					CampfireRoom room = new CampfireRoom(htmlAnchor.href, htmlAnchor.text, this);
					_cachedRooms.add(room);
				}
			}
		}
		return _cachedRooms;
	}

	public CampfireRoom getRoomByName(String name) throws CampfireException {
		List<CampfireRoom> rooms = getRooms();
		for (CampfireRoom r : rooms) {
			if (r.name.compareToIgnoreCase(name) == 0)
				return r;
		}
		return null;
	}

	public void test() throws CampfireException {
		try {
			FileReader fr = new FileReader("/tmp/xhtml.html");
			BufferedReader br = new BufferedReader(fr);

			String doc = "";
			while (br.ready()) {
				doc += br.readLine();
			}
			br.close();
			System.out.println(doc);
			_cachedLobbyStr = doc;
		} catch (IOException ex) {
			ex.printStackTrace();
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
