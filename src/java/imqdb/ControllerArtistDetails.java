package imqdb;

import imqdb.db.IDatabase;
import imqdb.utils.ArtistSearchResult;
import imqdb.utils.TableWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControllerArtistDetails {

//	private Connection connection;
	private final IDatabase db;

	@FXML private ListView<ArtistSearchResult> artistSearchList;
	@FXML private TextField artistSearchField;
	@FXML private VBox basicInfoLabels;
	@FXML private VBox basicInfoFields;
	@FXML private TextArea artistBio;
	@FXML private VBox artistMoneyBox;
	@FXML private VBox artistCreditBox;
	private TableWrapper artistCreditTable;

	public ControllerArtistDetails()
	{
		db = Services.getDatabase();
	}

	@FXML public void initialize()
	{
		artistSearchList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ArtistSearchResult> ov, ArtistSearchResult old_val, ArtistSearchResult new_val) -> {
			onArtistSearchSelectionChanged(new_val);
		});

		artistCreditTable = new TableWrapper();
		artistCreditBox.getChildren().add(artistCreditTable.getTable());

		artistSearchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent)
			{
				if(keyEvent.getCode().equals(KeyCode.ENTER)) {
					artistSearchTrigger();
				}
			}
		});
	}

	public void onArtistSearchSelectionChanged(ArtistSearchResult asr)
	{
		if(asr == null) return;

		// Basic Info
		fillArtistBasicInfo(asr);

		// Bio
		artistBio.clear();
		if(asr.bio != null)
			artistBio.setText(asr.bio);

		// Movie Credits
		IDatabase db = Services.getDatabase();

		String sqlMoney = "select\n" +
			"\tsum(worldwide_gross_income) as \"world_gross\",\n" +
			"\tsum(usa_gross_income) as \"domestic_gross\"\n" +
			"from title_principals tp\n" +
			"inner join movies m on tp.imdb_title_id = m.imdb_title_id\n" +
			"where tp.imdb_name_id = \"" + asr.id + "\"";
		db.runQuery(sqlMoney, this::fillArtistMoney);

		String sqlCredits = "select\n" +
			"original_title as Title, year as Year, titles.title as Job, movies.usa_gross_income as \"Gross Domestic Revenue\", movies.worldwide_gross_income as \"Gross Worldwide Revenur\", movies.budget_currency as Budget, movies.currency as Currency from title_principals\n" +
			"inner join movies on title_principals.imdb_title_id = movies.imdb_title_id\n" +
			"inner join titles on title_principals.title_id = titles.title_id\n" +
			"where title_principals.imdb_name_id = \"" + asr.id + "\"" +
			"order by year desc";
		db.runQuery(sqlCredits, this::fillArtistCredits);

	}

	private void fillArtistBasicInfo(ArtistSearchResult asr)
	{
		basicInfoLabels.getChildren().clear();
		basicInfoFields.getChildren().clear();

		List<Node> labels = new ArrayList<>();
		List<Node> fields = new ArrayList<>();

		if(asr.name != null) {
			labels.add(new Label("Name:"));
			fields.add(new Label(asr.name));
		}

		if(asr.birth_name != null) {
			labels.add(new Label("Birth Name:"));
			fields.add(new Label(asr.birth_name));
		}

		if(asr.height != null) {
			labels.add(new Label("Height:"));
			fields.add(new Label(asr.height + "cm"));
		}

		if(asr.date_of_birth != null) {
			labels.add(new Label("Date of Birth:"));
			fields.add(new Label(asr.date_of_birth));
		}

		if(asr.place_of_birth != null) {
			labels.add(new Label("Place of Birth:"));
			fields.add(new Label(asr.place_of_birth));
		}

		if(asr.country_of_birth != null) {
			labels.add(new Label("Country of Birth:"));
			fields.add(new Label(asr.country_of_birth));
		}

		if(asr.date_of_death != null) {
			labels.add(new Label("Date of Death:"));
			fields.add(new Label(asr.date_of_death));
		}

		if(asr.place_of_death != null) {
			labels.add(new Label("Place of Death:"));
			fields.add(new Label(asr.place_of_death));
		}

		if(asr.country_of_death != null) {
			labels.add(new Label("Country of Death:"));
			fields.add(new Label(asr.country_of_death));
		}

		if(asr.reason_of_death != null) {
			labels.add(new Label("Cause of Death:"));
			fields.add(new Label(asr.reason_of_death));
		}

		basicInfoLabels.getChildren().addAll(labels);
		basicInfoFields.getChildren().addAll(fields);
	}

	private void fillArtistMoney(ResultSet rs)
	{
		try {
			artistMoneyBox.getChildren().clear();
			artistMoneyBox.getChildren().add(new Label("Total domestic revenue generated: $" + rs.getString("domestic_gross")));
			artistMoneyBox.getChildren().add(new Label("Total worldwide revenue generated: $" + rs.getString("world_gross")));
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void fillArtistCredits(ResultSet rs)
	{
		try {
			artistCreditTable.fillTable(rs);
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML protected void artistSearchTrigger()
	{
		String val = artistSearchField.getText();
		if(val.equals("")) {
			return;
		}
		db.artistLookup(val+"%", this::fillArtistLookup);
	}

	public void fillArtistLookup(ArrayList<ArtistSearchResult> artists)
	{
		artistSearchList.getItems().clear();
		artistSearchList.getItems().addAll(artists);
	}

}
