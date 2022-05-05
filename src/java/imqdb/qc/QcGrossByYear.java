package imqdb.qc;

import imqdb.Services;
import imqdb.db.IDatabase;
import imqdb.utils.UtilQueryPair;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;

public class QcGrossByYear implements IQueryController {

	private final IDatabase db;
	@FXML private ChoiceBox<UtilQueryPair> genreBox;
	@FXML private ChoiceBox<UtilQueryPair> countryBox;
	@FXML private Spinner<Integer> minYear;
	@FXML private Spinner<Integer> maxYear;
	@FXML private RadioButton radioDomestic;
	@FXML private RadioButton radioWorldwide;
	@FXML private RadioButton radioHigh;
	@FXML private RadioButton radioLow;
	@FXML private RadioButton radioGross;
	@FXML private RadioButton radioProfit;

	public QcGrossByYear()
	{
		db = Services.getDatabase();
	}

	@FXML
	public void initialize()
	{
		radioDomestic.fire();
		radioHigh.fire();
		radioGross.fire();

		genreBox.getItems().add(UtilQueryPair.ANY);
		genreBox.getItems().addAll(db.getGenres());
		genreBox.setValue(UtilQueryPair.ANY);

		countryBox.getItems().add(UtilQueryPair.ANY);
		countryBox.getItems().addAll(db.getCountries());
		countryBox.setValue(UtilQueryPair.ANY);
	}

	@Override
	public String createQuery()
	{
		// Genre selection
		String genreSection = "";
		if(!genreBox.getValue().isAny()) {
			genreSection = "and m.imdb_title_id in (select mg.imdb_title_id from movie_genre mg where mg.genre_id = " + genreBox.getValue().id() + ")\n";
		}

		// Country selection
		String countrySection = "";
		if(!countryBox.getValue().isAny()) {
			countrySection = "and m.imdb_title_id in (select mc.imdb_title_id from movie_country mc where mc.country_id = " + countryBox.getValue().id() + ")\n";
		}

		String subtraction = (radioGross.isSelected()) ? "" : " - m.budget_currency ";
		String grossProfit = (radioGross.isSelected()) ? "Gross" : "Profit";

		// Region selection
		String region;
		String regionAlias;
		if(radioDomestic.isSelected()) {
			region = "usa_gross_income";
			regionAlias = "\"Domestic " + grossProfit + "\"";
		} else {
			region = "worldwide_gross_income";
			regionAlias = "\"Worldwide " + grossProfit + "\"";
		}

		String minMax = "max";
		if(radioLow.isSelected()){
			minMax = "min";
		}

		String sql =
			"select\n" +
				"	m.year as Year,\n" +
				"	m.original_title as Title,\n" +
					minMax + "(m." + region  + subtraction + ") as " + regionAlias + "\n" +
				"from movies m\n" +
				"where\n" +
				"m.currency = \"USD\"\n" +
				"and m.year >= " + minYear.getValue() + "\n" +
				"and m.year <= " + maxYear.getValue() + "\n" +
				genreSection +
				countrySection +
				"group by\n" +
				"	m.year\n" +
				"order by\n" +
				"	m.year desc";

		return sql;
	}
}
