package ca.softwareengineering.campfirej.test;

import java.util.List;

import ca.softwareengineering.campfirej.CampfireException;
import ca.softwareengineering.campfirej.CampfireRoom;
import ca.softwareengineering.campfirej.CampfireSession;

public class CTest {
	/*
	 * Args: subdomain, user, password
	 */
	public static void main(String[] args) {
		try {
			CampfireSession cs = new CampfireSession(args[0], args[1], args[2]);
			System.out.println("New CampfireSession: " + cs.toString());
					
			//cs.connect();
			List<CampfireRoom> rooms = cs.getRooms();
			System.out.println(rooms);
			CampfireRoom room1 = cs.getRoomByName("Room 1");
			room1.enter();
			room1.echo("Test speak.", false);
			
		} catch (CampfireException c_ex) {
			c_ex.printStackTrace();
		}
	}

}
