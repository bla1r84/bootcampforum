package individualProject;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
	public static Scanner sc = new Scanner(System.in);
	public static User loggedUser;


	public static void main(String[] args) throws SQLException {
		/*CreateDB createDB = CreateDB.getDbCon();
		createDB.dropDB();
		createDB.createDB();
		createDB.closeConn();*/
		
		/*
		PrepareDB prepareDB = PrepareDB.getDbCon();
		prepareDB.createTables();
		prepareDB.insertDummyValues();
		prepareDB.closeConn();*/
		
		MainMenu.process();
	}



}
