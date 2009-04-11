package ca.softwareengineering.jcampfire.test;

import java.util.List;

import ca.softwareengineering.jcampfire.CampfireException;
import ca.softwareengineering.jcampfire.CampfireRoom;
import ca.softwareengineering.jcampfire.CampfireSession;

public class CTest {
	/*
	 * Args: subdomain, user, password
	 */
	public static void main(String[] args) {
		try {
			CampfireSession cs = new CampfireSession(args[0], args[1], args[2]);
			System.out.println("New CampfireSession: " + cs.toString());
						
			//cs.connect();
			cs.test();
			List<CampfireRoom> rooms = cs.getRooms();
			System.out.println(rooms);
			CampfireRoom room1 = cs.getRoomByName("Room 1");
			room1.enter();
			
		} catch (CampfireException c_ex) {
			c_ex.printStackTrace();
		}
	}

}
