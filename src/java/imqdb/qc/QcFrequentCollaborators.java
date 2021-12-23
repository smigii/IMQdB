package imqdb.qc;

import imqdb.QueryController;
import imqdb.utils.ArtistSearchResult;
import imqdb.utils.Logger;
import imqdb.utils.UtilQueries;
import imqdb.utils.UtilQueryPair;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QcFrequentCollaborators extends QueryController {

	@FXML private TextField artistSearchField;
	@FXML private ListView<ArtistSearchResult> artistSearchList;
	@FXML private ListView<ArtistSearchResult> artistQueryList;
	@FXML private ChoiceBox<UtilQueryPair> titleBoxArtist;
	@FXML private ChoiceBox<UtilQueryPair> titleBoxCollab;
	@FXML private Spinner<Integer> minCollab;

	@FXML public void initialize()
	{
		titleBoxArtist.getItems().add(UtilQueryPair.ANY);
		titleBoxCollab.getItems().add(UtilQueryPair.ANY);
		titleBoxArtist.getItems().addAll(UtilQueries.getTitles());
		titleBoxCollab.getItems().addAll(UtilQueries.getTitles());
		titleBoxArtist.setValue(UtilQueryPair.ANY);
		titleBoxCollab.setValue(UtilQueryPair.ANY);

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

	@Override
	public String createQuery()
	{

		// Artist Id Where
		ArrayList<String> artistIdList = new ArrayList<>();
		for(ArtistSearchResult asr : artistQueryList.getItems()) {
			artistIdList.add("tp1.imdb_name_id = \"" + asr.id + "\"\n");
		}
		String artistIdWhere = "";
		if(!artistIdList.isEmpty())
			artistIdWhere = " (" + String.join("or ", artistIdList) + ")\n";

		// Artist Title Where
		String artistTitleWhere = "";
		if(!titleBoxArtist.getValue().isAny())
			artistTitleWhere = "tp1.title_id = " + titleBoxArtist.getValue().getId() + "\n";

		String collabTitleWhere = "";
		if(!titleBoxCollab.getValue().isAny())
			collabTitleWhere = "tp2.title_id = " + titleBoxCollab.getValue().getId() + "\n";

		// Full Where clause
		ArrayList<String> fullWhereList = new ArrayList<>();
		fullWhereList.add("tp1.imdb_name_id != tp2.imdb_name_id\n");
		if(!artistIdWhere.isEmpty())
			fullWhereList.add(artistIdWhere);
		if(!artistTitleWhere.isEmpty())
			fullWhereList.add(artistTitleWhere);
		if(!collabTitleWhere.isEmpty())
			fullWhereList.add(collabTitleWhere);

		String fullWhere = String.join(" and\n", fullWhereList);

		String sql =
				"select\n" +
						"	a1.name as \"Artist\",\n" +
						"	a2.name as \"Collaborator\",\n" +
						"	count(distinct tp2.imdb_title_id) as \"Count\",\n" +
						"	group_concat(m.original_title, '; ') as \"Movies\"\n" +
						"from title_principals tp1\n" +
						"inner join title_principals tp2 on\n" +
						"	tp1.imdb_title_id = tp2.imdb_title_id\n" +
						"inner join artist a1 on\n" +
						"	tp1.imdb_name_id = a1.imdb_name_id\n" +
						"inner join artist a2 on\n" +
						"	tp2.imdb_name_id = a2.imdb_name_id\n" +
						"inner join movies m on\n" +
						"	tp2.imdb_title_id = m.imdb_title_id\n" +
						"where\n" +
						fullWhere +
						"group by\n" +
						"	tp1.imdb_name_id, tp2.imdb_name_id\n" +
						"having\n" +
						"	\"Count\" >= " + minCollab.getValue() + "\n" +
						"order by \"Count\" desc";

		return sql;
	}
}
