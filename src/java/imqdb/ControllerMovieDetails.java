package imqdb;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControllerMovieDetails {

	private Connection connection;

	// Movie summary
	@FXML private ListView<MovieSearchResult> movieSearchResult;
	@FXML private TextField movieSearchField;

	public ControllerMovieDetails()
	{
		connection = SqliteConnection.getConnection();
	}

	@FXML public void initialize()
	{

	}

	@FXML protected void onMovieSearchBtnClick()
	{

	}


	static class MovieSearchResult {

		public String title;
		public Integer year;
		public Integer duration;
		public String production;

		public String currency;
		public Integer budget;
		public Integer domestic;
		public Integer worldwide;

		public MovieSearchResult(ResultSet rs)
		{
			try {
				this.title = rs.getString("original_title");
				this.year = rs.getInt("year");
				this.duration = rs.getInt("duration");
				this.production = rs.getString("production_company");
				this.currency = rs.getString("currency");
				this.budget = rs.getInt("budget_currency");
				this.domestic = rs.getInt("usa_gross_income");
				this.worldwide = rs.getInt("worldwide_gross_income");
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		public String toString()
		{
			return (year == null) ? title : title + " (" + year + ")";
		}

	}

}
