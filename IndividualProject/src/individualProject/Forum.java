package individualProject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Forum {

	public static int process(User myUser) throws SQLException {

		int forumOption = 0;

		if (myUser.getRole() == Role.ADMIN || myUser.getRole() == Role.MODERATOR){
			do {
				displayTopics();
				do {
					displayOptions(myUser);
					forumOption= chooseForumOption();
					if (forumOption<0 || forumOption>3)
						System.out.println("Please select one of the numbers in the menu!");
				}while (forumOption<0 || forumOption>3);
				switch (forumOption){
				case 1:
					int topicID = selectTopic(); 
					Topic.process(myUser,topicID);
					break;
				case 2:
					createTopic();
					break;
				case 3:
					topicID = selectTopic();
					deleteTopic(topicID);
					break;
				case 0:
					return 0;
				}
			}while(forumOption !=0); 
			return 0;
		}else {
			do {
				displayTopics();
				do {
					displayOptions(myUser);
					forumOption= chooseForumOption();
					if (forumOption<0 || forumOption>2)
						System.out.println("Please select one of the numbers in the menu!");
				}while (forumOption<0 || forumOption>2);
				switch (forumOption){
				case 1:
					int topicID = selectTopic(); 
					Topic.process(myUser,topicID);
					break;
				case 2:
					createTopic();
					break;
				case 0:
					return 0;
				}
			}while(forumOption !=0);
			return 0;
		}
	} // end process()


	public static void displayOptions(User myUser){
		System.out.println("--------------------------------------------------------------------------");
		if (myUser.getRole() == Role.ADMIN || myUser.getRole() == Role.MODERATOR){
			System.out.print("Press:\n" +
					"1 - to view a topic\n" +
					"2 - to create a new topic\n" +
					"3 - to delete a topic\n" +
					"0 - to exit the forum\n" +
					"Choose: ");
		}else{
			System.out.print("Press:\n" +
					"1 - to view a topic\n" +
					"2 - to create a new topic\n" +
					"0 - to exit the forum\n" +
					"Choose: ");
		}
	} // end displayOptions()

	public static int chooseForumOption(){
		int choice;
		try{
			choice = Integer.parseInt(Main.sc.nextLine());
		}catch (Exception e) {
			System.out.println("Invalid input!");
			return -1;
		}
		return choice;
	} // end chooseForumOption()


	public static void displayTopics(){
		System.out.println("****** Forum ******");
		System.out.println("****** Topic list ******");

		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String displayTopics = "SELECT Topic.ID, User.username, Topic.subject, Topic.dateTimePosted FROM Topic INNER JOIN User on Topic.creatorID = User.ID";
		try {
			ResultSet rs = dao.query(displayTopics);
			if (!rs.next()) {
				System.out.println("No topics have been posted yet!");
			}else {
				rs.beforeFirst();
				while(rs.next()) {
					int topicID = rs.getInt(1);
					String username = rs.getString(2);
					String topicSubject = rs.getString(3);
					Timestamp dateTimePosted = rs.getTimestamp(4);
					String formattedDate = DateConvert.dateFormat(dateTimePosted);
					System.out.format("%4d.\t%s\t%s Created by: %s\n",topicID,topicSubject,formattedDate,username);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} // end displayTopics()


	public static void createTopic(){
		System.out.print("Topic subject: ");
		String topicSubject = Main.sc.nextLine();
		topicSubject = topicSubject.replace("'","''");
		System.out.println("Message:");
		String topicFirstMessage = Main.sc.nextLine();
		topicFirstMessage = topicFirstMessage.replace("'","''");
		Timestamp dateTimePosted = new Timestamp(System.currentTimeMillis());
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String insertTopic = "INSERT INTO Topic (creatorID,subject,dateTimePosted) VALUES (" + Main.loggedUser.getUserID() + ", '" + topicSubject + "','" + dateTimePosted + "');";
		try {
			dao.insert(insertTopic);
			String getLastID = "SELECT LAST_INSERT_ID()";
			ResultSet rs = dao.query(getLastID);
			rs.next();
			int newTopicID = rs.getInt(1);
			String insertTopicMessage = "INSERT INTO TopicMessage (authorID,topicID,topicMessageText,dateTimePosted) VALUES (" + Main.loggedUser.getUserID() + "," + newTopicID + ", '" + topicFirstMessage + "','" + dateTimePosted + "');";
			dao.insert(insertTopicMessage);
			System.out.println("New topic created successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} // end createTopic()

	public static void deleteTopic(int topicID){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		if (topicID != -1) {
			String emptyTopic = "DELETE FROM TopicMessage WHERE topicID = " + topicID + ";";
			try {
				dao.insert(emptyTopic);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("No topic found with that ID");
			return;
		}
		String deleteTopic = "DELETE FROM Topic WHERE ID = " + topicID +";";
		try {
			int topicDeleted = dao.insert(deleteTopic);
			if (topicDeleted > 0)
				System.out.println("Topic deleted successfully.");
			else
				System.out.println("Topic does not exist!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} // end deleteTopic()

	public static int selectTopic() {
		int topicID;
		do {
			try{
				displayTopics();
				System.out.print("Type topic ID: ");
				topicID = Integer.parseInt(Main.sc.nextLine());
			}catch(Exception e) {
				topicID = -1;
				System.out.println("Invalid input!");
			}
		}while(topicID == -1);
		return topicID;
	} // end selectTopic()

	public static int findTopicByID(int topicID) {
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String findTopicByID = "SELECT * FROM Topic WHERE ID = " + topicID + ";";
		try {
			ResultSet rs = dao.query(findTopicByID);
			if (rs.next())
				return topicID;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return -1;
	} // end findTopicByID()

	public static ResultSet retrieveTopicByID(int topicID){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String findTopicByID = "SELECT * FROM Topic WHERE ID = " + topicID + ";";
		try {
			ResultSet rs = dao.query(findTopicByID);
			if (rs.next()) 
				return rs;

		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	} // end retrieveTopicByID()
} // end of class
