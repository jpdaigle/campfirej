package ca.softwareengineering.jcampfire;

public class CampfireException extends Exception {
	private static final long serialVersionUID = 1L;

	public CampfireException(String message) {
		super(message);
	}

	public CampfireException(String message, Throwable cause) {
		super(message, cause);
	}

}
