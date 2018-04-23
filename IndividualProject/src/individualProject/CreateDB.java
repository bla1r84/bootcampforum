package individualProject;

import com.mysql.jdbc.Connection;
import java.sql.*;

public final class CreateDB {
	public Connection conn;
	public static CreateDB db;
	private CreateDB() {
		String url= "jdbc:mysql://localhost/";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "bla1r84";
		String password = "12345";
		try {
			Class.forName(driver).newInstance();
			this.conn = (Connection)DriverManager.getConnection(url,userName,password);
		}
		catch (Exception sqle) {
			sqle.printStackTrace();
		}
	} // end CreateDB()

	public static synchronized CreateDB getDbCon() {
		if ( db == null ) {
			db = new CreateDB();
		}
		return db;
	} // end getDbCon()

	public void createDB() {
		PreparedStatement stm = null;
		String createDBQuery = "CREATE DATABASE IF NOT EXISTS bootcampForum;";
		try {
			stm = this.conn.prepareStatement(createDBQuery);
			stm.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // end createDB()

	public void dropDB() {
		PreparedStatement stm = null;
		String createDBQuery = "DROP DATABASE IF EXISTS bootcampForum;";
		try {
			stm = this.conn.prepareStatement(createDBQuery);
			stm.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // end dropDB()

	public void closeConn() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // end closeConn()
}
