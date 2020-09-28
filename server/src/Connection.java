import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Connection extends Thread{
	
	private String user = "";
	private Socket socket;
	private InputStream is;
	private BufferedReader br;
	public boolean clientConnectedToServer;
	
	
	/**
	 * Management of a Session for a SINGLE user.
	 * @param socket  users socket
	 * @param user  user name
	 * @return new Connection Class for only one user
	 * @author github.com/oliverrascheja
	 */
	public Connection(Socket s, String u) {
		this(s);
		this.user = u;
	}
	
	
	
	/**
	 * Opening a new Socket for a SINGLE user
	 * @param s  users socket
	 * @return new Connection Class for only one user
	 */
	public Connection(Socket s) {
		socket = s;
		try {
			is = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			clientConnectedToServer = true;
		} catch (Exception e) {}
	}
	

	
	/**
	 * Infinite loop for fetching messages from one Socket
	 */
	public void run() {
		System.out.println("running connection on user " + user);

		while (clientConnectedToServer) {	
			if (socket != null) readMessage(getRequest(socket));
			else clientConnectedToServer = false;
		}
		
		kill();
	}
	
	
	
	/**
	 * Clearing resources and closing connection to the user.
	 */
	public void kill() {
		try {
			clientConnectedToServer = false;
			br.close();
			socket.close();
			Server.removeClient(this);
			user = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Setting a user name
	 * @param s  users name
	 */
	public void setUser(String s) {
		user = s;
	}
	 
	
	
	/**
	 * Returning the user name form this Connection
	 * @return the user name form this Connection
	 */
	public String getUserName() {
		return user;
	}
	
	
	
	/**
	 * Setting a Socket for this Connection
	 * @param so  Socket for this Connection
	 */
	public void setSocket(Socket so) {
		socket = so;
	}
	
	
	
	/**
	 * Returning the users socket form this Connection
	 * @return the users socket form this Connection
	 */
	public Socket getSocket() {
		return socket;
	}
	
	
	
	/**
	 * Processing a new incoming message from this Socket.
	 * @param str  the full protocolled message from the user
	 */
	public void readMessage(List<String> str) {
		try {

			if (str.size() == 0) {
				//respond(Response.getEmptyMessageError());
				return;
			}
			
			if (!str.get(0).equals("message/head")) {
				respond(Response.getWrongHeaderError());
				return;
			}
			
			if (str.get(1).equals("request time")) {
				if (str.get(2).equals("message/body")) {
					respond(Response.getTimeStamp());
				}
			}
			else if (str.get(1).equals("group join")) {
				String groupName = str.get(2);
				if (str.get(3).equals("message/body")) {
					Server.addUserToGroup(groupName, user);
				}
				else {
					respond(Response.getMissingBodyError());
				}
				return;
			}
			else if (str.get(1).equals("group leave")) {
				String groupName = str.get(2);
				if (str.get(3).equals("message/body")) {
					Server.removeUserFromGroup(groupName, user);
					if (AdminCommands.log) System.out.println("log: User '" + user + "' left the server.");
					kill();
				}
				else {
					respond(Response.getMissingBodyError());
				}
				return;
			}
			else if (str.get(1).equals("group notify")) {
				String groupName = str.get(2);
				if (str.get(4).equals("message/body")) {
					String message = "";
					int lines = str.size() - 5;
					try {
						for (int y = 5; y < str.size(); y++) {
							message += str.get(y) + "\n";
						}
					} catch (Exception e) {}
					Server.notifyToGroup(groupName, user, message, lines);
				}
				else {
					respond(Response.getMissingBodyError());
				}
				return;
			}
			else if (str.get(1).equals("user join")) {
				String user = str.get(2);
				if (str.get(3).equals("message/body")) {
					//deprecated: userJoin(socket, user);
				}
				else {
					respond(Response.getMissingBodyError());
				}
				return;
			}
			else if (str.get(1).equals("user leave")) {
				if (str.get(3).equals("message/body") && (str.get(2).equals(user))) {
					if (AdminCommands.log) System.out.println("log: User '" + user + "' left the server.");
					kill();
				}
				else {
					respond(Response.getMissingBodyError());
				}
				return;
			}
			else if (str.get(1).equals("user text notify")) {
				String user = str.get(2);
				String receiver = str.get(3);
				String msg = "";
				int lines = str.size() - 6;
				try {
					for (int y = 6; y < str.size(); y++) {
						msg += str.get(y) + "\n";
					}
				} catch (Exception e) {}
				
				msg = Response.getNotifyUser(user, receiver, msg, lines);
				int errCode = Server.respondOnSocket(receiver, msg);
				System.out.println("ErrorCode: " + errCode);
				if (errCode == 1) {
					respond(Response.userDestinationUnknown(receiver));
				}
				return;
			}
			//else {
				//respond(socket, Response.getWrongMessageTypeError());
				//return;
			//}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Fetching incoming request from the user (client). The full message will be stored in a list of strings and returned.
	 * @param s  Socket of the incoming message
	 * @return the full protocolled message from the Socket.
	 * @author github.com/oliverrascheja
	 */
	public List<String> getRequest(Socket s) {
		if (!AdminCommands.running) return null;
		
		List<String> str = new ArrayList<String>();
		if (s != null) {
			boolean reading = true;
			int linesToRead = -1, x = -1;
			
			try {
				do {
					String line = br.readLine();
					str.add(line);
					if (line.equals(null)) reading = false;
					if (x != -1) linesToRead--;
					
					//try {
						if ((line.length() < 3) && (isNumeric(line))) {
							x = Integer.parseInt(line);
						}
						if (line.contains("message/body")) {
							linesToRead = x;
							if (linesToRead == -1) linesToRead = 0;
						}
					//} catch (NumberFormatException nfe) {}
					
					if (linesToRead == 0) reading = false;
					
				} while (reading);
			}
			catch (SocketException e) {
				System.err.println("SocketException detected. removing user '" + user + "' from server.");
				kill();
			}
			catch (Exception e) {}
		}
		return str;
	}
	
	
	
	/**
	 * Checking if a String is numeric
	 * @param str  the String to check
	 * @return boolean value determing whether the String is numeric.
	 */
	private static boolean isNumeric(String str) {
		try {
			int a = Integer.parseInt(str);
			return true;
		}
		catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	
	
	/**
	 * responding to a Socket with a message.
	 * @param str  the message to send.
	 */
	public void respond(String str) {
		if (!str.contains("\r")) str = str.replace("\n", "\r\n");
		
		try {
			if (!socket.isClosed()) {
				OutputStream outputstream = socket.getOutputStream();
				outputstream.write(str.getBytes());
				outputstream.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
