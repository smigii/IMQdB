package imqdb;

import imqdb.db.SqliteConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends javafx.application.Application {
	@Override
	public void start(Stage stage) throws IOException
	{
		SqliteConnection.connect();
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main.fxml"));
		Scene scene = new Scene(fxmlLoader.load());
		scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());  // Apply stylesheet
		stage.setTitle("IMQDB");
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
	}

	public static void main(String[] args)
	{
		launch();
	}

}