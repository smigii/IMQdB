package imqdb.qc;

import imqdb.QueryController;
import imqdb.SqliteConnection;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcMovieRating implements QueryController {

    @FXML private ChoiceBox<String> genreBox;
    @FXML private ChoiceBox<String> languageBox;
    @FXML private Spinner<Integer> minYear;
    @FXML private Spinner<Integer> maxYear;
    @FXML private RadioButton radioHighest;

    @FXML void initialize()
    {
        try {
            Connection connection = SqliteConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("select genre from genres");
            ResultSet rs = ps.executeQuery();
            genreBox.getItems().add("Any");
            while (rs.next()) {
                genreBox.getItems().add(rs.getString("genre"));
            }
            genreBox.setValue("Any");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ResultSet execute(Connection db) throws SQLException
    {
        String genre_selected = genreBox.getValue();
        String language_selected = languageBox.getValue();
        String genre_where = " and genre = \"" + genre_selected + "\"";
        String language_where = " and language = \"" + language_selected + "\"";
        String minValue = Integer.toString(minYear.getValue());
        String maxValue = Integer.toString(maxYear.getValue());
        String highLow = "";

        if (genre_selected.equals("Any")) {
            genre_where = "";
        }

        if (language_selected.equals("Any")) {
            language_where = "";
        }

        if (radioHighest.isSelected()){
            highLow = "desc";
        }

        PreparedStatement ps = db.prepareStatement("select original_title as Title, avg_vote as Rating " +
                        "from movies\n" +
                        "\twhere substr(date_published, 0, 4) >= " + minValue +
                        "\n\tand substr(date_published, 0, 4) <= " + maxValue +
                        genre_where +
                        language_where +
                        "\n\torder by avg_vote" + highLow +
                        "\n\tlimit 10;");

        return ps.executeQuery();
    }
}
