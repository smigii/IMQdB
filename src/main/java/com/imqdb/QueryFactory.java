package com.imqdb;

import java.util.ArrayList;

public class QueryFactory {

	public static ArrayList<Query> generate()
	{
		ArrayList<Query> queries = new ArrayList<Query>();

		queries.add(
			new Query("Query 1", "query1.fxml")
		);

		queries.add(
			new Query("Query 2", "query2.fxml")
		);

		return queries;
	}

}
