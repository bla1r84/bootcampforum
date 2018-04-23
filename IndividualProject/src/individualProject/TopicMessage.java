package individualProject;

import java.util.Date;

public class TopicMessage {
    private int topicMessageID;
    private int AuthorID;
    private String topicMessageText;
    private Date date;

    private static int counter;

    public int getTopicMessageID() {
        return topicMessageID;
    }

    public int getAuthorID() {
        return AuthorID;
    }

    public String getTopicMessageText() {
        return topicMessageText;
    }

    public void setTopicMessageText(String topicMessageText) {
		this.topicMessageText = topicMessageText;
	}

	public Date getDate() {
        return date;
	}

    public void setDate(Date date) {
		this.date = date;
	}

	public TopicMessage(int authorID, String topicMessageText) {
        this.topicMessageID = ++counter;
        AuthorID = authorID;
        this.topicMessageText = topicMessageText;
        Date currentDateTime = new Date();
        this.date = currentDateTime;
    } // end TopicMessage()
} // end of class
