module com.imqdb {
	requires java.sql;
	requires org.xerial.sqlitejdbc;

	requires javafx.controls;
	requires javafx.fxml;

	opens com.imqdb to javafx.fxml;
	exports com.imqdb;
}