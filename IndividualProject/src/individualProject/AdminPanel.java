package individualProject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wagu.*;

public class AdminPanel {

	public static User selectedUser;


	public static int process() throws SQLException{
		int choice;
		do {
			showAdminOptions();
			choice = chooseAdminOption();
			if (choice<0 || choice>4)
				System.out.println("Please select one of the numbers in the menu!");
		}while(choice<0 || choice>4);
		switch (choice){
		case 1:
			User.checkUserStatus();
			showAllUsers();
			break;
		case 2:
			User.checkUserStatus();
			int editUserOption = 0;
			showAllUsers();
			int userID = selectUserID();
			selectedUser = User.searchUserByID(userID);
			if (selectedUser!=null) {
				do {
					editUserOption = EditUser.process(selectedUser);
				} while (editUserOption != 0);
			}
			break;
		case 3:
			User.checkUserStatus();
			adminCreateNewUser();
			break;
		case 4:
			User.checkUserStatus();
			deleteUser();
			break;
		case 0:
			User.checkUserStatus();
			return 0;

		}
		return choice;
	} // end process()

	public static void showAdminOptions() {
		System.out.println("****** Administrator Panel ******");
		System.out.print("Press:\n" +
				"1 - to view all users\n" +
				"2 - to edit a user\n" +
				"3 - to create a new user\n" +
				"4 - to delete a user\n" +
				"0 - to exit the Admin Panel\n" +
				"Choose: ");
	} // end showAdminOptions()

	public static int chooseAdminOption(){
		int choice;
		try{
			choice = Integer.parseInt(Main.sc.nextLine());
		}
		catch(Exception e) {
			System.out.println("Invalid input!");
			return -1;
		}
		return choice;
	} // end chooseAdminOption()

	public static void showAllUsers(){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		List<String> headersList = Arrays.asList("User ID","Username","Role");
		String showAllUsers = "SELECT User.ID, User.username, Role.roleName FROM User INNER JOIN Role ON User.roleID = Role.ID ORDER BY Role.roleName;";
		try {
			ResultSet rs = dao.query(showAllUsers);
			List<List<String>> rowsList = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt(1);
				String username = rs.getString(2);
				String role = rs.getString(3);
				List<String> entry = Arrays.asList(Integer.toString(id),username,role);
				rowsList.add(entry);
			}
			Board board = new Board(75);
			Table table = new Table(board, 75, headersList, rowsList);
			List<Integer> colAlignList = Arrays.asList(
					Block.DATA_MIDDLE_LEFT,
					Block.DATA_MIDDLE_LEFT,
					Block.DATA_MIDDLE_LEFT);
			table.setColAlignsList(colAlignList);
			Block tableBlock = table.tableToBlocks();
			board.setInitialBlock(tableBlock);
			board.build();
			String tableString = board.getPreview();
			System.out.println(tableString);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	} // end showAllUsers()

	public static void adminCreateNewUser() throws SQLException{
		System.out.println("****** Create new user screen ******");
		System.out.print("Username: ");
		String username = Main.sc.nextLine();
		username = username.replace("'", "''");
		if (User.findUserByUsername(username)!=null){
			System.out.format("User %s already exists!", username);
			return;
		}
		System.out.print("Password: ");
		String password = Main.sc.nextLine();
		password = password.replace("'", "''");
		int role = chooseRole();
		User.checkUserStatus();
		User.insertUser(username, password, role);
	} // end adminCreateNewUser()

	public static void deleteUser(){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		showAllUsers();
		System.out.println("*WARNING*\nAll topics and messages posted in the forum,\nas well as all private messages sent and received by your selected user will be permanently removed!");
		int userID = selectUserID();
		String deleteUser = "DELETE FROM User WHERE ID = " + userID + ";";
		User.checkUserStatus();
		try {
			if (dao.insert(deleteUser) > 0)
				System.out.println("User deleted successfully!");
			else
				System.out.println("No user found with that ID!");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	} // end deleteUser()

	public static int selectUserID() {
		int userID;
		do {
			try {
				System.out.print("Type user ID: ");
				userID = Integer.parseInt(Main.sc.nextLine());
			}
			catch(Exception e){
				System.out.println("Invalid input!");
				userID = -1;
			}
		}while(userID == -1);
		return userID;
	} // end selectUserID()

	public static int chooseRole() {
		int role;
		do {
			User.checkUserStatus();
			System.out.print("Available roles\n"
					+ "1. Administrator (Full admininistrator access, can delete users etc.)\n"
					+ "2. Moderator (Can only edit/delete posts in the forum)\n"
					+ "3. Standard User\n"
					+ "Choose: "
					);
			try {
				role = Integer.parseInt(Main.sc.nextLine());
			}
			catch(Exception e) {
				System.out.println("Invalid input!");
				role = -1;
			}
			if (role<1 || role>3)
				System.out.println("You need to choose one of the available roles!");
		}while (role<1 || role>3);
		return role;
	} // end chooseRole()

} // end of class


