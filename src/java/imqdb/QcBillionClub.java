package imqdb;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcBillionClub implements QueryController {

	@FXML ChoiceBox<String> titleBox;

	@FXML
	public void initialize()
	{
		try {
			Connection connection = SqliteConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement("select title from titles");
			ResultSet rs = ps.executeQuery();
			titleBox.getItems().add("Any");
			while (rs.next()) {
				titleBox.getItems().add(rs.getString("title"));
			}
			titleBox.setValue("Any");
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		String titleSection = "";
		String titleSelection = titleBox.getValue();
		if(!titleSelection.equals("Any")) {
			titleSection = "where titles.title = \"" + titleSelection + "\"";
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
				"left join titles on title_principals.title_id = titles.title_id " + titleSection
		);

		return ps.executeQuery();
	}
}
