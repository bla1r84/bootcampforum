package individualProject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
	private int userId;
	private String username;
	private String password;
	private Role role;

	public static int userCount;

	public User(String username, String password) {
		this.userId = ++userCount;
		this.username = username;
		this.password = password;
		this.role = Role.USER;
	}

	public User(int userID, String username, String password, Role role) {
		this.userId = userID;
		this.username = username;
		this.password = password;
		this.role = role;

	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public int getUserID() {
		return this.userId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", password=" + password + ", role=" + role + "]";
	}

	public static User findUserByUsername(String username){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		username = username.replace("'", "''");
		String searchTableQuery = "SELECT User.ID,User.username, User.password, Role.roleName FROM User INNER JOIN Role ON User.roleID = Role.ID WHERE User.username = '" + username + "';";
		try {
			ResultSet rs = dao.query(searchTableQuery);
			while (rs.next()) {
				int userID = rs.getInt(1);
				String password = rs.getString(3);
				String role = rs.getString(4);
				if (role.equals("ADMIN"))
					return (new User(userID,username,password,Role.ADMIN));
				else if (role.equals("MODERATOR"))
					return (new User(userID,username,password,Role.MODERATOR));
				else
					return (new User(userID,username,password,Role.USER));
			}
			return null;
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	} // end findUserByUsername()

	public static User searchUserByID(int userID){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String searchTableQuery = "SELECT User.ID, User.username, User.password, Role.roleName FROM User INNER JOIN Role ON User.roleID = Role.ID WHERE User.ID = '" + userID + "';";
		try{
			ResultSet rs = dao.query(searchTableQuery);
			while (rs.next()) {
				String username = rs.getString(2);
				String password = rs.getString(3);
				String role = rs.getString(4);
				if (role.equals("ADMIN"))
					return (new User(userID,username,password,Role.ADMIN));
				else if (role.equals("MODERATOR"))
					return (new User(userID,username,password,Role.MODERATOR));
				else
					return (new User(userID,username,password,Role.USER));
			}
			return null;
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	} // end searchUserByID()
	
	public static ResultSet retrieveUserByID(int userID) {
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String searchTableQuery = "SELECT User.ID, User.username, User.password, Role.roleName FROM User INNER JOIN Role ON User.roleID = Role.ID WHERE User.ID = '" + userID + "';";
		try{
			ResultSet rs = dao.query(searchTableQuery);
			if (rs.next()) {
				return rs;
			}
			else
				return null;
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	} // end retrieveUserByID()

	public static void insertUser(String username, String password, int role) {
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String insertUser = "INSERT INTO User (username,password,roleID) VALUES ('"+ username + "','" + password + "', " + role + ");";
		try{
			dao.insert(insertUser);
			switch (role) {
			case 1:
				System.out.println("New administrator ('" + username + "') created successfully!");
				break;
			case 2:
				System.out.println("New moderator ('" + username + "') created successfully!");
				break;
			case 3:
				System.out.println("New standard user ('" + username + "') created successfully!");
			}
		} catch(SQLException e) {
			if (e.getMessage().contains("Duplicate"))
				System.out.println("Dear oh dear! It seems that someone else beat you to it.\nWhile you were typing the password and choosing the role,\nanother user using the same username was created.");
			else {
				System.out.println("Unknown error occurred! Please check your connection and try again.");
			}
		}
	} // end insertUser()
	
	public static void insertUser(String username, String password){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		String insertUser = "INSERT INTO User (username,password,roleID) VALUES ('"+ username + "','" + password + "',"+ 3 + ");";
		try {
			dao.insert(insertUser);
			System.out.println("You have signed up successfully!");
		} catch(SQLException e) {
			if (e.getMessage().contains("Duplicate"))
				System.out.println("Dear oh dear! It seems that someone else beat you to it.\nWhile you were typing the password and choosing the role,\nanother user using the same username was created.");
			else {
				System.out.println("Unknown error occurred! Please check your connection and try again.");
			}
		}
			
	} // end insertUser()
	
	public static boolean changePassword(User myUser, String password){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		myUser = User.searchUserByID(myUser.getUserID());
		if (myUser == null) {
			System.out.println("Too late! The user was deleted in the meantime.");
			return false;
		}
		String changePassword = "UPDATE User SET password = '" + password + "' WHERE username = '" + myUser.getUsername() + "'";
		try {
			dao.insert(changePassword);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	} // end changePassword()
	
	public static void checkUserStatus() {
		User tempUser = User.searchUserByID(Main.loggedUser.getUserID());
		if (tempUser == null || tempUser.getRole()!=Main.loggedUser.getRole() ) {
			System.out.println("It seems your account has been deleted or updated.\nFor security reasons, no futher action is possible.\nShutting down...");
			System.exit(0);
		}
	}
}
