package imqdb.qc;

import imqdb.QueryController;
import imqdb.utils.ArtistSearchResult;
import imqdb.utils.UtilQueries;
import imqdb.utils.UtilQueryPair;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcArtistMoneyGenerated implements QueryController {

	@FXML private TextField artistSearchField;
	@FXML private ListView<ArtistSearchResult> artistSearchList;
	@FXML private ListView<ArtistSearchResult> artistQueryList;
	@FXML private ChoiceBox<UtilQueryPair> titleBox;
	@FXML private Spinner<Integer> minYear;
	@FXML private Spinner<Integer> maxYear;
	@FXML private CheckBox famBoxSpouses;
	@FXML private CheckBox famBoxChildren;
	@FXML private CheckBox famBoxParents;
	@FXML private CheckBox famBoxRelatives;

	@FXML public void initialize()
	{
		titleBox.getItems().add(UtilQueryPair.ANY);
		titleBox.getItems().addAll(UtilQueries.getTitles());
		titleBox.setValue(UtilQueryPair.ANY);

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


	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		PreparedStatement ps = db.prepareStatement(
			"select\n" +
			"	fam_movies.name as \"Artist\",\n" +
			"	group_concat(fam_movies.original_title,'; ') as \"Movies Included in Query\",\n" +
			"	sum(fam_movies.worldwide_gross_income) as \"Worldwide Revenue Generated\",\n" +
			"	sum(fam_movies.usa_gross_income) as \"Domestic Revenue Generated\",\n" +
			"	fam_names.family_names as \"Family Members Included\"\n" +
			"from (\n" +
			"	-- Get all movies involving artist or artist family member\n" +
			"	select distinct\n" +
			"		a.imdb_name_id,\n" +
			"		a.name,\n" +
			"		m.*\n" +
			"	from (\n" +
			"\n" +
			"		select a.imdb_name_id, a.imdb_name_id as fam_id from artist a\n" +
			"		\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, sa.spouse_id as fam_id from artist a\n" +
			"		inner join spouses_artist sa on a.imdb_name_id = sa.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, ca.child_imdb_id as fam_id from artist a\n" +
			"		inner join children_artist ca on a.imdb_name_id = ca.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, pa.parent_id as fam_id from artist a\n" +
			"		inner join parents_artist pa on a.imdb_name_id = pa.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, ra.relative_id as fam_id from artist a\n" +
			"		inner join relatives_artist ra on a.imdb_name_id = ra.imdb_name_id\n" +
			"\n" +
			"	) as x\n" +
			"	inner join title_principals tp on\n" +
			"		tp.imdb_name_id = x.fam_id\n" +
			"	inner join movies m on\n" +
			"		tp.imdb_title_id = m.imdb_title_id\n" +
			"	inner join artist a on\n" +
			"		x.imdb_name_id = a.imdb_name_id\n" +
			"	where\n" +
			"		m.currency = \"USD\"\n" +
			"\n" +
			") as fam_movies\n" +
			"inner join (\n" +
			"	-- Get the list of family members\n" +
			"	select \n" +
			"		fam_ids.imdb_name_id,\n" +
			"		group_concat(fam_names.name,'; ') as \"family_names\"\n" +
			"	from (\n" +
			"		select a.imdb_name_id, a.imdb_name_id as fam_id from artist a\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, sa.spouse_id as fam_id from artist a\n" +
			"		inner join spouses_artist sa on a.imdb_name_id = sa.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, ca.child_imdb_id as fam_id from artist a\n" +
			"		inner join children_artist ca on a.imdb_name_id = ca.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, pa.parent_id as fam_id from artist a\n" +
			"		inner join parents_artist pa on a.imdb_name_id = pa.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, ra.relative_id as fam_id from artist a\n" +
			"		inner join relatives_artist ra on a.imdb_name_id = ra.imdb_name_id\n" +
			"\n" +
			"	) as fam_ids\n" +
			"	inner join artist fam_names on fam_ids.fam_id = fam_names.imdb_name_id\n" +
			"	group by fam_ids.imdb_name_id\n" +
			") fam_names\n" +
			"on fam_movies.imdb_name_id = fam_names.imdb_name_id\n" +
			"-- where\n" +
			"	-- fam_movies.imdb_name_id = \"nm0905154\"\n" +
			"group by\n" +
			"	fam_movies.imdb_name_id\n" +
			"order by fam_movies.imdb_title_id");
		return ps.executeQuery();
	}

	@FXML protected void onClearBtnClick()
	{
		artistQueryList.getItems().clear();
	}

	@FXML protected void onRemoveBtnClick()
	{
		ArtistSearchResult selectedArtist = artistQueryList.getSelectionModel().getSelectedItem();
		if(selectedArtist == null) {
			return;
		}
		artistQueryList.getItems().remove(selectedArtist);
		System.out.println("hi");
	}

	@FXML protected void onAddBtnClick()
	{
		ArtistSearchResult selectedArtist = artistSearchList.getSelectionModel().getSelectedItem();
		if(selectedArtist == null) {
			return;
		}
		artistSearchList.getItems().remove(selectedArtist);
		artistQueryList.getItems().add(selectedArtist);
	}

	@FXML protected void artistSearchTrigger()
	{
		String artist = artistSearchField.getText();
		artistSearchList.getItems().clear();
		artistSearchList.getItems().addAll(UtilQueries.artistLookup(artist + "%"));
	}

}
