package imqdb.qc;

import imqdb.QueryController;
import imqdb.utils.Logger;
import imqdb.utils.UtilQueries;
import imqdb.utils.UtilQueryPair;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;

import java.sql.*;

public class QcGrossByYear implements QueryController {

	@FXML ChoiceBox<UtilQueryPair> genreBox;
	@FXML ChoiceBox<UtilQueryPair> countryBox;
	@FXML Spinner<Integer> minYear;
	@FXML Spinner<Integer> maxYear;
	@FXML RadioButton radioDomestic;
	@FXML RadioButton radioWorldwide;
	@FXML RadioButton radioGross;
	@FXML RadioButton radioNone;
	@FXML RadioButton radioHigh;
	@FXML RadioButton radioLow;

	@FXML
	public void initialize()
	{
		radioDomestic.fire();
		radioGross.fire();
		radioNone.fire();

		genreBox.getItems().add(UtilQueryPair.ANY);
		genreBox.getItems().addAll(UtilQueries.getGenres());
		genreBox.setValue(UtilQueryPair.ANY);

		countryBox.getItems().add(UtilQueryPair.ANY);
		countryBox.getItems().addAll(UtilQueries.getCountries());
		countryBox.setValue(UtilQueryPair.ANY);
	}

	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		// Genre selection
		String genre_section = "";
		if(!genreBox.getValue().isAny()) {
			genre_section = "genres.genre_id = \"" + genreBox.getValue().getId() + "\" and ";
		}

		// Country selection
		String count_section = "";
		if(!countryBox.getValue().isAny()) {
			count_section = "countries.country_id = \"" + countryBox.getValue().getId() + "\" and";
		}

		// Region selection
		String region;
		String regionAlias;
		if(radioDomestic.isSelected()) {
			region = "usa_gross_income";
			regionAlias = "Domestic_Income";
		} else {
			region = "worldwide_gross_income";
			regionAlias = "Worldwide_Income";
		}

		String subtractionAmount = "";
		if(!radioGross.isSelected()){
			subtractionAmount = "- movies.budget_currency";
			regionAlias = "Revenue";
		}

		String orderBy = "order by movies.year DESC";
		if(radioLow.isSelected()){
			orderBy = "order by " + regionAlias + " ASC";
		} else if(radioHigh.isSelected()) {
			orderBy = "order by " + regionAlias + " DESC";

		}

		String sql =
				"select movies.original_title as \"Title\", movies.year as \"Year\", movies." + region + subtractionAmount + " as \"" + regionAlias + "\" from movies\n" +
						"natural join movie_genre natural join genres natural join countries\n" +
						"where " + genre_section + count_section + "movies.year >= " + minYear.getValue() + " and movies.year <= " + maxYear.getValue() + "\n" +
						"group by movies.year\n" +
						"having max(movies." + region + ")\n" +
						orderBy;
		Logger.log(sql);
		PreparedStatement ps = db.prepareStatement(sql);

		return ps.executeQuery();
	}
}
