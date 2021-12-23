package imqdb;

import imqdb.utils.ErrorWindow;
import imqdb.utils.Logger;
import imqdb.utils.SqliteConnection;
import imqdb.utils.TableWrapper;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class ControllerMain {

	private final Connection db;

	// Main query section (left side)
	@FXML private ChoiceBox<Query> querySelector;
	@FXML private AnchorPane queryParamPane;
	@FXML private VBox mainVbox;
	@FXML private Shape statusCircle;
	private TableWrapper mainQueryTable;

	// Export
	@FXML private Label exportMsg;
	private String lastQuery;

	public ControllerMain()
	{
		db = SqliteConnection.getConnection();
	}

	@FXML public void initialize()
	{
		// Query selector
		querySelector.getItems().addAll(QueryFactory.generate());
		querySelector.setValue(QueryFactory.dummy);
		setQueryParamPane(QueryFactory.dummy.getNode());

		// Whenever selection changes, this method is called
		querySelector.setOnAction(this::onQuerySelectionChanged);

		// Tables
		mainQueryTable = new TableWrapper();
		mainQueryTable.getTable().setMaxHeight(999999999);
		mainQueryTable.getTable().setPrefHeight(999999999);
		mainVbox.getChildren().add(mainQueryTable.getTable());

		try {
			if(!db.isClosed()) {
				statusCircle.setFill(Color.GREEN);
			}
		} catch(SQLException e){
			System.out.println(e.toString());
		}

	}

	public void onQuerySelectionChanged(ActionEvent event)
	{
		System.out.println(event.toString());
		Query currentQuery = querySelector.getValue();
		Node n = currentQuery.getNode();
		setQueryParamPane(n);
		lastQuery = querySelector.getValue().getName();
	}

	@FXML protected void onRunBtnClick()
	{
		QueryController activeController = querySelector.getValue().getController();

		// Create the sql logging directory if it does not exist.
		try {
			Files.createDirectories(Path.of("sql"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(activeController == null)
			return;

		try {
			ResultSet rs = activeController.execute(db);

			if(rs == null) return;

			mainQueryTable.fillTable(rs);
		}
		catch (SQLException e) {
			Logger.logSqlError(e);
			ErrorWindow.CreateSqlErrorWindow();
		}

	}

	@FXML protected void onMovieSearchBtnClick()
	{
		System.out.println("Movie search");
	}

	private void setQueryParamPane(Node n)
	{
		queryParamPane.getChildren().clear();
		queryParamPane.getChildren().add(n);
	}

	@FXML protected void onExportCsvClick()
	{
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String query = lastQuery.replaceAll("\\s+", "_");
		String path = "csv/" + query + "-" + ts.getTime() + ".csv";
		try {
			Files.createDirectories(Path.of("csv"));
			mainQueryTable.toCSV(path);
			exportMsg.setText("Exported CSV to " + path);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			exportMsg.setText("Failed to export CSV :(");
		}

	}

}

