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

import java.util.ArrayList;

public class QcArtistMoneyGenerated extends QueryController {

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
	public String createQuery()
	{

		String unionBase = "select a.imdb_name_id, a.imdb_name_id as fam_id from artist a\n";

		String unionParents =
			"select a.imdb_name_id, pa.parent_id as fam_id from artist a\n" +
			"inner join parents_artist pa on a.imdb_name_id = pa.imdb_name_id\n";

		String unionChildren =
			"select a.imdb_name_id, ca.child_imdb_id as fam_id from artist a\n" +
			"inner join children_artist ca on a.imdb_name_id = ca.imdb_name_id\n";

		String unionSpouses =
			"select a.imdb_name_id, sa.spouse_id as fam_id from artist a\n" +
			"inner join spouses_artist sa on a.imdb_name_id = sa.imdb_name_id\n";

		String unionRelatives =
			"select a.imdb_name_id, ra.relative_id as fam_id from artist a\n" +
			"inner join relatives_artist ra on a.imdb_name_id = ra.imdb_name_id\n";

		ArrayList<String> unionList = new ArrayList<>();

		unionList.add(unionBase);
		if(famBoxSpouses.isSelected())
			unionList.add(unionSpouses);
		if(famBoxChildren.isSelected())
			unionList.add(unionChildren);
		if(famBoxParents.isSelected())
			unionList.add(unionParents);
		if(famBoxRelatives.isSelected())
			unionList.add(unionRelatives);

		String unionSection = String.join("\n union \n", unionList);

		ArrayList<String> artistIds = new ArrayList<>();
		for(ArtistSearchResult asr : artistQueryList.getItems()) {
			artistIds.add("fam_movies.imdb_name_id = \"" + asr.id + "\" ");
		}

		String artistIdWhere = "";
		if(!artistIds.isEmpty()) {
			artistIdWhere += "where\n";
			artistIdWhere += String.join(" or\n", artistIds);
		}

		String famMoviesWhere = "";
		if(!titleBox.getValue().isAny()) {
			famMoviesWhere = "and tp.title_id = " + titleBox.getValue().getId() + "\n";
		}

		String sql =
			"select\n" +
			"	fam_movies.name as \"Artist\",\n" +
			"	sum(fam_movies.worldwide_gross_income) as \"Worldwide Revenue Generated\",\n" +
			"	sum(fam_movies.usa_gross_income) as \"Domestic Revenue Generated\",\n" +
			"	fam_names.family_names as \"Family Members Queried\",\n" +
			"	group_concat(fam_movies.original_title,'; ') as \"Movies Included in Query\"\n" +
			"from (\n" +
			"	-- Get all movies involving artist or artist family member\n" +
			"	select distinct\n" +
			"		a.imdb_name_id,\n" +
			"		a.name,\n" +
			"		m.*\n" +
			"	from (\n" +
			unionSection +
			"	) as x\n" +
			"	inner join title_principals tp on\n" +
			"		tp.imdb_name_id = x.fam_id\n" +
			"	inner join movies m on\n" +
			"		tp.imdb_title_id = m.imdb_title_id\n" +
			"	inner join artist a on\n" +
			"		x.imdb_name_id = a.imdb_name_id\n" +
			"	where\n" +
			"		m.currency = \"USD\"\n" +
			"		and m.year >= " + minYear.getValue() + "\n" +
			"		and m.year <= " + maxYear.getValue() + "\n" +
			famMoviesWhere +
			"\n" +
			") as fam_movies\n" +
			"inner join (\n" +
			"	-- Get the list of family members\n" +
			"	select \n" +
			"		fam_ids.imdb_name_id,\n" +
			"		group_concat(fam_names.name,'; ') as \"family_names\"\n" +
			"	from (\n" +
			unionSection +
			"	) as fam_ids\n" +
			"	inner join artist fam_names on fam_ids.fam_id = fam_names.imdb_name_id\n" +
			"	group by fam_ids.imdb_name_id\n" +
			") fam_names\n" +
			"on fam_movies.imdb_name_id = fam_names.imdb_name_id\n" +
			artistIdWhere +
			"group by\n" +
			"	fam_movies.imdb_name_id\n" +
			"order by \"Worldwide Revenue Generated\" desc";

		return sql;
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
