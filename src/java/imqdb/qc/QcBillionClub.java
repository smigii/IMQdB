package imqdb.qc;

import imqdb.QueryController;
import imqdb.UtilQueryCache;
import imqdb.UtilQueryPair;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcBillionClub implements QueryController {

	@FXML ChoiceBox<UtilQueryPair> titleBox;

	@FXML
	public void initialize()
	{
		titleBox.getItems().add(UtilQueryPair.ANY);
		titleBox.getItems().addAll(UtilQueryCache.getTitles());
		titleBox.setValue(UtilQueryPair.ANY);
	}

	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		String titleSection = "";
		if(!titleBox.getValue().getId().equals("*")) {
			titleSection = "where titles.title_id = \"" + titleBox.getValue().getId() + "\"";
		}

		PreparedStatement ps = db.prepareStatement(
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
				titleSection
		);

		return ps.executeQuery();
	}
}
