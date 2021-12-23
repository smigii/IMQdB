package imqdb.qc;

import imqdb.QueryController;
import imqdb.utils.UtilQueries;
import imqdb.utils.UtilQueryPair;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;

import java.sql.*;

public class QcGrossByYear implements QueryController {

	@FXML ChoiceBox<UtilQueryPair> genreBox;
	@FXML Spinner<Integer> minYear;
	@FXML Spinner<Integer> maxYear;
	@FXML RadioButton radioDomestic;
	@FXML RadioButton radioWorldwide;

	@FXML
	public void initialize()
	{
		radioDomestic.fire();
		genreBox.getItems().add(UtilQueryPair.ANY);
		genreBox.getItems().addAll(UtilQueries.getGenres());
		genreBox.setValue(UtilQueryPair.ANY);
	}

	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		// Genre selection
		String genre_section = "";
		if(!genreBox.getValue().getId().equals("*")) {
			genre_section = "genres.genre_id = \"" + genreBox.getValue().getId() + "\" and";
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
