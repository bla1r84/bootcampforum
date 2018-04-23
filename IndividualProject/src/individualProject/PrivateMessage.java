package individualProject;
import java.sql.SQLException;
import java.util.Date;

public class PrivateMessage {
	private int senderID;
	private int receiverID;
	private String text;
	private Date date;

	public PrivateMessage(int senderID, int receiverID, String text, Date date) {
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.text = text;
		this.date = date;
	}

	public int getSenderID() {
		return senderID;
	}

	public int getReceiverID() {
		return receiverID;
	}

	public String getText() {
		return text;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "PrivateMessage [senderID=" + senderID + ", receiverID=" + receiverID + ", text=" + text + ", date="
				+ date + "]";
	}

	public static void sendPrivateMessage(User sender, User receiver, String messageText, Date dateTimePosted){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		if (User.findUserByUsername(receiver.getUsername()) == null)
			System.out.println("Unlucky! The receiving user was deleted while you were typing the message!");
		else {
			String insertPrivateMessage = "INSERT INTO PrivateMessage (senderID, receiverID, messageText, dateTimePosted) VALUES (" + sender.getUserID() + ", " + receiver.getUserID() + ", '" + messageText + "', '" + dateTimePosted + "');";
			try {
				dao.insert(insertPrivateMessage);
				System.out.println("Private message sent successfully to " + receiver.getUsername() + "!");
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	} // end sendPrivateMessage()


}
