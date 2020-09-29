import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	
	static ServerSocket serverSocket;
	
	static List<Socket> socketList;
	
	static List<Connection> clients;
	static List<Group> groups;
	
	
	
	/**
	 * Constructor for launching the server.
	 * @throws Exception if server already running on that port.
	 * @author github.com/oliverrascheja
	 */
	public static void main(String[] args) throws IOException {
		
		serverSocket = new ServerSocket(44445);
		socketList = new ArrayList<Socket>();
		clients = new ArrayList<Connection>();
		groups = new ArrayList<Group>();
		
		AdminCommands adminCommands = new AdminCommands(InetAddress.getLocalHost());
		adminCommands.start();
		
		System.out.println("Server launched successfully!");
		manageNewClients();
	}
	
	
	
	/**
	 * Manages new Clients and processing 'join' and 'request time' commands. containing infinite loop.
	 * All users doesn't have to be logged in to the Server to perform these commands listed in this method
	 * @author github.com/oliverrascheja
	 */
	private static void manageNewClients() {
		while (AdminCommands.running) {
			
			try {
				if (!serverSocket.isClosed()) {
					Socket currSocket = serverSocket.accept(); //wait for new request
					socketList.add(currSocket);

					Connection co = new Connection(socketList.get(socketList.size() - 1));

					List<String> request = co.getRequest(socketList.get(socketList.size() - 1));
					System.out.println(request.toString());
					if (request.size() > 3) {
						if ((request.get(0).contains("message/head")) && 
								(request.get(1).contains("user join")) && (request.get(3).contains("message/body"))) {
							
							co.setUser(request.get(2));	
							clients.add(co);
							co.start();
							
							if (AdminCommands.log) 
								System.out.println("log: User '" + co.getUserName() + "' connected to the server.");
						}
					}
					
					if ((request.get(0).contains("message/head") && (request.get(1).contains("request time")))) {
						System.out.println("requested server time");
						String str = Response.getTimeStamp();
						OutputStream outputstream = currSocket.getOutputStream();
						outputstream.write(str.getBytes());
						outputstream.flush();
					}
					
					if ((request.get(0).contains("message/head")) && (request.get(1).contains("group join"))) {
						String user = "user-" + (int)(Math.random() * 88888 + 10000);
						co.setUser(user);	
						clients.add(co);
						addUserToGroup(request.get(2), user);
						co.start();
						
						if (AdminCommands.log) 
							System.out.println("log: User '" + co.getUserName() + "' connected to the server.");
					}
				}
			} 
			catch (SocketException se) {
				//System.out.println();
			}
			catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
			
	
	
	/**
	 * Adds new User to a Chat Group
	 * @param name  the Group name
	 * @param user  the Username to join the group
	 */
	public static void addUserToGroup(String name, String user) {
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getGroupName().equals(name)) {
				groups.get(i).addNewMember(user);
				return;
			}
		}
		//if group not existing: Create new Group and join the user
		groups.add(new Group(name));
		groups.get(groups.size() - 1).addNewMember(user);
		if (AdminCommands.log) {
			System.out.println("log: created group '" + name + "'.");
			System.out.println("log: added '" + user + "' to group '" + name + "'.");
		}
	}
	
	
	
	/**
	 * Sends a notification to all users of a group
	 * @param groupName  the name of the specified group
	 * @param user  the name of the message sender
	 * @param message  the actual message as a String
	 * @param lines  number of lines for multiline messages
	 * @return returns 1 if group doesn't exist, otherwise 0
	 * @author github.com/oliverrascheja
	 */
	public static int notifyToGroup(String groupName, String user, String message, int lines) {
		Group gr = null;
		
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getGroupName().equals(groupName)) {
				gr = groups.get(i);
				break;
			}
		}
		if (gr == null) return 1;
		
		List<String> members = gr.getMemberList();
		for (int m = 0; m < members.size(); m++) {
			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i).getUserName().equals(members.get(m))) {
					//if client is a member of the group:
					respondOnSocket(members.get(m), Response.getNotifyGroup(groupName, message, lines));
				}
			}
		}
		return 0;
	}
	
	
	
	/**
	 * Removes a User from a Group. Deleting the group if last user leaving.
	 * @param groupName  the Name of the specified group
	 * @param user  the name of the user to leave the group
	 */
	public static void removeUserFromGroup(String groupName, String user) {
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getGroupName().equals(groupName)) {
				groups.get(i).removeMember(user);
				if (groups.get(i).getMemberList().size() == 0) {
					groups.remove(groups.get(i));
					if (AdminCommands.log) 
						System.out.println("log: removed empty group '" + groupName + "'.");
				}
			}
		}
	}
	
	
	
	/**
	 * Sends a notification to all users online
	 * @param message  the message from admin to all users
	 * @return returns always 1
	 */
	public static int notifyToAll(String msg) {
		for (int i = 0; i < clients.size(); i++) {
			String str = clients.get(i).getUserName();
			String message = Response.getNotifyUser("Server", str, msg, 1);
			clients.get(i).respond(message);
		}
		return 1;
	}
	
	
	
	/**
	 * Sending a recieved message to the reciever.
	 * @param userName  the reciever's user name
	 * @param msg  the message
	 * @return status of the sent message. 0: successfully sent, 1: user not found
	 * @author github.com/oliverrascheja
	 */
	public static int respondOnSocket(String userName, String msg) {
		for (int i = 0; i < clients.size(); i++) {
			String str = clients.get(i).getUserName();
			System.out.println(clients.get(i).getUserName());
			if (str.equals(userName)) {
				System.out.println("Respond on " + str + " message: " + msg);
				clients.get(i).respond(msg);
				return 0;
			}
		}
		return 1;
	}
	
	
	
	/**
	 * Kicks a user from the server.
	 * @param userName  the user to be kicked
	 * @return returns 0 if kicked, 1 if not kicked
	 */
	public static int kickUser(String userName) {
		for (Connection client : clients) {
			String str = client.getUserName();
			System.out.println(client.getUserName());
			if (str.equals(userName)) {
				String msg = Response.getNotifyUser("Server", userName, "INFO: You were kicked from the Server.", 1);
				client.respond(msg);
				client.clientConnectedToServer = false;
				clients.remove(client);
				return 0;
			}
		}
		return 1;
	}
	
	/**
	 * Search for specified group and returning reference to it.
	 * @param na  name of the group to search
	 * @return returns reference to group or creates new instance of Group if not doesn't exist
	 */
	public static Group getGroup(String na) {
		for (Group gr : groups) {
			if (gr.getGroupName().equals(na)) {
				return gr;
			}
		}
		
		groups.add(new Group(na));
		return groups.get(groups.size() - 1);
	}
	
	
	
	/**
	 * Removes a Client from the Server
	 * @param t Connection Instance from the specified user.
	 */
	public static void removeClient(Connection t) {
		clients.remove(t);
	}
	
	
	
	/**
	 * Disconnects with all groups and all users.
	 * @return returns always 0 after completion.
	 */
	public static int disconnectAll() {
		disconnectAllGroups();
		disconnectAllClients();
		return 0;
	}
	
	
	
	/**
	 * Disconnecting all groups form the server.
	 */
	public static void disconnectAllGroups() {
		try {
			for (Group group : groups) {
				group.dissolve();
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	
	
	/**
	 * Disconnecting all users from the server.
	 */
	public static void disconnectAllClients() {
		try {
			for (Connection client : clients) {
				String msg = Response.getNotifyUser("Server", client.getUserName(), "INFO: Server is shutting down.", 1);
				client.respond(msg);
				client.clientConnectedToServer = false;
				clients.remove(client);
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	
	
	/**
	 * Closes the server and cleares up resources.
	 */
	public static void closeServer() {
		
		int ret = disconnectAll();
		if (ret == 0) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Server closed.");
			System.out.println(" (c) Oliver Rascheja.");
			System.exit(0);
		}
	}
}
