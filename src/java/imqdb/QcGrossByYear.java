package imqdb;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;

import java.sql.*;

public class QcGrossByYear implements QueryController {

	@FXML ChoiceBox<String> genreBox;
	@FXML Spinner<Integer> minYear;
	@FXML Spinner<Integer> maxYear;
	@FXML RadioButton radioDomestic;
	@FXML RadioButton radioWorldwide;

	@FXML
	public void initialize()
	{
		radioDomestic.fire();
		try {
			Connection connection = SqliteConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement("select genre from genres");
			ResultSet rs = ps.executeQuery();
			genreBox.getItems().add("Any");
			while (rs.next()) {
				genreBox.getItems().add(rs.getString("genre"));
			}
			genreBox.setValue("Any");
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		// Genre selection
		String genre_section = "";
		String genre_selection = genreBox.getValue();
		if(!genre_selection.equals("Any")) {
			genre_section = "genres.genre = \"" + genre_selection + "\" and";
		}

		// Region selection
		String region;
		String regionAlias;
		if(radioDomestic.isSelected()) {
			region = "usa_gross_income";
			regionAlias = "Domestic Income";
		} else {
			region = "worldwide_gross_income";
			regionAlias = "Worldwide Income";
		}

		PreparedStatement ps = db.prepareStatement(
			"select movies.original_title as \"Title\", movies.year as \"Year\", movies." + region + " as \"" + regionAlias + "\" from movies\n" +
				"natural join movie_genre natural join genres\n" +
				"where " + genre_section + " movies.year >= " + minYear.getValue() + " and movies.year <= " + maxYear.getValue() + "\n" +
				"group by movies.year\n" +
				"having max(movies." + region + ")\n" +
				"order by movies.year DESC"
		);

		return ps.executeQuery();
	}
}
