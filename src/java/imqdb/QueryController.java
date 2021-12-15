package imqdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface QueryController {

	public abstract ResultSet execute(Connection db) throws SQLException;

}