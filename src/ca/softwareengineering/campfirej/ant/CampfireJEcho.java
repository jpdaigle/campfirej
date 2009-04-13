package ca.softwareengineering.campfirej.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import ca.softwareengineering.campfirej.exec.EchoRuntimeTask;

/**
 * This is an ant task for sending a message to a Campfire room.
 */
public class CampfireJEcho extends Task {

	private boolean failonerror = true;

	private EchoRuntimeTask task;

	public CampfireJEcho() {
		task = new EchoRuntimeTask();
	}

	public void setSubdomain(String subdomain) {
		task.setSubdomain(subdomain);
	}

	public void setUser(String user) {
		task.setUser(user);
	}

	public void setPassword(String password) {
		task.setPassword(password);
	}

	public void setRoom(String room) {
		task.setRoom(room);
	}

	public void setMessage(String message) {
		task.setMessage(message);
	}

	public void setFailonerror(boolean failonerror) {
		this.failonerror = failonerror;
	}

	public void setPaste(boolean paste) {
		task.setPaste(paste);
	}

	public void execute() throws BuildException {
		try {
			task.validate();
		} catch (Exception e) {
			// Settings validation failure
			throw new BuildException(e);
		}

		try {
			System.out.printf("CampfireJEcho: subdomain '%s', user '%s', room '%s' \n", 
					task.getSubdomain(), 
					task.getUser(), 
					task.getRoom());
			task.execute();
			System.out.println(task.getMessage());
		} catch (Throwable t) {
			if (failonerror)
				throw new BuildException(t);
			else {
				// Configured to swallow errors
				System.out.println("CampfireJ error: " + t.getMessage());
			}
		}
	}

}
