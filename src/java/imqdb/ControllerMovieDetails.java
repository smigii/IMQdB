package imqdb;

import imqdb.db.IDatabase;
import imqdb.db.SqliteConnection;
import imqdb.utils.TableWrapper;
import imqdb.utils.MovieSearchResult;
import imqdb.utils.UtilQueries;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControllerMovieDetails {

//	private Connection connection;
	private final IDatabase db;

	@FXML private ListView<MovieSearchResult> movieSearchList;
	@FXML private TextField movieSearchField;

	@FXML private VBox movieDetailsBox;
	private TableWrapper creditsTable;

	@FXML private VBox basicInfoLabels;
	@FXML private VBox basicInfoFields;

	public ControllerMovieDetails()
	{
//		connection = SqliteConnection.getConnection();
		db = Services.getDatabase();
	}

	@FXML public void initialize()
	{
		movieSearchList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends MovieSearchResult> ov, MovieSearchResult old_val, MovieSearchResult new_val) -> {
			onMovieSearchSelectionChanged(new_val);
		});

		creditsTable = new TableWrapper();
		movieDetailsBox.getChildren().add(creditsTable.getTable());

		movieSearchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent)
			{
				if(keyEvent.getCode().equals(KeyCode.ENTER)) {
					movieSearchTrigger();
				}
			}
		});
	}

	public void onMovieSearchSelectionChanged(MovieSearchResult msr)
	{
		if(msr == null) return;

		fillBasicInfo(msr);

		String sql = "select\n" +
			"name as Name, title as Title from title_principals tp\n" +
			"inner join artist on tp.imdb_name_id = artist.imdb_name_id\n" +
			"inner join titles on tp.title_id = titles.title_id\n" +
			"where tp.imdb_title_id = \"" + msr.id + "\"";

		db.runQuery(sql, this::fillCreditsTable);
	}

	public void fillCreditsTable(ResultSet rs)
	{
		try {
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

	@FXML protected void movieSearchTrigger()
	{
		String val = movieSearchField.getText();
		if(val.equals("")) {
			return;
		}
		db.movieLookup(val+"%", this::fillMovieSearchList);
	}

	public void fillMovieSearchList(ArrayList<MovieSearchResult> movies)
	{
		movieSearchList.getItems().clear();
		movieSearchList.getItems().addAll(movies);
	}

}
