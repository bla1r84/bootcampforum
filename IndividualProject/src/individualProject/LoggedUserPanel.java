package individualProject;

import java.sql.SQLException;

public class LoggedUserPanel {

	public static void process(User myUser) throws SQLException{
		System.out.println("Welcome " + myUser.getUsername() + "!");
		int choice = 0;
		if (myUser.getRole() == Role.ADMIN) {
			do {
				adminUserOptions();
				choice = chooseLoggedUserOption();
				if (choice<0 || choice>4) {
					System.out.println("You need to type one of the numbers in the menu!");
					choice = -1;
				}
				switch (choice) {
				case 1:
					User.checkUserStatus();
					changePassword(Main.loggedUser);
					break;
				case 2:
					User.checkUserStatus();
					int mailboxOption = 0;
					
					do {
						do {
							Mailbox.displayMailboxOptions();
							mailboxOption = Mailbox.chooseMailboxOption();
							if (mailboxOption<0 || mailboxOption>3)
								System.out.println("Please select one of the numbers in the menu!");
						}while(mailboxOption<0 || mailboxOption>3);
						Mailbox.mailboxAction(mailboxOption);
					}while(mailboxOption!=0);
					break;
				case 3:
					User.checkUserStatus();
					int forumOption = 0;
					do {
						forumOption = Forum.process(myUser);
					}while(forumOption!=0);
					break;
				case 4:
					User.checkUserStatus();
					int adminPanelOption = 0;
					do{
						adminPanelOption = AdminPanel.process();
					}while(adminPanelOption!=0);
					break;
				case 0:
					User.checkUserStatus();
					Main.loggedUser = null;
				}

			} while (choice != 0 && Main.loggedUser != null);
		}else{
			do {
				nonAdminOptions();
				choice = chooseLoggedUserOption();
				if (choice<0 || choice>3) {
					System.out.println("You need to type one of the numbers in the menu!");
					choice = -1;
				}
				switch (choice) {
				case 1:
					User.checkUserStatus();
					changePassword(Main.loggedUser);
					break;
				case 2:
					User.checkUserStatus();
					int mailboxOption = 0;
					do {
						Mailbox.displayMailboxOptions();
						mailboxOption = Mailbox.chooseMailboxOption();
						Mailbox.mailboxAction(mailboxOption);
					}while(mailboxOption!=0);
					break;
				case 3:
					User.checkUserStatus();
					int forumOption = 0;
					do {
						forumOption = Forum.process(myUser);
					}while(forumOption!=0);
					break;
				case 0:
					Main.loggedUser = null;
				}


			} while (choice != 0 && Main.loggedUser != null);
		}
	} // end process()

	public static void nonAdminOptions(){
		System.out.print("Press:\n" +
				"1 - to change your password\n" +
				"2 - to view your mailbox\n" +
				"3 - to view the forum\n" +
				"0 - logout\n" +
				"Choose: ");
	} // end nonAdminOptions()

	public static void adminUserOptions(){
		System.out.print("Press:\n" +
				"1 - to change your password\n" +
				"2 - to view your mailbox\n" +
				"3 - to view the forum\n" +
				"4 - to access the Admin Panel\n" +
				"0 - logout\n" +
				"Choose: ");
	} // end adminUserOptions()

	public static int chooseLoggedUserOption(){
		int choice;
		try {
			choice = Integer.parseInt(Main.sc.nextLine());
		}
		catch (Exception e) {
			System.out.println ("Invalid input!");
			return -1;
		}
		return choice;
	} // end chooseLoggedUserOption()

	public static void changePassword(User myUser){
		myUser = User.searchUserByID(myUser.getUserID());
		if (myUser == null) {
			System.out.println("Too late, too late! The user no longer exists!");
			return;
		}
		System.out.println("Change password screen");
		System.out.print("Enter new password: ");
		String password = Main.sc.nextLine();
		password = password.replace("'","''");
		User.checkUserStatus();
		boolean changePasswordSuccessful = User.changePassword(myUser, password);
		if (changePasswordSuccessful) {
			System.out.println("Password changed successfully!");
			if (Main.loggedUser.getUserID() == myUser.getUserID()) {
				System.out.println("You will need to log in again. Returning to main menu...");
				Main.loggedUser = null;
			}
		}
	} // end changePassword()
} // end of class
