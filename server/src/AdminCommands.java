import java.net.InetAddress;
import java.util.Scanner;

public class AdminCommands extends Thread{

	/**
	 * In this class the Admin has the opportunity to submit commands to the Server.
	 * The commands will be processed here.
	 */
	
	private Scanner scanner;
	public static boolean running = true;
	public static boolean log = false;
	
	
	
	/**
	 * Constructor of the AdminCommands Class. Is being called on the launch of the server.
	 * @param ipAddress  the ip address from the server
	 * @return new AdminCommands class. The Admin Commands will be processed here.
	 * @author github.com/oliverrascheja
	 */
	public AdminCommands(InetAddress ipAddress) {
		scanner = new Scanner(System.in);
		System.out.println("logging data: " + log);
		System.out.println("Admin-Eingabe\n'exit' to close the server.");
		System.out.println("'help' for further information");
		System.out.println("server address: " + ipAddress.getHostAddress() + ":44445");
	}
	
	
	
	/**
	 * Processing the Commands from the Server Admin
	 */
	public void run() {
		while (running) {
			String line = scanner.nextLine();
			
			
			if (line.contains("exit")) {
				running = false;
				scanner.close();
				Server.closeServer();
			}
			else if (line.contains("toggle log")) {
				log = !log;
				System.out.println("logging data: " + log);
			}
			else if (line.contains("list users")) {
				System.out.print("Users Online: " + Server.clients.size() + "\n");
				for (Connection c : Server.clients) {
					System.out.print("'" + c.getUserName() + "' ");
				}
				System.out.print("\n");
			}
			else if (line.contains("list groups")) {
				for (int i = 0; i < Server.clients.size(); i++) {
					System.out.println("Group '" + Server.groups.get(i).getGroupName() + "'  ");
					System.out.print("   Members online: ");
					for (int k = 0; k < Server.groups.get(i).getMemberList().size(); k++) {
						System.out.print("'" + Server.groups.get(i).getMemberList().get(k) + "'  ");
					}
					for (String user : Server.groups.get(i).getMemberList()) {
						System.out.print("'" + user + "' ");
					}
				}
				System.out.print("\n");
			}
			else if (line.contains("help")) {
				System.out.println("Help Page: ");
				System.out.println("'toggle log' : print all future messages");
				System.out.println("'list users' : list all users");
				System.out.println("'list groups' : list all groups");
				System.out.println("'exit' : shut down server.\n\n");
			}
			
		}
	}
}