package individualProject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EditUser {

	public static int process(User selectedUser) throws SQLException{
		int choice = 0;
		ResultSet rs = null;
		do {
			rs = User.retrieveUserByID(selectedUser.getUserID());
			if (rs!=null) {
				do {
					editUserOption(selectedUser);
					choice = chooseEditUserOption();
					if (choice<0 || choice>3)
						System.out.println("Please select one of the numbers in the menu!");
				}while(choice<0 || choice>3);
				switch (choice) {
				case 1:
					User.checkUserStatus();
					LoggedUserPanel.changePassword(selectedUser);
					break;
				case 2:
					User.checkUserStatus();
					changeRole(selectedUser);
					break;
				case 3:
					User.checkUserStatus();
					deleteUser(selectedUser);
					return 0;
				case 0:
					User.checkUserStatus();
					return 0;
				}
			}
			else {
				System.out.println("This user does not exist anymore!\nRedirecting you to previous page.");
			}
		}while(choice!=0 && rs!=null);
		return 0;
	} // end  process()

	public static void editUserOption(User selectedUser){
		System.out.format("Editing user \"%s\"\n",selectedUser.getUsername());
		System.out.print("Press:\n" +
				"1 - to modify the user's password \n" +
				"2 - to change the user's role\n" +
				"3 - to delete the user\n" +
				"0 - to go back\n" +
				"Choose: ");
	} // end editUserOption()

	public static int chooseEditUserOption(){
		int choice;
		try{
			choice = Integer.parseInt(Main.sc.nextLine());
		}
		catch(Exception e){
			System.out.println("Invalid input!");
			return -1;
		}
		return choice;
	} // end chooseEditUserOption()

	public static void changeRole(User selectedUser){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		selectedUser = User.searchUserByID(selectedUser.getUserID());
		if (selectedUser == null) {
			System.out.println("Too late, too late! The user no longer exists!");
			return;
		}
		System.out.format("User's current role: %s\n", selectedUser.getRole());
		int role = AdminPanel.chooseRole();
		selectedUser = User.searchUserByID(selectedUser.getUserID());
		if (selectedUser == null)
			return;
		String changeRole = "UPDATE User SET roleID = " + role + " WHERE ID = " + selectedUser.getUserID() + ";";
		User.checkUserStatus();
		try {
			dao.insert(changeRole);
			switch (role) {
			case 1:
				System.out.println("This user is now an Administrator.");
				break;
			case 2:
				System.out.println("This user is now a Moderator.");
				break;
			case 3:
				System.out.println("This user is now a Standard User.");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	} // end changeRole()

	public static void deleteUser(User selectedUser){
		DatabaseConnection dao = DatabaseConnection.getDbCon();
		selectedUser = User.searchUserByID(selectedUser.getUserID());
		if (selectedUser == null) {
			System.out.println("It seems that someone beat you to it. The user has already been deleted.");
			return;
		}
		else {
			int userID = selectedUser.getUserID();
			String deleteUser = "DELETE FROM User WHERE ID = " + userID + ";";
			User.checkUserStatus();
			try {
				dao.insert(deleteUser);
				System.out.println("User deleted successfully!");
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	} // end deleteUser()
} // end of class
