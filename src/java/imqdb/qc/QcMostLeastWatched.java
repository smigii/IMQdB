package imqdb.qc;

import imqdb.QueryController;
import imqdb.db.SqliteConnection;
import imqdb.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QcMostLeastWatched extends QueryController {

    private Connection connection;
    private ArrayList<ArtistSearchResult> artists;
    @FXML private ChoiceBox<String> genreBox;
    @FXML private ChoiceBox<String> languageBox;
    @FXML private ChoiceBox<String> countryBox;
    @FXML private Spinner<Integer> minYear;
    @FXML private Spinner<Integer> maxYear;
    @FXML private RadioButton radioMostWatched;
    @FXML private TextField artistSearchField;
    @FXML private ListView<ArtistSearchResult> artistSearchList;


    @FXML void initialize()
    {
        radioMostWatched.fire();
        artists = new ArrayList<>();
        try {
            connection = SqliteConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("select genre from genres");

            ResultSet rs = ps.executeQuery();
            genreBox.getItems().add("Any");
            while (rs.next()) {
                genreBox.getItems().add(rs.getString("genre"));
            }
            genreBox.setValue("Any");


            ps = connection.prepareStatement("select language from languages");
            rs = ps.executeQuery();
            languageBox.getItems().add("Any");
            while (rs.next()) {
                languageBox.getItems().add(rs.getString("language"));
            }
            languageBox.setValue("English");

            ps = connection.prepareStatement("select country from countries");
            rs = ps.executeQuery();
            countryBox.getItems().add("Any");
            while (rs.next()) {
                countryBox.getItems().add(rs.getString("country"));
            }
            countryBox.setValue("USA");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String createQuery()
    {

        String genre_selected = genreBox.getValue();
        String language_selected = languageBox.getValue();
        String country_selected = countryBox.getValue();
        ArtistSearchResult artist_selected = artistSearchList.getSelectionModel().getSelectedItem();
        String genre_where = "movies.imdb_title_id in (select imdb_title_id from movie_genre natural join genres where genres.genre = \"" + genre_selected + "\")";
        String language_where = "movies.imdb_title_id in (select imdb_title_id from movie_language natural join languages where languages.language = \"" + language_selected + "\")";
        String country_where = "movies.imdb_title_id in (select imdb_title_id from movie_country natural join countries where countries.country = \"" + country_selected + "\")";
        String artist_where = (artist_selected == null) ? "" : "movies.imdb_title_id in (select imdb_title_id from title_principals where title_principals.imdb_name_id = \"" + artist_selected.id + "\")";

        String clg_where = "";
        int clauses = 0;
        if(!genre_selected.equals("Any")) {
            clg_where += genre_where + "\n";
            clauses++;
        }

        if(!language_selected.equals("Any")) {
            if(clauses > 0)
                clg_where += "and ";
            clg_where += language_where + "\n";
            clauses++;
        }

        if(!country_selected.equals("Any")) {
            if(clauses > 0)
                clg_where += "and ";
            clg_where += country_where + "\n";
            clauses++;
        }

        if (artist_selected != null){
            if (clauses > 0)
                clg_where += "and ";
            clg_where += artist_where + "\n";
        }

        if(clauses > 0)
            clg_where += " and ";

        String highLow = "";
        if (radioMostWatched.isSelected()){
            highLow = "desc";
        }

        String sql = "select movies.original_title as Title, (movies.reviews_from_users + movies.reviews_from_critics) as \"Watches (i.e. Ratings)\", " +
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

        return sql;

    }

    @FXML
    protected void onArtistSearchBtnClick()
    {
        // copied from ControllerArtistDetails
        String val = artistSearchField.getText();
        if(val.equals("")) {
            return;
        }

        artistSearchList.getItems().clear();
        artists = UtilQueries.artistLookup("%" + val + "%");
        for (ArtistSearchResult asr : artists){
            artistSearchList.getItems().add(asr);
        }
    }

    public void onArtistClearBtnClick() {
        artistSearchField.clear();
        artistSearchList.getItems().clear();
    }
}
