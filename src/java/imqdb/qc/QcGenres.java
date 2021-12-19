package imqdb.qc;

import imqdb.QueryController;

import java.sql.*;

public class QcGenres implements QueryController {


	@Override
	public ResultSet execute(Connection db)
	{
		try {
			PreparedStatement ps = db.prepareStatement("select genre_id as Id, genre as Genre from genres");
			ResultSet rs = ps.executeQuery();
			return rs;
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
