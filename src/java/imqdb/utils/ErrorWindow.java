package imqdb.utils;

import imqdb.App;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ErrorWindow {

	public static void CreateSqlErrorWindow()
	{
		VBox root = new VBox();

		root.setPadding(new Insets(20,20,20,20));

		Label l1 = new Label("An SQL exception has occurred.");
		Label l2 = new Label("Please check " + Logger.getErrPathPrefix() + Logger.getSessionTimestamp() + ".sql for details.");
		l1.setStyle("-fx-font: 20 system;");
		l2.setStyle("-fx-font: 20 system;");

		root.getChildren().add(l1);
		root.getChildren().add(l2);
		Scene scene = new Scene(root);
		scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
		Stage stage = new Stage();
		stage.setTitle("SQL Error Dialog");
		stage.setScene(scene);
		stage.show();
	}

}
