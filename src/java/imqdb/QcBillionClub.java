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
			Connection connection = SqliteConnection.connect();
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
			titleSection = "and titles.title = \"" + titleSelection + "\"";
		}

		PreparedStatement ps = db.prepareStatement(
			"select artist.name as Name, titles.title as \"Artist Job\", movies.original_title as Title, movies.year as Year, movies.worldwide_gross_income as \"Worldwide Gross\"\n" +
				"from title_principals left join titles on title_principals.title_id = titles.title_id\n" +
				"left join artist on title_principals.imdb_name_id = artist.imdb_name_id\n" +
				"left join movies on title_principals.imdb_title_id = movies.imdb_title_id\n" +
				"where movies.worldwide_gross_income >= 1000000000 " + titleSection
		);

		return ps.executeQuery();
	}
}
