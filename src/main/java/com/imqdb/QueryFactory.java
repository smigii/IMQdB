package com.imqdb;

import java.util.ArrayList;

public class QueryFactory {

	public final static Query dummy = new Query(
		"-- Select a query! --", "query_dummy.fxml"
	);

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
