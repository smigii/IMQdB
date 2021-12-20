package imqdb.qc;

import imqdb.QueryController;
import imqdb.SqliteConnection;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcShortestMovie implements QueryController {

	@FXML private ChoiceBox<String> choiceBox;

	@FXML void initialize()
	{
		try {
			Connection connection = SqliteConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement("select genre from genres");
			ResultSet rs = ps.executeQuery();
			choiceBox.getItems().add("Any");
			while (rs.next()) {
				choiceBox.getItems().add(rs.getString("genre"));
			}
			choiceBox.setValue("Any");
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		String selected = choiceBox.getValue();
		String genre_where = "where genre = \"" + selected + "\"";
		if(selected.equals("Any"))
			genre_where = "";

		PreparedStatement ps = db.prepareStatement("select distinct original_title as Title, duration as \"Duration (m)\" from movies\n" +
			"natural join movie_genre\n" +
			"natural join genres\n" +
			genre_where + "\n" +
			"\torder by duration desc\n" +
			"\tlimit 10;");

		return ps.executeQuery();
	}

}
