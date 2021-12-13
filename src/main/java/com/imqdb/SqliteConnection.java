package com.imqdb;

import java.sql.*;

public class SqliteConnection {

	public static Connection connect()
	{
		checkDrivers();
		String prefix = "jdbc:sqlite:";
		String location = App.class.getResource("imdb.db").toExternalForm();
		Connection connection;

		try {
			connection = DriverManager.getConnection(prefix + location);
		}
		catch (Exception e) {
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
//			Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not start SQLite Drivers");
			return false;
		}
	}

}
