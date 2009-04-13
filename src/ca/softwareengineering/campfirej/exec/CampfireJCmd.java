package ca.softwareengineering.campfirej.exec;

import ca.softwareengineering.campfirej.CampfireException;

public class CampfireJCmd {

	private EchoRuntimeTask task;

	public CampfireJCmd() {
		task = new EchoRuntimeTask();
	}

	public static void main(String[] args) {
		CampfireJCmd cmd = new CampfireJCmd();

		try {
			cmd.parseArgs(args);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			printUsage();
			return;
		}

		try {
			cmd.execute();
		} catch (CampfireException e) {
			System.out.println(e);
		}
	}

	private void parseArgs(String[] args) throws CampfireException {
		int i = 0;
		while (i < args.length) {
			if (args[i].equals("-s")) {
				task.setSubdomain(args[++i]);
			} else if (args[i].equals("-u")) {
				task.setUser(args[++i]);
			} else if (args[i].equals("-p")) {
				task.setPassword(args[++i]);
			} else if (args[i].equals("-r")) {
				task.setRoom(args[++i]);
			} else if (args[i].equals("-m")) {
				task.setMessage(args[++i]);
			} else if (args[i].equals("--paste")) {
				task.setPaste(true);
			} else {
				throw new IllegalArgumentException("Unknown option " + args[i]);
			}
			i++;
		}
		task.validate();
	}

	public void execute() throws CampfireException {
		System.out.printf("CampfireJEcho: subdomain '%s', user '%s', room '%s' \n", 
				task.getSubdomain(), 
				task.getUser(), 
				task.getRoom());
		task.execute();
		System.out.println(task.getMessage());
	}

	public static void printUsage() {
		String usage = "USAGE: -s <subdomain> -u <username> -p <password> -r <room> -m \"message\" [--paste]\n";
		usage += "    -s <subdomain>    SUBDOMAIN.campfirenow.com\n" + "    -u <username>     user@example.com\n"
				+ "    -p <password>     password\n" + "    -r \"<room>\"       room name\n"
				+ "    -m \"message\"      message to send\n" + "    --paste           send as a paste\n";

		System.out.println(usage);
	}

}
