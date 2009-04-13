package ca.softwareengineering.campfirej.exec;

import ca.softwareengineering.campfirej.CampfireException;
import ca.softwareengineering.campfirej.CampfireRoom;
import ca.softwareengineering.campfirej.CampfireSession;

public class EchoRuntimeTask {
	private String subdomain = "", user = "", password = "", room = "", message = "";
	private boolean paste = false;

	public EchoRuntimeTask() {
	}

	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPaste(boolean paste) {
		this.paste = paste;
	}

	public String getSubdomain() {
		return subdomain;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getRoom() {
		return room;
	}

	public String getMessage() {
		return message;
	}

	public boolean isPaste() {
		return paste;
	}

	public void validate() throws CampfireException {
		checkNotBlank(password, "password");
		checkNotBlank(subdomain, "subdomain");
		checkNotBlank(user, "user");
		checkNotBlank(room, "room");
		checkNotBlank(message, "message");
	}

	private void checkNotBlank(String arg, String name) throws CampfireException {
		if (arg == null || arg.length() == 0) {
			throw new CampfireException(String.format("Must supply value for '%s'", name));
		}
	}

	public void execute() throws CampfireException {
		validate();

		CampfireSession c_session = new CampfireSession(subdomain, user, password);
		c_session.connect();
		CampfireRoom c_room = c_session.getRoomByName(room);
		if (c_room == null)
			throw new CampfireException(String.format("Unable to find room '%s'", room));

		c_room.enter();
		c_room.echo(message, paste);
	}

}
