package imqdb;

import imqdb.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class QueryController {

	public ResultSet execute(Connection db) throws SQLException
	{
		String sql = createQuery();
		Logger.logQueryAttempt(sql);
		PreparedStatement ps = db.prepareStatement(sql);
		return ps.executeQuery();
	}

	public abstract String createQuery();

}