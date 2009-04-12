package ca.softwareengineering.jcampfire;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.softwareengineering.jcampfire.http.SimpleClient.RTYPE;
import ca.softwareengineering.jcampfire.http.SimpleClient.Request;

/**
 * A handle to a Campfire room.
 * 
 */
public class CampfireRoom {

	protected final String href;
	protected final String name;
	protected final int id;
	final CampfireSession session;

	protected CampfireRoom(String href, String name, CampfireSession session) throws CampfireException {
		this.href = href;
		this.name = name;
		this.session = session;
		this.id = getId(href);
	}

	private final int getId(String roomHref) throws CampfireException {
		Pattern p = Pattern.compile(".*room/((\\d+)).*");
		Matcher m = p.matcher(roomHref);
		if (m.matches()) {
			if (m.groupCount() > 0)
				return Integer.parseInt(m.group(1));
		}
		// Parse error
		throw new CampfireException(String.format("Could not parse room href '%s' for ID.", roomHref));
	}

	/**
	 * Enter this room. Should be called before <code>echo()</echo>.
	 * 
	 * @throws CampfireException
	 */
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

	/**
	 * Send a message to the room. The room should first be entered using
	 * <code>enter()</code>.
	 * 
	 * @param message
	 *            Message to send, will be URLEncoded.
	 * @param paste
	 *            Whether to mark this message as a paste.
	 * @throws CampfireException
	 */
	public void echo(String message, boolean paste) throws CampfireException {
		if (!session.connected())
			session.connect();
		Request req = new Request();
		req.address = this.href + "/speak";
		req.rtype = RTYPE.POST;
		req.formFields.put("message", message);
		req.formFields.put("t", String.valueOf(System.currentTimeMillis()));
		req.formFields.put("ajax", "true");
		if (paste)
			req.formFields.put("paste", "true");

		try {
			String ret = session._client.doRequest(req);
			System.out.println("RET: " + ret);
		} catch (IOException e) {
			throw new CampfireException("Unable to echo message to room.", e);
		}

	}

	@Override
	public String toString() {
		return String.format("%s (%s, id=%s)", name, href, id);
	}
}
