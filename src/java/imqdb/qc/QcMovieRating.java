package imqdb.qc;

import imqdb.QueryController;
import imqdb.utils.Logger;
import imqdb.utils.SqliteConnection;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcMovieRating extends QueryController {

    @FXML private ChoiceBox<String> genreBox;
    @FXML private ChoiceBox<String> languageBox;
    @FXML private Spinner<Integer> minYear;
    @FXML private Spinner<Integer> maxYear;
    @FXML private RadioButton radioHighest;
    @FXML private Spinner<Integer> minCount;

    @FXML void initialize()
    {
        radioHighest.fire();
        try {
            Connection connection = SqliteConnection.getConnection();
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
        String genre_where = "\n\tand genre = \"" + genre_selected + "\"";
        String language_where = "\n\tand language = \"" + language_selected + "\"";
        String minValue = Integer.toString(minYear.getValue());
        String maxValue = Integer.toString(maxYear.getValue());
        String highLow = "";
        String count = Integer.toString(minCount.getValue());
        String count_where = "\n\tand votes >= " + count;

        if (genre_selected.equals("Any")) {
            genre_where = "";
        }

        if (language_selected.equals("Any")) {
            language_where = "";
        }

        if (radioHighest.isSelected()){
            highLow = "desc";
        }

        String sql = "select distinct original_title as Title, avg_vote as Rating " +
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

        return sql;
    }
}
