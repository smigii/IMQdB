package imqdb;

import imqdb.db.IDatabase;
import imqdb.db.LocalDatabase;
import imqdb.db.SqliteConnection;

public class Services {

	private static IDatabase db = null;

	public static IDatabase getDatabase()
	{
		if(db == null) {
			db = new LocalDatabase();
		}
		return db;
	}

	public static void init()
	{
		SqliteConnection.connect();
	}

}
