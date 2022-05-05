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

public class QcMovieRating implements IQueryController {

    private final IDatabase db;
    @FXML private ChoiceBox<UtilQueryPair> genreBox;
    @FXML private ChoiceBox<UtilQueryPair> languageBox;
    @FXML private Spinner<Integer> minYear;
    @FXML private Spinner<Integer> maxYear;
    @FXML private RadioButton radioHighest;
    @FXML private Spinner<Integer> minCount;

    public QcMovieRating()
    {
        db = Services.getDatabase();
    }

    @FXML void initialize()
    {
        radioHighest.fire();
        UtilQueryPair.fillChoiceBoxAddAny(genreBox, db.getGenres());
        UtilQueryPair.fillChoiceBoxAddAny(languageBox, db.getLanguages());
    }

    @Override
    public String createQuery()
    {
        UtilQueryPair selGenre = genreBox.getValue();
        UtilQueryPair selLang = languageBox.getValue();
        String genre_where = "\n\tand genre = \"" + selGenre.name() + "\"";
        String language_where = "\n\tand language = \"" + selLang.name() + "\"";
        String minValue = Integer.toString(minYear.getValue());
        String maxValue = Integer.toString(maxYear.getValue());
        String highLow = "";
        String count = Integer.toString(minCount.getValue());
        String count_where = "\n\tand votes >= " + count;

        if(selGenre.isAny()) {
            genre_where = "";
        }

        if(selLang.isAny()) {
            language_where = "";
        }

        if(radioHighest.isSelected()){
            highLow = "desc";
        }

        return "select distinct original_title as Title, avg_vote as Rating " +
                "from movies\n" +
                "natural join movie_genre " +
                "natural join genres " +
                "natural join movie_language " +
                "natural join languages " +
                "\n\twhere year >= " + minValue +
                "\n\tand year <= " + maxValue +
                genre_where +
                language_where +
                count_where +
                "\n\torder by avg_vote " + highLow +
                "\n\tlimit 10;";
    }
}
