package imqdb.qc;

import imqdb.Services;
import imqdb.db.IDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QcAllTables implements IQueryController {

	private final IDatabase db;
	@FXML private ChoiceBox<String> tableBox;

	public QcAllTables()
	{
		db = Services.getDatabase();
	}

	@FXML
	public void initialize()
	{
		String sql = "SELECT name FROM sqlite_master WHERE type = \"table\"";
		db.runQuery(sql, this::fillTableBox);
	}

	public void fillTableBox(ResultSet rs)
	{
		try {
			while (rs.next()) {
				tableBox.getItems().add(rs.getString("name"));
			}
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String createQuery()
	{
		String selectedTable = tableBox.getValue();
		if(selectedTable == null) {
			return "select null";
		}
		String sql = "select * from " + selectedTable;
		return sql;
	}

}
