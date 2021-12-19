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
			new Query("Select all genres", "qc/q_genres.fxml")
		);

		queries.add(
			new Query("Select all names", "qc/q_names.fxml")
		);

		queries.add(
			new Query("Highest Gross Income by Year", "qc/q_gross_by_year.fxml")
		);

		queries.add(
			new Query("Billion Dollar Club", "qc/q_billion_club.fxml")
		);

		return queries;
	}

}
