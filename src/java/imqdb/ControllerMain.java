package imqdb;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class ControllerMain {

	private final Connection db;

	// Main query section (left side)
	@FXML private ChoiceBox<Query> querySelector;
	@FXML private AnchorPane queryParamPane;
	@FXML private VBox mainVbox;
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
		mainVbox.getChildren().add(mainQueryTable.getTable());
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

		try {
			ResultSet rs = activeController.execute(db);
			// TODO: FIX
			if(rs == null) return;

			mainQueryTable.fillTable(rs);
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
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

