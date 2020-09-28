import java.text.SimpleDateFormat;
import java.util.Date;

public class Response {

	
	
	/**
	 * Returning the full message for a specified user
	 * @param sender  the user's name of the sender
	 * @param reciever  the reciever's user's name
	 * @param textMessage  the text message for the reciever
	 * @param lines  number of lines of this message
	 * @return the formatted message to be sent to the reciever
	 * @author github.com/oliverrascheja
	 */
	public static String getNotifyUser(String sender, String receiver, String textMessage, int lines) {
		String msg = "message/head\r\n" + "user text notify\r\n" + sender + "\r\n" + receiver + "\r\n" +
				lines + "\r\n" + "message/body\r\n" + textMessage + "\r\n";
		return msg;
	}
	
	
	
	/**
	 * Returning the full message for a specified group
	 * @param groupName  the group name to recieve this message
	 * @param textMessage  the text message for the reciever
	 * @param lines  number of lines of this message
	 * @return the formatted message to be sent to the group
	 */
	public static String getNotifyGroup(String groupName, String textMessage, int lines) {
		String msg = "message/head\r\n" + "group notify\r\n" + groupName + "\r\n" + lines + "\r\n" +
				"message/body\r\n" + textMessage + "\r\n";
		return msg; 
	}
	
	
	
	/**
	 * Returning a time stamp to a specified user
	 * @return the formatted time stamp message to be sent to the reciever
	 */
	public static String getTimeStamp() {
		SimpleDateFormat datum = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		String DateToStr = datum.format(new Date());
		String msg = "message/head\r\n" + "response time\r\n" + "message/body\r\n" + DateToStr + "\r\n";
		return msg; 
	}
	
	
	
	/**
	 * Returning an error message if the 'message/body' protocol header string is missing
	 * @return an error message if the 'message/body' protocol header string is missing
	 */
	public static String getMissingBodyError() {
		String msg = "message/head\r\n" + "error\r\n" + "1\r\n" + "message/body\r\n" + 
				"The message doesn't contain a body 'message/body'.\r\n";
		return msg; 
	}
	
	
	
	/**
	 * Returning an error message if the message type is wrong
	 * @return an error message if the message type is wrong
	 */
	public static String getWrongMessageTypeError() {
		String msg = "message/head\r\n" + "error\r\n" + "1\r\n" + "message/body\r\n" + 
				"A wrong message type has been transmitted.\r\n";
		return msg;
	}
	
	
	
	/**
	 * Returning an error message if the message is empty
	 * @return an error message if the message is empty
	 */
	public static String getEmptyMessageError() {
		String msg = "message/head\r\n" + "error\r\n" + "1\r\n" + "message/body\r\n" + 
				"The entered message to the server is empty.\r\n";
		return msg;
	}
	
	
	
	/**
	 * Returning an error message if the message header is wrong
	 * @return an error message if the message header is wrong
	 */
	public static String getWrongHeaderError() {
		String msg = "message/head\r\n" + "error\r\n" + "1\r\n" + "message/body\r\n" + 
				"The message header has to start with: message/head\r\n";
		return msg;
	}
	
	
	
	/**
	 * Returning an error message if a specified user already exists on the server
	 * @return an error message if a specified user already exists on the server
	 */
	public static String userAlreadyExistsError(String user) {
		String msg = "message/head\r\n" + "error\r\n" + "1\r\n" + "message/body\r\n" + 
				"user " + user + "already exists.\r\n";
		return msg; 
	}
	
	
	
	/**
	 * Returning an error message if a specified user cannot be found on the server
	 * @return an error message if a specified user cannot be found on the server
	 */
	public static String userDestinationUnknown(String user) {
		String msg = "message/head\r\n" + "error\r\n" + "message/body\r\n" + 
				"user destination '" + user + "' is unknown.\r\n";
		return msg;
	}
	
}
