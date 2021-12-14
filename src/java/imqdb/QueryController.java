package imqdb;

import java.sql.Connection;
import java.sql.ResultSet;

public interface QueryController {

	public abstract ResultSet execute(Connection db);

}