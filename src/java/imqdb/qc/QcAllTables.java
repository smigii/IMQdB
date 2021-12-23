package imqdb.qc;

import imqdb.QueryController;
import imqdb.utils.Logger;
import imqdb.utils.SqliteConnection;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcAllTables extends QueryController {

	@FXML ChoiceBox<String> tableBox;

	@FXML
	public void initialize()
	{
		try {
			Connection connection = SqliteConnection.getConnection();
			String sql = "SELECT name FROM sqlite_master WHERE type = \"table\"";
			Logger.logQueryAttempt(sql);
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
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
			return null;
		}
		String sql = "select *a from " + selectedTable;
		return sql;
	}

}
