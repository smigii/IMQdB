package imqdb.qc;

import imqdb.Services;
import imqdb.db.IDatabase;
import imqdb.db.SqliteConnection;
import imqdb.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QcMostLeastWatched implements IQueryController {

    private final IDatabase db;
    @FXML private ChoiceBox<UtilQueryPair> genreBox;
    @FXML private ChoiceBox<UtilQueryPair> languageBox;
    @FXML private ChoiceBox<UtilQueryPair> countryBox;
    @FXML private Spinner<Integer> minYear;
    @FXML private Spinner<Integer> maxYear;
    @FXML private RadioButton radioMostWatched;
    @FXML private TextField artistSearchField;
    @FXML private ListView<ArtistSearchResult> artistSearchList;

    public QcMostLeastWatched()
    {
        db = Services.getDatabase();
    }

    @FXML void initialize()
    {
        radioMostWatched.fire();

        genreBox.getItems().add(UtilQueryPair.ANY);
        genreBox.getItems().addAll(db.getGenres());
        genreBox.setValue(UtilQueryPair.ANY);
        countryBox.getItems().add(UtilQueryPair.ANY);
        countryBox.getItems().addAll(db.getCountries());
        countryBox.setValue(UtilQueryPair.ANY);
        languageBox.getItems().add(UtilQueryPair.ANY);
        languageBox.getItems().addAll(db.getLanguages());
        languageBox.setValue(UtilQueryPair.ANY);
    }

    @Override
    public String createQuery()
    {
        UtilQueryPair selGenre = genreBox.getValue();
        UtilQueryPair selLang = languageBox.getValue();
        UtilQueryPair selCountry = countryBox.getValue();
        ArtistSearchResult selArtist = artistSearchList.getSelectionModel().getSelectedItem();
        String genreWhere = "movies.imdb_title_id in (select imdb_title_id from movie_genre natural join genres where genres.genre = \"" + selGenre.name() + "\")";
        String langWhere = "movies.imdb_title_id in (select imdb_title_id from movie_language natural join languages where languages.language = \"" + selLang.name() + "\")";
        String countryWhere = "movies.imdb_title_id in (select imdb_title_id from movie_country natural join countries where countries.country = \"" + selCountry.name() + "\")";
        String artistWhere = (selArtist == null) ? "" : "movies.imdb_title_id in (select imdb_title_id from title_principals where title_principals.imdb_name_id = \"" + selArtist.id + "\")";

        String clg_where = "";
        int clauses = 0;
        if(!selGenre.isAny()) {
            clg_where += genreWhere + "\n";
            clauses++;
        }

        if(!selLang.isAny()) {
            if(clauses > 0)
                clg_where += "and ";
            clg_where += langWhere + "\n";
            clauses++;
        }

        if(!selCountry.isAny()) {
            if(clauses > 0)
                clg_where += "and ";
            clg_where += countryWhere + "\n";
            clauses++;
        }

        if (selArtist != null){
            if (clauses > 0)
                clg_where += "and ";
            clg_where += artistWhere + "\n";
            clauses++;
        }

        if(clauses > 0)
            clg_where += " and ";

        String highLow = "";
        if (radioMostWatched.isSelected()){
            highLow = "desc";
        }

        return "select movies.original_title as Title, (movies.reviews_from_users + movies.reviews_from_critics) as \"Watches (i.e. Ratings)\", " +
                "movies.year as Year, group_concat(distinct country) as Countries, \n" +
                "group_concat(distinct genre) as Genres, \n" +
                "group_concat(distinct language) as Languages \n" +
                "from movies\n" +
                "inner join movie_country on movie_country.imdb_title_id = movies.imdb_title_id\n" +
                "inner join countries on movie_country.country_id = countries.country_id\n" +
                "inner join movie_genre on movie_genre.imdb_title_id = movies.imdb_title_id\n" +
                "inner join genres on movie_genre.genre_id = genres.genre_id\n" +
                "inner join movie_language on movie_language.imdb_title_id = movies.imdb_title_id\n" +
                "inner join languages on movie_language.language_id = languages.language_id\n" +
                "where\n" +
                clg_where + "\n" +
                "year >= " + minYear.getValue() + "\n" +
                "and year <= " + maxYear.getValue() + "\n" +
                "group by movies.imdb_title_id\n" +
                "order by (movies.reviews_from_users + movies.reviews_from_critics) " + highLow + ";";
    }

    @FXML protected void onArtistSearchBtnClick()
    {
        String artist = artistSearchField.getText().strip();
        if(artist.isEmpty())
            return;
        db.artistLookup(artist+"%", this::fillArtistSearchTable);
    }

    public void fillArtistSearchTable(ArrayList<ArtistSearchResult> artists)
    {
        artistSearchList.getItems().clear();
        artistSearchList.getItems().addAll(artists);
    }

    public void onArtistClearBtnClick() {
        artistSearchField.clear();
        artistSearchList.getItems().clear();
    }
}
