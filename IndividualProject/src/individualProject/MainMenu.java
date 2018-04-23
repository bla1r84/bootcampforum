package individualProject;

import java.sql.SQLException;

public class MainMenu {
	public static void process() throws SQLException {
		int choice;
		do {            
            do {
            	firstScreen();
  	            choice = chooseFirstScreenOption();        	
	            if (choice<0 || choice>2) {
	            	System.out.println("You need to type one of the numbers in the menu!");
	            	choice = -1;
	            }
            }while(choice == -1);
            switch (choice){
                case 1:
                	Main.loggedUser = loginScreen();
                    while(Main.loggedUser !=null){
                    	LoggedUserPanel.process(Main.loggedUser);
                    }
                    break;
                case 2:
                    userSignUp();
                    break;
                case 0:
                    System.out.println("Quiting application");
                    break;
            }
        }while(choice!=0);
    } // end process()

    public static void firstScreen(){
        System.out.println("****** Welcome to Bootcamp forum ******");
        System.out.print("Press:\n" +
                "1 - to login\n" +
                "2 - to sign up\n" +
                "0 - to exit the application\n" +
                "Choose: ");
    } // end firstScreen()
    
    public static int chooseFirstScreenOption(){
    	int choice;
    	try {
    		choice = Integer.parseInt(Main.sc.nextLine());
    	}
    	catch (Exception e) {
    		System.out.println ("Invalid input!");
    		return -1;
    	}
        return choice;
    } // end chooseFirstScreenOption()

    public static User loginScreen() throws SQLException{
        System.out.println("****** Login screen ******");
        System.out.print("Username: ");
        String username = Main.sc.nextLine();
        System.out.print("Password: ");
        String password = Main.sc.nextLine();
        Main.loggedUser = User.findUserByUsername(username);
        if (Main.loggedUser !=null) {
            boolean passwordCorrect = checkPassword(password, Main.loggedUser);
            if (passwordCorrect) {
                return Main.loggedUser;
            }else{
                System.out.println("Invalid username or password!");
                return null;
            }
        }
        System.out.println("Invalid username or password!");
        return null;
    } // end loginScreen()

    public static void userSignUp() throws SQLException{
        System.out.println("****** Sign up screen ******");
        System.out.print("Username: ");
        String username = Main.sc.nextLine();
        username = username.replace("'","''"); 
    	User myUser = User.findUserByUsername(username);
    	
        if (myUser != null){
            System.out.println("That username is already taken!");
            return;
        }
        System.out.print("Password: ");
        String password = Main.sc.nextLine();
        password = password.replace("'","''");
        User.insertUser(username, password);
    } // end userSignUp()

    public static boolean checkPassword(String password, User myUser){
        if (myUser.getPassword().equals(password))
            return true;
        else
            return false;
    } // end checkPassword()
    
}
