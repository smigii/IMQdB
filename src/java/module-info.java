module com.imqdb {
	requires java.sql;
	requires org.xerial.sqlitejdbc;

	requires javafx.controls;
	requires javafx.fxml;

	exports imqdb;
	opens imqdb to javafx.fxml;
}