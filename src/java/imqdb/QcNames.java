package imqdb;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcNames implements QueryController {

	@FXML
	Spinner<Integer> limitSpinner;

	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		int limit = limitSpinner.getValue();
		PreparedStatement ps = db.prepareStatement("select name from artist order by imdb_name_id limit " + limit);
		ResultSet rs = ps.executeQuery();
		return rs;
	}

}
