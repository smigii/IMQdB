package imqdb;

import java.util.ArrayList;

public class QueryFactory {

	public final static Query dummy = new Query(
		"~~ Select a query! ~~", "qc/q_dummy.fxml"
	);

	public static ArrayList<Query> generate()
	{
		ArrayList<Query> queries = new ArrayList<Query>();

		queries.add(
			new Query("Table Browser", "qc/q_all_tables.fxml")
		);

		queries.add(
			new Query("Highest Gross Income by Year", "qc/q_gross_by_year.fxml")
		);

		queries.add(
			new Query("Billion Dollar Club", "qc/q_billion_club.fxml")
		);

		queries.add(
			new Query("Movie Rating", "qc/q_movie_rating.fxml")
		);

		queries.add(
				new Query("Movie Duration", "qc/q_movie_duration.fxml")
		);

		return queries;
	}

}
