package imqdb.qc;

import imqdb.db.SqliteConnection;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcMatchmaker implements IQueryController {

    @FXML private ChoiceBox<String> titleBox;
    @FXML private Spinner<Integer> afterYear;

    @FXML void initialize()
    {
        try {
            Connection connection = SqliteConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("select title from titles");
            ResultSet rs = ps.executeQuery();
            titleBox.getItems().add("Any");
            while (rs.next()) {
                titleBox.getItems().add(rs.getString("title"));
            }
            titleBox.setValue("Actor");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String createQuery()
    {
        String title_selected = titleBox.getValue();
        String year_selected = Integer.toString(afterYear.getValue());
        String title_where = "\n\tand t1.title = \"" + title_selected + "\"";
        String year_where = "\n\tand x.s_year >= \"" + year_selected + "\"";

        if (title_selected.equals("Any")){
            title_where = "";
        }

        String sql = "select\n" +
                "    m.original_title as \"Movie\",\n" +
                "    m.year as \"Year\",\n" +
                "    a1.name as \"Artist\",\n" +
                "    t1.title as \"Artist Title\",\n" +
                "    a2.name as \"Spouse Name\",\n" +
                "    t2.title as \"Spouse Title\",\n" +
                "    x.s_year as \"Year Married\"\n" +
                "from (\n" +
                "\n" +
                "    select\n" +
                "        tp.imdb_title_id, tp.title_id, sa.*, tp2.title_id as spouse_role_id\n" +
                "    from spouses_artist sa\n" +
                "\n" +
                "    inner join title_principals tp on tp.imdb_name_id = sa.imdb_name_id\n" +
                "    inner join title_principals tp2 on tp2.imdb_name_id = sa.spouse_id\n" +
                "\n" +
                "    where\n" +
                "        tp.imdb_title_id = tp2.imdb_title_id\n" +
                "\n" +
                ") as x\n" +
                "\n" +
                "inner join artist a1 on x.imdb_name_id = a1.imdb_name_id\n" +
                "inner join artist a2 on x.spouse_id = a2.imdb_name_id\n" +
                "inner join titles t1 on x.title_id = t1.title_id\n" +
                "inner join titles t2 on x.spouse_role_id = t2.title_id\n" +
                "inner join movies m  on x.imdb_title_id = m.imdb_title_id\n" +
                "\n" +
                "where\n" +
                "    m.year <= x.s_year\n" +
                title_where +
                year_where +
                "\n" +
                "order by \"Artist\"";

        return sql;
    }
}
