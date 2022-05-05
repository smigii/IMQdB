package imqdb.qc;

import imqdb.utils.UtilQueries;
import imqdb.utils.UtilQueryPair;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class QcBillionClub implements IQueryController {

	@FXML ChoiceBox<UtilQueryPair> titleBox;

	@FXML
	public void initialize()
	{
		titleBox.getItems().add(UtilQueryPair.ANY);
		titleBox.getItems().addAll(UtilQueries.getTitles());
		titleBox.setValue(UtilQueryPair.ANY);
	}

	@Override
	public String createQuery()
	{
		String titleSection = "";
		if(!titleBox.getValue().getId().equals("*")) {
			titleSection = "where titles.title_id = \"" + titleBox.getValue().getId() + "\"";
		}

		String sql =
				"select\n" +
						"artist.name as Name,\n" +
						"titles.title as \"Artist Job\",\n" +
						"billion_dollar_movies.original_title as Title,\n" +
						"billion_dollar_movies.year as Year,\n" +
						"billion_dollar_movies.worldwide_gross_income as \"Worldwide Gross\"\n" +
						"from billion_dollar_movies\n" +
						"left join title_principals on billion_dollar_movies.imdb_title_id = title_principals.imdb_title_id\n" +
						"left join artist on title_principals.imdb_name_id = artist.imdb_name_id\n" +
						"left join titles on title_principals.title_id = titles.title_id " +
						titleSection;

		return sql;
	}
}
