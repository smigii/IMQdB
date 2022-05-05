package imqdb.qc;

import imqdb.Services;
import imqdb.db.IDatabase;
import imqdb.db.SqliteConnection;
import imqdb.utils.UtilQueryPair;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcMovieDuration implements IQueryController {

    private final IDatabase db;
    @FXML private ChoiceBox<UtilQueryPair> genreBox;
    @FXML private ChoiceBox<UtilQueryPair> languageBox;
    @FXML private ChoiceBox<UtilQueryPair> countryBox;
    @FXML private Spinner<Integer> minYear;
    @FXML private Spinner<Integer> maxYear;
    @FXML private RadioButton radioLongest;
    @FXML private Spinner<Integer> minDuration;
    @FXML private Spinner<Integer> minCount;

    public QcMovieDuration()
    {
        db = Services.getDatabase();
    }

    @FXML void initialize()
    {
        radioLongest.fire();
        UtilQueryPair.fillChoiceBoxAddAny(genreBox, db.getGenres());
        UtilQueryPair.fillChoiceBoxAddAny(languageBox, db.getLanguages());
        UtilQueryPair.fillChoiceBoxAddAny(countryBox, db.getCountries());
    }

    @Override
    public String createQuery()
    {
        UtilQueryPair selGenre = genreBox.getValue();
        UtilQueryPair selLang = languageBox.getValue();
        UtilQueryPair selCountry = countryBox.getValue();

        String genre_where = "movies.imdb_title_id in (select imdb_title_id from movie_genre natural join genres where genres.genre = \"" + selGenre.name() + "\")";
        String language_where = "movies.imdb_title_id in (select imdb_title_id from movie_language natural join languages where languages.language = \"" + selLang.name() + "\")";
        String country_where = "movies.imdb_title_id in (select imdb_title_id from movie_country natural join countries where countries.country = \"" + selCountry.name() + "\")";

        String clg_where = "";
        int clauses = 0;

        if(!selGenre.isAny()) {
            clg_where += genre_where + "\n";
            clauses++;
        }

        if(!selLang.isAny()) {
            if(clauses > 0)
                clg_where += "and ";
            clg_where += language_where + "\n";
            clauses++;
        }

        if(!selCountry.isAny()) {
            if(clauses > 0)
                clg_where += "and ";
            clg_where += country_where + "\n";
            clauses++;
        }

        if(clauses > 0)
            clg_where += " and ";

        String highLow = "";
        if (radioLongest.isSelected()){
            highLow = "desc";
        }


        String sql = "select movies.original_title as Title, movies.duration as \"Duration (minutes)\", " +
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
                "and duration >= " + minDuration.getValue() + "\n" +
                "and votes >= " + minCount.getValue() + "\n" +
                "group by movies.imdb_title_id\n" +
                "order by duration " + highLow + ";";

        return sql;
    }
}
