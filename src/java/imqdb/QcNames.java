package imqdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcNames implements QueryController {

	@Override
	public ResultSet execute(Connection db)
	{
		try {
			PreparedStatement ps = db.prepareStatement("select name from actor limit 10");
			ResultSet rs = ps.executeQuery();
			return rs;
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

}
