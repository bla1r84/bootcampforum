package individualProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


public class Mailbox {
	private static final String FILENAME = "messages.txt";
	private static final String sp = File.separator;

	public static void mailboxAction(int mailboxOption) throws SQLException{
		switch (mailboxOption){
		case 1:
			User.checkUserStatus();
			viewIncomingMessages();
			break;
		case 2:
			User.checkUserStatus();
			viewSentMessages();
			break;
		case 3:
			User.checkUserStatus();
			sendMessage();
			break;
		}
	} // end mailboxAction()

	public static void displayMailboxOptions(){
		System.out.println("****** Mailbox options ******");
		System.out.print("Press:\n" +
				"1 - to view your incoming messages\n" +
				"2 - to view your sent messages\n" +
				"3 - to send a new message to another user\n" +
				"0 - to exit the mailbox\n" +
				"Choose: ");
	} // end displayMailboxOptions()

	public static int chooseMailboxOption(){
		int choice;
		try {
			choice = Integer.parseInt(Main.sc.nextLine());
		}
		catch (Exception e) {
			System.out.println ("Invalid input!");
			return -1;
		}
		return choice;
	} // end chooseMailboxOption()

	public static void viewIncomingMessages() throws SQLException{
		System.out.println("****** Received Messages ******");
		System.out.println("---------------------------");
		retrieveReceivedMessages(Main.loggedUser.getUserID());
	} // end viewIncomingMessages()

	public static void viewSentMessages() throws SQLException{
		System.out.println("****** Sent Messages ******");
		System.out.println("---------------------------");
		retrieveSentMessages(Main.loggedUser.getUserID());
	} // end viewSentMessages()
	
	public static void sendMessage() throws SQLException{
		System.out.println("****** Send Private Message screen");
		System.out.println("Please type the username of the user you would like to send a message to.");
		System.out.print("Username: ");
		String username = Main.sc.nextLine();
		User receivingUser = User.findUserByUsername(username);
		if (receivingUser == null){
			System.out.format("User %s does not exist!\n", username);
			return;
		}
		String text;
		do {
			System.out.println("Please type your message (up to 250 characters).");
			System.out.println("Private Message: ");
			text = Main.sc.nextLine();
			if (text.length()>250)
				System.out.format("Your message contains more than 250 characters (%d characters).\n",text.length());
		}while(text.length()>250);
		text = text.replace("'", "''");
		Timestamp currentDateTime = new Timestamp(System.currentTimeMillis());
		User.checkUserStatus();
		PrivateMessage newMessage = new PrivateMessage(Main.loggedUser.getUserID(),receivingUser.getUserID(),text,currentDateTime);
		PrivateMessage.sendPrivateMessage(Main.loggedUser, receivingUser, text, currentDateTime);
		writeMessageToFile(newMessage.toString());
	} // end sendMessage()
	
	public static void retrieveReceivedMessages(int receiverID) throws SQLException{
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String retrieveSentMessagesQuery = "SELECT User.username, PrivateMessage.dateTimePosted, PrivateMessage.messageText FROM PrivateMessage INNER JOIN User ON PrivateMessage.senderID = User.ID WHERE receiverID = " + receiverID + " ORDER BY PrivateMessage.dateTimePosted DESC;";
		ResultSet rs = dao.query(retrieveSentMessagesQuery);
		if (!rs.next()) {
			System.out.println("You have received no messages yet.");
			System.out.println("------------------------------");
		}else {
			rs.beforeFirst();
			while (rs.next()) {
				String username = rs.getString(1);
				Timestamp dateTimePosted = rs.getTimestamp(2);
				String messageText = rs.getString(3);
				System.out.format("Receiver: %s\nDate Sent: %s\nPrivate Message:\n%s\n",username,dateTimePosted,messageText);
				System.out.println("------------------------------");
			}
		}
	} // end retrieveReceivedMessages()

	public static void retrieveSentMessages(int senderID) throws SQLException{
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String retrieveSentMessagesQuery = "SELECT User.username, PrivateMessage.dateTimePosted, PrivateMessage.messageText FROM PrivateMessage INNER JOIN User ON PrivateMessage.receiverID = User.ID WHERE senderID = " + senderID + " ORDER BY PrivateMessage.dateTimePosted DESC;";
		ResultSet rs = dao.query(retrieveSentMessagesQuery);
		if (!rs.next()) {
			System.out.println("You have sent no messages yet.");
			System.out.println("------------------------------");
		}else {
			rs.beforeFirst();
			while (rs.next()) {
				String username = rs.getString(1);
				Timestamp dateTimePosted = rs.getTimestamp(2);
				String formattedDate = DateConvert.dateFormat(dateTimePosted);
				String messageText = rs.getString(3);
				System.out.format("Receiver: %s\nDate Sent: %s\nPrivate Message:\n%s\n",username,formattedDate,messageText);
				System.out.println("------------------------------");
			}
		}
	} // end retrieveSentMessages()
	
	public static void writeMessageToFile(String message) {
		String jarPath = "";
		try {
			jarPath = URLDecoder.decode(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String completePath = jarPath.substring(0, jarPath.lastIndexOf("/")) 
				+ sp + FILENAME;
		File f = new File(completePath);
		BufferedWriter writer;
		try {
			if (!f.exists() && !f.createNewFile()) {
				System.out.println("File doesn't exist, and creating file with path: " + completePath + " failed. ");

			} else {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(completePath,true), "UTF8"));
				writer.write(message + "\n\n");
				writer.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end writeMessageToFile()
} // end of class
