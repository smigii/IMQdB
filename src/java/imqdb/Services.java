package imqdb;

import imqdb.db.IDatabase;
import imqdb.db.LocalDatabase;

public class Services {

	private static IDatabase db = null;

	public static IDatabase getDatabase()
	{
		if(db == null) {
			db = new LocalDatabase();
		}
		return db;
	}

}
