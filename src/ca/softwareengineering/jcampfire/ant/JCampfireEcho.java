package ca.softwareengineering.jcampfire.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import ca.softwareengineering.jcampfire.CampfireRoom;
import ca.softwareengineering.jcampfire.CampfireSession;

/**
 * This is an ant task for sending a message to a Campfire room.
 */
public class JCampfireEcho extends Task {

	private String subdomain = "", user = "", password = "", room = "", message = "";
	private boolean paste = false, failonerror = true;

	public JCampfireEcho() {
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

	public void setFailonerror(boolean failonerror) {
		this.failonerror = failonerror;
	}

	public void setPaste(boolean paste) {
		this.paste = paste;
	}

	public void execute() throws BuildException {
		checkNotBlank(password, "password");
		checkNotBlank(subdomain, "subdomain");
		checkNotBlank(user, "user");
		checkNotBlank(room, "room");
		checkNotBlank(message, "message");

		try {
			System.out.printf("JCampfireEcho: subdomain '%s', user '%s', room '%s' \n", subdomain, user, room);

			CampfireSession c_session = new CampfireSession(subdomain, user, password);
			c_session.connect();
			CampfireRoom c_room = c_session.getRoomByName(room);
			if (c_room == null)
				throw new BuildException(String.format("Unable to find room '%s'", room));

			c_room.enter();
			c_room.echo(message, paste);
			System.out.println(message);
			
		} catch (Throwable t) {
			if (failonerror)
				throw new BuildException(t);
			else {
				// Configured to swallow errors
				System.out.println("JCampfire error: " + t.getMessage());
			}
		}
	}

	private void checkNotBlank(String arg, String name) throws BuildException {
		if (arg == null || arg.length() == 0) {
			throw new BuildException(String.format("Must supply value for '%s'", name));
		}
	}

}
