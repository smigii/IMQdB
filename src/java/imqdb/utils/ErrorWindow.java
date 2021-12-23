package imqdb.utils;

import imqdb.App;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ErrorWindow {

	public static void CreateSqlErrorWindow()
	{
		VBox root = new VBox();
		root.getChildren().add(new Label("An SQL exception has occurred."));
		root.getChildren().add(new Label("Please check " + Logger.getSessionTimestamp() + " for details."));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
		Stage stage = new Stage();
		stage.setTitle("SQL Error Dialog");
		stage.setScene(scene);
		stage.show();
	}

}
