import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Empfang extends Thread{

	Socket socket = null;
	public boolean running = true;
	String user;
	List<String> response;

	
	/**
	 * Settings up the Listener for recieved messages
	 * @param s  the Socket for incoming messages
	 * @param u  the user name
	 * @return new Instance of the listener
	 * @author github.com/oliverrascheja
	 */
	public Empfang(Socket s, String u) {
		socket = s;
		user = u;
	}

	
	
	/**
	 * Processing and displaying the recieved message. 
	 */
	public void run(){

		while (running) {
			try {
				response = getResponse(socket);
				if (response.size() > 2) {
					if (response.get(1).contains("user text notify")) {
						if (response.get(3).contains(user)) {
							for (int l = 5; l <= 5 + Integer.parseInt(response.get(4)); l++) {
								if (!response.get(l).contains("message/body")) {
									System.out.println("(" + response.get(2) + ") " + response.get(l));

									if (response.get(l).contains("INFO: Server is shutting down.")) {
										Client.leaveUser(socket, user);
										kill();
									}
								}
							}
						}
					}
					else if (response.get(1).contains("error")){
						for (int l = 3; l <= response.size() - 1; l++) {
							if (!response.get(l).contains("message/body")) {
								System.out.println(response.get(l));
							}
						}
					}
				}

				Thread.sleep(500);
			} catch (Exception e) {}
		}
	}

	
	
	/**
	 * Stopping the Thread.
	 */
	public void kill() {
		while (running) {
			try {
				running = false;
				Thread.sleep(50);
			}
			catch (Exception e) {	
			}

		}
	}
	

	
	/**
	 * Fetching the message from the server. The full formatted message will be saved in a list if strings.
	 * @param socket  the Socket containing a new message
	 * @return a list of string containing the revieved message from the server
	 * @throws errors when socket is not existing or on other parsing difficulties
	 * @author github.com/oliverrascheja
	 */
	static List<String> getResponse(Socket socket) throws IOException {
		
		if (socket != null) {
			List<String> str = new ArrayList<String>();

			boolean reading = true;
			int linesToRead = -1, x = -1;
			
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				do {
					String line = br.readLine();
					str.add(line);
					linesToRead--;
					
					try {
						if (x == -1) x = Integer.parseInt(line);
						if (line.contains("message/body")) linesToRead = x;
					} catch (NumberFormatException nfe) {}
					
					if (linesToRead == 0) reading = false;
					
				} while (reading);

				return str;
			}
			catch (Exception e) {}
		}

		return null;
	}
}
