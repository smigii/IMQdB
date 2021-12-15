package imqdb;

import java.util.ArrayList;

public class QueryFactory {

	public final static Query dummy = new Query(
		"~~ Select a query! ~~", "q_dummy.fxml"
	);

	public static ArrayList<Query> generate()
	{
		ArrayList<Query> queries = new ArrayList<Query>();

		queries.add(
			new Query("Select all genres", "q_genres.fxml")
		);

		queries.add(
			new Query("Select all names", "q_names.fxml")
		);

		queries.add(
			new Query("Highest Gross Income by Year", "q_gross_by_year.fxml")
		);

		queries.add(
			new Query("Billion Dollar Club", "q_billion_club.fxml")
		);

		return queries;
	}

}
