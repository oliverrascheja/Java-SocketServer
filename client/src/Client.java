import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.Scanner;

public class Client {

	static Socket socket;
	
	
	
	/**
	 * Main Constructor class for the Client. Connecting to a server and 
	 * creating a loop so the user can enter commands and messages.
	 * @throws IOException if the Server is not reachable
	 * @throws ParseException if the IP Address from the local text file cannot be parsed
	 * @return new Instance of a client
	 * @author github.com/oliverrascheja
	 */
	public static void main(String[] args) throws IOException, ParseException {
		
		Scanner scanner = new Scanner(System.in);
		String benutzer = "";
		String empfaenger = "";
		
		
		if (args.length < 1) {
			System.out.println("a user name is missing!");
			do {
				System.out.print("Please enter a user name: ");
				benutzer = scanner.nextLine().split(" ")[0];
				System.out.print("");
			} 
			while (benutzer.equals("") || benutzer.contains(":") || benutzer.contains("Server"));
		}
		else {
			benutzer = args[0];
		}
		

		socket = connect(getServerIP(), 44445);
		joinUser(socket, benutzer);
		
		Empfang empfang = new Empfang(socket, benutzer);
		empfang.start();
		
		System.out.println("Enter a message ('stop' or 'exit' to end this program).");
		
		boolean repeat = true;
		try {
			while(repeat) {
				String eingabe = scanner.nextLine();
				if ((eingabe.contains("exit")) || (eingabe.equals("stop")))  {
					repeat = false;
				}
				else {
					empfaenger = eingabe.split(":")[0];
					String message = eingabe.replace(empfaenger + ":", "");
					textNotify(socket, benutzer, message, empfaenger);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		scanner.close();
		empfang.kill();

		leaveUser(socket, benutzer);
	}


	
	/**
	 * Reads the server IP address from a locally stored text file
	 */
	static String getServerIP() {
		try {
			FileInputStream stream = new FileInputStream("server-ip.txt");
			InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
			BufferedReader buffer = new BufferedReader(reader);
			
			String serverIP = buffer.readLine();

			buffer.close();
			reader.close();
			stream.close();

			return serverIP;
		}
		catch (IOException e) {
			System.out.println("'server-ip.txt' not found!");
			System.exit(1);
			return null;
		}
		
	}
	

	
	/**
	 * Connects to a server
	 * @param adresse  the ip address to a server
	 * @param port  the port to the server
	 * @return new connected Socket to the specified server
	 * @author github.com/oliverrascheja
	 */
	static Socket connect(String adresse, int port) {
		System.out.println("Connecting to '" + adresse + "' ...");
		Socket socket = null;
		try {
			socket = new Socket(adresse, port);
		} catch (Exception e) {
			System.out.println("Couldn't establish a connection to the server! Program is being terminated.");
			System.err.println(e.toString());
			System.exit(0);
		}
		String socketAddress = socket.getInetAddress() + ":" + socket.getPort();
		socketAddress = socketAddress.replace("/", "");
		System.out.println("Connected to " + socketAddress);
		return socket;
	}
	
	
	
	/**
	 * Sends a "join to server" command
	 * @param socket  the Socket of this user
	 * @param benutzer  the user name of this Socket.
	 */
	public static void joinUser(Socket socket, String benutzer) {
		String str = "message/head\n" + "user join\n" + benutzer + "\n" + "message/body\n";
		sendMessage(socket, str);
		System.out.println("Logged in as '" + benutzer + "'.");
	}
	
	
	
	/**
	 * Sends a message to another user via server.
	 * @param socket  the Socket of the sender
	 * @param benutzer  the user name of the sender
	 * @param msg  the unfotmatted text message
	 * @param reciever  the reciever's user name
	 */
	public static void textNotify(Socket socket, String benutzer, String msg, String receiver) {
		String str = "message/head\n" + "user text notify\n" + benutzer + "\n" + receiver + "\n" +
				"1\n" + "message/body\n" + msg + "\n"; 
		sendMessage(socket, str);
	}
	
	
	/**
	 * Sends a command to disconnect this user from the server.
	 * @param socket  the Socket of the outgoing command
	 * @param benutzer  the user name
	 * @throws Exception if Server not reachable.
	 */
	static void leaveUser(Socket socket, String benutzer) throws IOException {
		String str = "message/head\r\n" + "user leave\n" + benutzer + "\n" + "message/body\n";
		sendMessage(socket, str);
		System.out.println("User '" + benutzer + "' was disconnected.");
		System.out.println("Chat terminated.");
		
		socket.close();
		
		System.exit(0);
	}
	
	
	
	/**
	 * Sending a message to the Server
	 * @param socket  the Socket of the outgoing message
	 * @param str  the message as String
	 */
	static void sendMessage(Socket socket, String str) {
		if (!str.contains("\r")) str = str.replace("\n", "\r\n");
		
		try {
			OutputStream outputstream = socket.getOutputStream();
			outputstream.write(str.getBytes());
			outputstream.flush();
		} catch (IOException e) {}
	}
	
}