package imqdb;

import imqdb.utils.ErrorWindow;
import imqdb.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class QueryController {

	public ResultSet execute(Connection db)
	{
		String sql = createQuery();
		return execute(db, sql);
	}
	public ResultSet execute(Connection db, String sql)
	{
		Logger.logQueryAttempt(sql);
		try {
			PreparedStatement ps = db.prepareStatement(sql);
			return ps.executeQuery();
		}
		catch(SQLException e) {
			Logger.logSqlError(e, sql);
			ErrorWindow.CreateSqlErrorWindow();
			return null;
		}
	}

	public abstract String createQuery();

}