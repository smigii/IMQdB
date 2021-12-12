module com.imqdb {
	requires javafx.controls;
	requires javafx.fxml;


	opens com.imqdb to javafx.fxml;
	exports com.imqdb;
}