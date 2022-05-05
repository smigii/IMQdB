package imqdb.qc;

import imqdb.Services;
import imqdb.db.IDatabase;
import imqdb.utils.UtilQueryPair;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;

public class QcMatchmaker implements IQueryController {

    private final IDatabase db;
    @FXML private ChoiceBox<UtilQueryPair> titleBox;
    @FXML private Spinner<Integer> afterYear;

    public QcMatchmaker()
    {
        db = Services.getDatabase();
    }

    @FXML void initialize()
    {
        titleBox.getItems().add(UtilQueryPair.ANY);
        titleBox.getItems().addAll(db.getTitles());
        titleBox.setValue(UtilQueryPair.ANY);
    }

    @Override
    public String createQuery()
    {
        UtilQueryPair selectedTitle = titleBox.getValue();
        String title_where = selectedTitle.isAny() ? "" : "\n\tand t1.title = \"" + selectedTitle.name() + "\"";
        String year_selected = Integer.toString(afterYear.getValue());
        String year_where = "\n\tand x.s_year >= \"" + year_selected + "\"";

        return "select\n" +
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
    }
}
