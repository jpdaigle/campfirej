package ca.softwareengineering.jcampfire;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.softwareengineering.jcampfire.http.SimpleClient.Request;

public class CampfireRoom {

	public final String href;
	public final String name;
	public final int id;
	final CampfireSession session;

	public CampfireRoom(String href, String name, CampfireSession session) {
		this.href = href;
		this.name = name;
		this.session = session;
		this.id = getId(href);
	}

	private final int getId(String roomHref) {
		Pattern p = Pattern.compile(".*room/((\\d+)).*");
		Matcher m = p.matcher(roomHref);
		if (m.matches()) {
			if (m.groupCount()>0)
				return Integer.parseInt(m.group(1));
		}
		return 0;
	}
	
	
	public void enter() throws CampfireException {
		if (!session.connected()) {
			session.connect();
		}
		Request req = new Request();
		req.address = this.href;
		try {
			String roomContents = session._client.doRequest(req);
			System.out.println(roomContents);
		} catch (IOException e) {
			throw new CampfireException("Error joining room " + name, e);
		}
	}

	@Override
	public String toString() {
		return String.format("%s (%s, id=%s)", name, href, id);
	}
}
