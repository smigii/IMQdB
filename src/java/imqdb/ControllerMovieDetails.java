package imqdb;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControllerMovieDetails {

	private Connection connection;

	@FXML private ListView<MovieSearchResult> movieSearchList;
	@FXML private TextField movieSearchField;

	@FXML private VBox movieDetailsBox;
	private TableWrapper creditsTable;

	@FXML private VBox basicInfoLabels;
	@FXML private VBox basicInfoFields;

	public ControllerMovieDetails()
	{
		connection = SqliteConnection.getConnection();
	}

	@FXML public void initialize()
	{
		movieSearchList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends MovieSearchResult> ov, MovieSearchResult old_val, MovieSearchResult new_val) -> {
			onMovieSearchSelectionChanged(new_val);
		});

		creditsTable = new TableWrapper();
		movieDetailsBox.getChildren().add(creditsTable.getTable());
	}

	@FXML protected void onMovieSearchBtnClick()
	{
		String val = movieSearchField.getText();
		if(val.equals("")) {
			return;
		}

		try {
			PreparedStatement ps = connection.prepareStatement("select\n" +
				"\tmovies.*,\n" +
				"\tproduction_company,\n" +
				"\tgroup_concat(distinct country) as \"countries\",\n" +
				"\tgroup_concat(distinct genre) as \"genres\",\n" +
				"\tgroup_concat(distinct language) as \"languages\"\n" +
				"from movies\n" +
				"\n" +
				"inner join production_companies on production_companies.production_id = movies.production_id\n" +
				"inner join movie_country on movie_country.imdb_title_id = movies.imdb_title_id\n" +
				"inner join countries on movie_country.country_id = countries.country_id\n" +
				"inner join movie_genre on movie_genre.imdb_title_id = movies.imdb_title_id\n" +
				"inner join genres on movie_genre.genre_id = genres.genre_id\n" +
				"inner join movie_language on movie_language.imdb_title_id = movies.imdb_title_id\n" +
				"inner join languages on movie_language.language_id = languages.language_id\n" +
				"where\n" +
				"\tmovies.original_title like \"%" + val + "%\"\n" +
				"group by movies.imdb_title_id;");
			ResultSet rs = ps.executeQuery();
			movieSearchList.getItems().clear();
			while(rs.next()) {
				MovieSearchResult msr = new MovieSearchResult(rs);
				movieSearchList.getItems().add(msr);
			}
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void onMovieSearchSelectionChanged(MovieSearchResult msr)
	{
		if(msr == null) return;

		fillBasicInfo(msr);

		try {
			PreparedStatement ps = connection.prepareStatement("select name as Name, title as Title from title_principals tp\n" +
				"inner join artist on tp.imdb_name_id = artist.imdb_name_id\n" +
				"inner join titles on tp.title_id = titles.title_id\n" +
				"where tp.imdb_title_id = \"" + msr.id + "\"");
			ResultSet rs = ps.executeQuery();
			creditsTable.fillTable(rs);
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void fillBasicInfo(MovieSearchResult msr)
	{
		basicInfoLabels.getChildren().clear();
		basicInfoFields.getChildren().clear();

		List<Node> labels = new ArrayList<>();
		List<Node> fields = new ArrayList<>();

		if (msr.title != null) {
			labels.add(new Label("Title:"));
			fields.add(new Label(msr.title));
		}

		if (msr.year != null) {
			labels.add(new Label("Year:"));
			fields.add(new Label(msr.year));
		}

		if (msr.production != null) {
			labels.add(new Label("Production Co.:"));
			fields.add(new Label(msr.production));
		}

		if (msr.duration != null) {
			labels.add(new Label("Duration:"));
			fields.add(new Label(msr.duration + " mins"));
		}

		if (msr.genres != null) {
			labels.add(new Label("Genres:"));
			fields.add(new Label(msr.genres));
		}

		if (msr.languages != null) {
			labels.add(new Label("Available in:"));
			fields.add(new Label(msr.languages));
		}

		if (msr.countries != null) {
			labels.add(new Label("Produced in:"));
			fields.add(new Label(msr.countries));
		}

		if (msr.imdb_score != null) {
			String votes = (msr.num_votes != null) ? " (" + msr.num_votes + " votes)" : "";
			labels.add(new Label("IMdB Score:"));
			fields.add(new Label(msr.imdb_score + votes));
		}

		if (msr.metascore != null) {
			labels.add(new Label("Metascore:"));
			fields.add(new Label(msr.metascore + "/100"));
		}

		String currency = (msr.currency != null) ? msr.currency : "";

		if (msr.budget != null) {
			labels.add(new Label("Budget:"));
			fields.add(new Label("$" + msr.budget + " " + currency));
		}

		if (msr.domestic != null) {
			labels.add(new Label("Domestic Revenue:"));
			fields.add(new Label("$" + msr.domestic + " " + currency));
		}

		if (msr.domestic != null && msr.budget != null) {
			labels.add(new Label("Domestic Profit:"));
			fields.add(new Label("$" + (msr.domestic - msr.budget) + " " + currency));
		}

		if (msr.worldwide != null) {
			labels.add(new Label("Worldwide Revenue:"));
			fields.add(new Label("$" + msr.worldwide + " " + currency));
		}

		if (msr.worldwide != null && msr.budget != null) {
			labels.add(new Label("Worldwide Profit:"));
			fields.add(new Label("$" + (msr.worldwide - msr.budget) + " " + currency));
		}

		basicInfoLabels.getChildren().addAll(labels);
		basicInfoFields.getChildren().addAll(fields);
	}


	static class MovieSearchResult {

		public String id;
		public String title;
		public String year;
		public String duration;
		public String production;

		public String currency;
		public Long budget;
		public Long domestic;
		public Long worldwide;

		public Float imdb_score;
		public Integer metascore;
		public Long num_votes;

		public String genres;
		public String languages;
		public String countries;

		public MovieSearchResult(ResultSet rs)
		{
			try {
				this.id = rs.getString("imdb_title_id");
				this.title = rs.getString("original_title");
				this.year = rs.getString("year");
				this.duration = rs.getString("duration");
				this.production = rs.getString("production_company");
				this.currency = rs.getString("currency");
				this.budget = rs.getLong("budget_currency");
				this.domestic = rs.getLong("usa_gross_income");
				this.worldwide = rs.getLong("worldwide_gross_income");
				this.imdb_score = rs.getFloat("avg_vote");
				this.metascore = rs.getInt("metascore");
				this.num_votes = rs.getLong("votes");
				this.genres = rs.getString("genres");
				this.countries = rs.getString("countries");
				this.languages = rs.getString("languages");
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
