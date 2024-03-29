module imqdb {
	requires java.sql;
	requires org.xerial.sqlitejdbc;

	requires javafx.controls;
	requires javafx.fxml;

	exports imqdb;
	opens imqdb to javafx.fxml;
	exports imqdb.qc;
	opens imqdb.qc to javafx.fxml;
	exports imqdb.utils;
	opens imqdb.utils to javafx.fxml;
	exports imqdb.db;
	opens imqdb.db to javafx.fxml;
}