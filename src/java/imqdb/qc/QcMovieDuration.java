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

public class QcMovieDuration implements QueryController {

    @FXML private ChoiceBox<String> genreBox;
    @FXML private ChoiceBox<String> languageBox;
    @FXML private ChoiceBox<String> countryBox;
    @FXML private Spinner<Integer> minYear;
    @FXML private Spinner<Integer> maxYear;
    @FXML private RadioButton radioLongest;
    @FXML private Spinner<Integer> minDuration;
    @FXML private Spinner<Integer> minCount;

    @FXML void initialize()
    {
        radioLongest.fire();
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
    public ResultSet execute(Connection db) throws SQLException
    {
        String genre_selected = genreBox.getValue();
        String language_selected = languageBox.getValue();
        String country_selected = countryBox.getValue();
        String genre_where = "\n\twhere genre = \"" + genre_selected + "\"";
        String language_where = "\n\twhere language = \"" + language_selected + "\"";
        String country_where = "\n\twhere country = \"" + country_selected + "\"";
        String minValue = Integer.toString(minYear.getValue());
        String maxValue = Integer.toString(maxYear.getValue());
        String highLow = "";
        String duration = Integer.toString(minDuration.getValue());
        String duration_where = "\n\tand duration >= " + duration;
        String count = Integer.toString(minCount.getValue());
        String count_where = "\n\tand votes >= " + count;

        if (genre_selected.equals("Any")) {
            genre_where = "";
        }

        if (language_selected.equals("Any")) {
            language_where = "";
        }

        if (country_selected.equals("Any")){
            count_where = "";
        }

        if (radioLongest.isSelected()){
            highLow = "desc";
        }

        PreparedStatement ps = db.prepareStatement(
            "select movies.original_title as Title, movies.duration as Duration, movies.year as Year,\n" +
                    "group_concat(distinct country) as Countries, group_concat(distinct genre) as Genres, " +
                    "group_concat(distinct language) as Languages\n" +
                    "from movies\n" +
                    "\n" +
                    "inner join movie_country on movie_country.imdb_title_id = movies.imdb_title_id\n" +
                    "inner join countries on movie_country.country_id = countries.country_id\n" +
                    "inner join movie_genre on movie_genre.imdb_title_id = movies.imdb_title_id\n" +
                    "inner join genres on movie_genre.genre_id = genres.genre_id\n" +
                    "inner join movie_language on movie_language.imdb_title_id = movies.imdb_title_id\n" +
                    "inner join languages on movie_language.language_id = languages.language_id\n" +
                    "\n\twhere year >= " + minValue +
                    "\n\tand year <= " + maxValue +
                    "\n\tand " +
                    "    movies.imdb_title_id in (\n" +
                    "        select imdb_title_id from movie_country natural join countries\n" +
                    country_where +
                    "    )\n" +
                    "    and\n" +
                    "    movies.imdb_title_id in (\n" +
                    "        select imdb_title_id from movie_genre natural join genres\n" +
                    genre_where +
                    "    )\n" +
                    "    and\n" +
                    "    movies.imdb_title_id in (\n" +
                    "        select imdb_title_id from movie_language natural join languages\n" +
                    language_where +
                    "    )\n" +
                    duration_where +
                    count_where +
                    "\n\tgroup by movies.imdb_title_id" +
                "\n\torder by duration " + highLow +
                "\n\tlimit 10;"
        );


//        PreparedStatement ps = db.prepareStatement(
//                "select distinct a.original_title, a.duration, a.year, b.countries from " +
//                "(select distinct original_title, duration, " +
//                        "year " + //GROUP_CONCAT(country, ',') as Countries " +
//                        "from movies" +
//                        "\n\tnatural join movie_genre" +
//                        "\n\tnatural join genres" +
//                        "\n\tnatural join movie_language" +
//                        "\n\tnatural join languages" +
//                        "\n\twhere year >= " + minValue +
//                        "\n\tand year <= " + maxValue +
//                        genre_where +
//                        language_where +
//                        duration_where +
//                        count_where +
//                        "\n\tgroup by original_title " +
//                        "\n\torder by duration " + highLow +
//                        "\n\tlimit 10) a" +
//
//                        "\ninner join " +
//
//                        "(select movies.original_title, group_concat(country) as countries from movie_country" +
//                        "\ninner join movies on movie_country.imdb_title_id = movies.imdb_title_id" +
//                        "\ninner join countries on movie_country.country_id = countries.country_id" +
//                        "\ngroup by movies.imdb_title_id) b" +
//                        "\n on a.original_title = b.original_title"
//                );





//        PreparedStatement ps = db.prepareStatement(
//                "select distinct original_title as Title, duration as \"Length (min)\", " +
//                "year as Year " + //GROUP_CONCAT(country, ',') as Countries " +
//                "from movies" +
////                "from movie_country" +
////                "\n\tinner join movies on movie_country.imdb_title_id = movies.imdb_title_id" +
////                "\n\tinner join countries on movie_country.country_id = countries.country_id" +
//                "\n\tnatural join movie_genre" +
//                "\n\tnatural join genres" +
//                "\n\tnatural join movie_language" +
//                "\n\tnatural join languages" +
//                "\n\twhere year >= " + minValue +
//                "\n\tand year <= " + maxValue +
//                genre_where +
//                language_where +
//                duration_where +
//                count_where +
//                "\n\tgroup by original_title " +
//                "\n\torder by duration " + highLow +
//                "\n\tlimit 10;");
//
////        PreparedStatement ps = db.prepareStatement(
////        "select movies.original_title, group_concat(country) from movie_country" +
////        "\ninner join movies on movie_country.imdb_title_id = movies.imdb_title_id" +
////        "\ninner join countries on movie_country.country_id = countries.country_id" +
////        "\ngroup by movies.imdb_title_id"
////        );

        return ps.executeQuery();
    }
}
