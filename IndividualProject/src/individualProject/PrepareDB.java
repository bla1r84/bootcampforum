package individualProject;

import com.mysql.jdbc.Connection;
import java.sql.*;

public final class PrepareDB {
    public Connection conn;
    public static PrepareDB db;
    private PrepareDB() {
        String url= "jdbc:mysql://sql7.freemysqlhosting.net/sql7233928?allowMultiQueries=true";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "sql7233928";
        String password = "JemblQ8qML";
        try {
            Class.forName(driver).newInstance();
            this.conn = (Connection)DriverManager.getConnection(url,userName,password);
        }
        catch (Exception sqle) {
            sqle.printStackTrace();
        }
    } // end PrepareDB()

    public static synchronized PrepareDB getDbCon() {
        if ( db == null ) {
            db = new PrepareDB();
        }
        return db;
    } // end getDbCon()
    
	public void createTables() {
		PreparedStatement stm = null;
		String createTables = 
				"CREATE TABLE IF NOT EXISTS Role (ID int NOT NULL AUTO_INCREMENT, roleName varchar(20) NOT NULL, PRIMARY KEY(ID), UNIQUE (roleName));" + 
				"CREATE TABLE IF NOT EXISTS User (ID int NOT NULL AUTO_INCREMENT, username varchar(20) NOT NULL UNIQUE, password varchar(20), roleID int DEFAULT 3, PRIMARY KEY(ID), FOREIGN KEY (roleID) REFERENCES Role(ID));" +  
				"CREATE TABLE IF NOT EXISTS PrivateMessage (ID int NOT NULL AUTO_INCREMENT, senderID int NOT NULL, receiverID int, messageText varchar(250), dateTimePosted timestamp, PRIMARY KEY(ID), FOREIGN KEY (senderID) REFERENCES User(ID) ON DELETE CASCADE, FOREIGN KEY (receiverID) REFERENCES User(ID) ON DELETE CASCADE);" +
				"CREATE TABLE IF NOT EXISTS Topic (ID int NOT NULL AUTO_INCREMENT, creatorID int, subject varchar (100) NOT NULL, dateTimePosted timestamp, PRIMARY KEY (ID), FOREIGN KEY (creatorID) REFERENCES User(ID) ON DELETE CASCADE);" +
				"CREATE TABLE IF NOT EXISTS TopicMessage (ID int NOT NULL AUTO_INCREMENT, authorID int, topicID int, topicMessageText varchar (1000) NOT NULL, dateTimePosted timestamp, PRIMARY KEY (ID), FOREIGN KEY (authorID) REFERENCES User(ID) ON DELETE CASCADE, FOREIGN KEY (topicID) REFERENCES Topic(ID) ON DELETE CASCADE);";
		try {
			stm = this.conn.prepareStatement(createTables);
			stm.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // end createTables()

	public void insertDummyValues() {
		PreparedStatement stm = null;
		String insertDummies = 
				"INSERT INTO Role (roleName) VALUES (\"ADMIN\");" +
						"INSERT INTO Role (roleName) VALUES (\"MODERATOR\");" +
						"INSERT INTO Role (roleName) VALUES (\"USER\");" +
						"INSERT INTO User (username,password,roleID) VALUES (\"user1\",\"123\",1);" +
						"INSERT INTO User (username,password,roleID) VALUES (\"user2\",\"123\",2);" +
						"INSERT INTO User (username,password,roleID) VALUES (\"user3\",\"123\",3);";

		try {
			stm = this.conn.prepareStatement(insertDummies);
			stm.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // end insertDummyValues()
	
	public void closeConn() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // end closeConn()
}