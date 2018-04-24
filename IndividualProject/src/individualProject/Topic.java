package individualProject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class Topic {
	private int topicID;
	private String subject;
	private Date date;
	private static int counter;

	public int getTopicID() {
		return topicID;
	}


	public String getSubject() {
		return subject;
	}

	public Date getDate() {
		return date;
	}

	public Topic(String subject) {
		this.topicID = ++counter;
		this.subject = subject;
		Date currentDateTime = new Date();
		this.date = currentDateTime;
	}

	public static void process(User myUser, int topicID) throws SQLException {
		int topicOption = 0;
		ResultSet rs = null;
		do {
			rs = Forum.retrieveTopicByID(topicID);
			if (rs!=null) {
				displayTopic(topicID);
				if (myUser.getRole().equals(Role.ADMIN) || myUser.getRole().equals(Role.MODERATOR)) {
					topicOption = Topic.chooseTopicOption(0,4,myUser);
					topicAction(topicOption,myUser,topicID);
				}else {
					topicOption = Topic.chooseTopicOption(0,1,myUser);
					topicAction(topicOption,myUser,topicID);
				}
			}else {
				System.out.println("This topic does not exist!");
			}
		}while(topicOption!=0 && rs!=null);
	} // end process()

	public static void displayTopic(int topicID) throws SQLException{
		System.out.println("****** Topic View ******");
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		ResultSet rs = Forum.retrieveTopicByID(topicID);
		String topicSubject = rs.getString(3);
		System.out.format("Topic ID: %d\nTopic Subject: %s\n",topicID,topicSubject);
		System.out.println("--------------------------------------------------------------------------");
		String retrieveMessages = "SELECT TopicMessage.ID, User.username, TopicMessage.topicMessageText, TopicMessage.dateTimePosted FROM TopicMessage INNER JOIN User on TopicMessage.authorID = User.ID where TopicMessage.topicID = " + topicID + " ORDER BY TopicMessage.dateTimePosted DESC;";
		ResultSet messagesRS = dao.query(retrieveMessages);
		if (!messagesRS.next())
			System.out.println("There are no messages in this topic.");
		else {
			messagesRS.beforeFirst();
			while(messagesRS.next()) {
				int topicMessageID = messagesRS.getInt(1);
				String username = messagesRS.getString(2);
				String topicMessageText = messagesRS.getString(3);
				Timestamp timestamp = messagesRS.getTimestamp(4);
				System.out.format("%s\nMessage ID: #%d Posted by: %s on %s\n",topicMessageText,topicMessageID, username,timestamp);
				System.out.println("--------------------------------------------------------------------------");
			}
		}
	} // end displayTopic()


	public static void topicOptionsAdmin() {
		System.out.println("****** Topic options ******");
		System.out.print("Press:\n" +
				"1 - to post a new reply\n" +
				"2 - to edit the topic's subject\n" +
				"3 - to edit a message\n" +
				"4 - to delete a message\n" +
				"0 - to exit the topic\n" +
				"Choose: ");
	} // end topicOptionsAdmin()

	public static void topicOptionsUser() {
		System.out.print("Press:\n" +
				"1 - to post a new reply\n" +
				"0 - to exit the topic\n" +
				"Choose: ");
	} // end topicOptionsUser()

	public static int chooseTopicOption(int lowestOption, int highestOption, User myUser){
		int choice;
		do {
			try{
				if (myUser.getRole() == Role.ADMIN || myUser.getRole() == Role.MODERATOR){
					topicOptionsAdmin();
				}else {
					topicOptionsUser();
				}
				choice = Integer.parseInt(Main.sc.nextLine());
			}catch (Exception e) {
				System.out.println("Invalid input!");
				choice = -1;
			}
			if (choice < lowestOption || choice > highestOption)
				System.out.println("Please choose one of the available options!");
		}while(choice < lowestOption || choice > highestOption);
		return choice;
	} // end chooseTopicOption()

	public static void topicAction(int topicOption, User myUser, int topicID) throws SQLException {
		if (myUser.getRole() == Role.ADMIN || myUser.getRole() == Role.MODERATOR) {
			switch (topicOption) {
			case 1:
				User.checkUserStatus();
				postReply(topicID,myUser);
				break;
			case 2:
				User.checkUserStatus();
				editSubject(topicID);
				break;
			case 3:
				User.checkUserStatus();
				editMessage(getMessageID());
				break;
			case 4:
				User.checkUserStatus();
				deleteMessage(getMessageID());
			}
		}else
		{
			switch (topicOption) {
			case 1:
				User.checkUserStatus();
				postReply(topicID,myUser);
				break;
			}
		}
	} // end topicAction()

	public static void postReply(int topicID, User myUser){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String selectTopic = "SELECT ID FROM Topic WHERE ID = " + topicID + " ;";
		try {
			ResultSet rs = dao.query(selectTopic);
			if (rs.next()) {
				System.out.println("Type your message: ");
				String messageText = Main.sc.nextLine();
				messageText = messageText.replace("'", "''");
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String insertNewMessage = "INSERT INTO TopicMessage (authorID, topicID, topicMessageText,dateTimePosted) VALUES ("+ myUser.getUserID() + "," + topicID + ",'" + messageText + "','" + timestamp + "');";
				User.checkUserStatus();
				if (dao.insert(insertNewMessage)>0)
					System.out.println("New reply posted successfully to this topic!");
				else
					System.out.println("Hmmm, it seems that the whole topic was deleted in the meantime. Strange world, isn't it?");
			}
			else {
				System.out.println("We are truly sorry, but it seems that the topic no longer exists.\nRedirecting you to the forum's main screen...");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	} // end postReply()

	public static void editSubject(int topicID) {
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String selectTopic = "SELECT ID FROM Topic WHERE ID = " + topicID + " ;";
		try {
			ResultSet rs = dao.query(selectTopic);
			if (rs.next()) {
				System.out.print("Type new subject: ");
				String newSubject = Main.sc.nextLine();
				newSubject = newSubject.replace("'","''");				
				String editSubject = "UPDATE Topic SET subject = '" + newSubject + "' WHERE ID = " + topicID + " ;";
				User.checkUserStatus();
				if (dao.insert(editSubject)>0)
					System.out.println("Subject edited successfully!");
				else
					System.out.println("Hmmm, it seems that the whole topic was deleted in the meantime. Strange world, isn't it?");
			}
			else {
				System.out.println("We are truly sorry, but it seems that the topic no longer exists.\nRedirecting you to the forum's main screen...");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	} // end editSubject()

	public static void editMessage(int messageID) {
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String selectMessage = "SELECT ID FROM TopicMessage WHERE ID = " + messageID + " ;";
		try {
			ResultSet rs = dao.query(selectMessage);
			if (rs.next()) {
				System.out.print("Type the new text: ");
				String newText = Main.sc.nextLine();
				newText = newText.replace("'","''");
				String editMessage = "UPDATE TopicMessage SET topicMessageText = '" + newText + "' WHERE ID = " + messageID + " ;";
				User.checkUserStatus();
				if(dao.insert(editMessage)>0)
					System.out.println("Message edited successfully!");
				else {
					System.out.println("Hmmm, it seems that the message was deleted in the meantime. Where could it be?!");
				}
			}else {
				System.out.println("No message found with that ID");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	} // end editMessage()

	public static void deleteMessage(int messageID){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String deleteMessage = "DELETE FROM TopicMessage WHERE ID = " + messageID + " ;";
		User.checkUserStatus();
		try {
			if (dao.insert(deleteMessage) > 0)
				System.out.println("Message deleted successfully");
			else
				System.out.println("Message not found. Please verify the ID of the message you would like to delete.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} // end deleteMessage()

	public static int getMessageID() {
		System.out.print("Enter message ID: ");
		int messageID;
		try{
			messageID = Integer.parseInt(Main.sc.nextLine());
		}catch (Exception e) {
			messageID = -1;
		}
		return messageID;
	} // end getMessageID()

	public static int selectTopic() {
		System.out.print("Type topic ID: ");
		int topicID;
		do {
			try{
				topicID = Integer.parseInt(Main.sc.nextLine());
			}catch(Exception e) {
				topicID = -1;
			}
		}while(topicID == -1);
		return topicID;
	} // end selectTopic()
} // end of class
