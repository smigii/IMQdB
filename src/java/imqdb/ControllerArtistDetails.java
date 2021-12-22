package imqdb;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControllerArtistDetails {

	private Connection connection;

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
		connection = SqliteConnection.getConnection();
	}

	@FXML public void initialize()
	{
		artistSearchList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ArtistSearchResult> ov, ArtistSearchResult old_val, ArtistSearchResult new_val) -> {
			onArtistSearchSelectionChanged(new_val);
		});

		artistCreditTable = new TableWrapper();
		artistCreditBox.getChildren().add(artistCreditTable.getTable());
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
		try {
			artistMoneyBox.getChildren().clear();
			PreparedStatement ps = connection.prepareStatement(
				"select\n" +
					"\tsum(worldwide_gross_income) as \"world_gross\",\n" +
					"\tsum(usa_gross_income) as \"domestic_gross\"\n" +
					"from title_principals tp\n" +
					"inner join movies m on tp.imdb_title_id = m.imdb_title_id\n" +
					"where tp.imdb_name_id = \"" + asr.id + "\"");
			ResultSet rs = ps.executeQuery();
			artistMoneyBox.getChildren().add(new Label("Total domestic revenue generated: $" + rs.getString("domestic_gross")));
			artistMoneyBox.getChildren().add(new Label("Total worldwide revenue generated: $" + rs.getString("world_gross")));
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		try {
			PreparedStatement ps = connection.prepareStatement(
				"select original_title as Title, year as Year, titles.title as Job, movies.usa_gross_income as \"Gross Domestic Revenue\", movies.worldwide_gross_income as \"Gross Worldwide Revenur\", movies.budget_currency as Budget, movies.currency as Currency from title_principals\n" +
					"inner join movies on title_principals.imdb_title_id = movies.imdb_title_id\n" +
					"inner join titles on title_principals.title_id = titles.title_id\n" +
					"where title_principals.imdb_name_id = \"" + asr.id + "\"" +
					"order by year desc");
			ResultSet rs = ps.executeQuery();
			artistCreditTable.fillTable(rs);
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	protected void onArtistSearchBtnClick()
	{
		String val = artistSearchField.getText();
		if(val.equals("")) {
			return;
		}

		try {
			PreparedStatement ps = connection.prepareStatement("select artist.*, c1.country as birth_country, c2.country as death_country from artist\n" +
				"left join countries c1 on artist.country_of_birth_id = c1.country_id\n" +
				"left join countries c2 on artist.country_of_death_id = c2.country_id\n" +
				"where artist.name like \"%" + val + "%\"");
			ResultSet rs = ps.executeQuery();
			artistSearchList.getItems().clear();
			while(rs.next()) {
				ArtistSearchResult asr = new ArtistSearchResult(rs);
				artistSearchList.getItems().add(asr);
			}
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
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

		String death = "";

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

	static class ArtistSearchResult {

		public String id;
		public String name;
		public String birth_name;
		public String height;
		public String bio;

		public String place_of_birth;
		public String country_of_birth;
		public String date_of_birth;

		public String place_of_death;
		public String country_of_death;
		public String date_of_death;
		public String reason_of_death;

		public ArtistSearchResult(ResultSet rs)
		{
			try {
				this.id = rs.getString("imdb_name_id");
				this.name = rs.getString("name");
				this.birth_name = rs.getString("birth_name");
				this.height = rs.getString("height");
				this.bio = rs.getString("bio");
				this.place_of_birth = rs.getString("place_of_birth");
				this.place_of_death = rs.getString("place_of_death");
				this.country_of_birth = rs.getString("birth_country");
				this.country_of_death = rs.getString("death_country");
				this.date_of_birth = rs.getString("date_of_birth");
				this.date_of_death = rs.getString("date_of_death");
				this.reason_of_death = rs.getString("reason_of_death");
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		public String toString()
		{
			return name;
		}
	}

}