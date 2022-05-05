package imqdb.db;

import java.sql.ResultSet;
import java.util.function.Consumer;

public interface IDatabase {

	void runQuery(String sql, Consumer<ResultSet> callback);

}
