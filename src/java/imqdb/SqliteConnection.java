package imqdb;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqliteConnection {

	public static Connection connect()
	{
		checkDrivers();
		Connection connection;

		try {
			String prefix = "jdbc:sqlite:";
			String location = App.class.getResource("imdb.db").toExternalForm();
			connection = DriverManager.getConnection(prefix + location);
		}
		catch (Exception e) {
			// TODO: BE BETTER
			System.out.println("UH OH PROBLEM CREATING DB CONNECTION");
			connection = null;
		}

		return connection;
	}

	private static boolean checkDrivers() {
		try {
			Class.forName("org.sqlite.JDBC");
			DriverManager.registerDriver(new org.sqlite.JDBC());
			return true;
		} catch (ClassNotFoundException | SQLException classNotFoundException) {
			Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not start SQLite Drivers");
			return false;
		}
	}

}
