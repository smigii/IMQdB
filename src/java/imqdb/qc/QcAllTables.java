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

public class QcAllTables implements QueryController {

	@FXML ChoiceBox<String> tableBox;

	@FXML
	public void initialize()
	{
		try {
			Connection connection = SqliteConnection.getConnection();
			String sql = "SELECT name FROM sqlite_master WHERE type = \"table\"";
			Logger.log(sql);
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
	public ResultSet execute(Connection db) throws SQLException
	{
		String selectedTable = tableBox.getValue();
		if(selectedTable == null) {
			return null;
		}
		PreparedStatement ps = db.prepareStatement("select * from " + selectedTable);
		return ps.executeQuery();
	}

}
