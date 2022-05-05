package imqdb;

import imqdb.db.IDatabase;
import imqdb.utils.ErrorWindow;
import imqdb.utils.TableWrapper;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class ControllerMain {

//	private final Connection db;
	private final IDatabase db;

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
		db = Services.getDatabase();
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

//		try {
//			if(!db.isClosed()) {
//				statusCircle.setFill(Color.GREEN);
//			}
//		} catch(SQLException e){
//			System.out.println(e.toString());
//		}

	}

	public void onQuerySelectionChanged(ActionEvent event)
	{
		Query currentQuery = querySelector.getValue();
		Node n = currentQuery.getNode();
		setQueryParamPane(n);
		lastQuery = querySelector.getValue().getName();
	}

	@FXML protected void onRunBtnClick()
	{
		QueryController activeController = querySelector.getValue().getController();

		if(activeController == null)
			return;

		String sql = activeController.createQuery();
		db.runQuery(sql, this::fillTable);
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

	public void fillTable(ResultSet rs)
	{
		if(rs == null) return;
		try {
			mainQueryTable.fillTable(rs);
		}
		catch (SQLException e) {
			ErrorWindow.CreateSqlErrorWindow();
			System.out.println(e.getMessage());
		}
	}

}

