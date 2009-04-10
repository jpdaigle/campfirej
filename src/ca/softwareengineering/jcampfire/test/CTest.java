package ca.softwareengineering.jcampfire.test;

import ca.softwareengineering.jcampfire.CampfireException;
import ca.softwareengineering.jcampfire.CampfireSession;

public class CTest {
	/*
	 * Args: subdomain, user, password
	 */
	public static void main(String[] args) {
		try {
			CampfireSession cs = new CampfireSession(args[0], args[1], args[2]);
			System.out.println("New CampfireSession: " + cs.toString());
			cs.connect();
			
		} catch (CampfireException c_ex) {
			c_ex.printStackTrace();
		}
	}

}
