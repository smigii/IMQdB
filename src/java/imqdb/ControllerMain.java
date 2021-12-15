package imqdb;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerMain {
	@FXML private ChoiceBox<Query> querySelector;
	@FXML private AnchorPane queryParamPane;
	@FXML private TableView<List<String>> resultsTable;

	private final Connection db;

	public ControllerMain()
	{
		db = SqliteConnection.connect();
	}

	/*
	Like a constructor, but called after everything is setup, put any component setup in here.
	According to https://www.youtube.com/watch?v=9XJicRt_FaI 2:07:24, this should maybe be different but
	fuck it this is cleaner.
	*/
	@FXML
	public void initialize()
	{
		// Query selector
		querySelector.getItems().addAll(QueryFactory.generate());
		querySelector.setValue(QueryFactory.dummy);
		setQueryParamPane(QueryFactory.dummy.getNode());

		// Whenever selection changes, this method is called
		querySelector.setOnAction(this::onQuerySelectionChanged);

		// Table
		resultsTable.getColumns().clear();

	}

	public void onQuerySelectionChanged(ActionEvent event)
	{
		Query currentQuery = querySelector.getValue();
		Node n = currentQuery.getNode();
		setQueryParamPane(n);
	}

	@FXML
	protected void onRunBtnClick()
	{
		QueryController activeController = querySelector.getValue().getController();
		if(activeController == null)
			return;

		try {
			resultsTable.getColumns().clear();
			ResultSet rs = activeController.execute(db);
			// TODO: FIX
			if(rs == null) return;

			ResultSetMetaData rsmd = rs.getMetaData();
			int nCols = rsmd.getColumnCount();

			List<List<String>> data = new ArrayList<>();
			List<String> columns = new ArrayList<>();

			for(int i = 1; i <= nCols; i++){
				columns.add(rsmd.getColumnLabel(i));
			}

			while(rs.next()){
				List<String> row = new ArrayList<>();
				for(int i = 1; i <= nCols; i++){
					row.add(rs.getString(i));
				}
				data.add(row);
			}

			DataResult result = new DataResult(columns, data);

			for(int i = 0; i < result.getNumColumns(); i++){
				TableColumn<List<String>, String> column = new TableColumn<>(result.getColumnName(i));
				int colIdx = i;
				column.setCellValueFactory(cellData ->
					new SimpleObjectProperty<>(cellData.getValue().get(colIdx)));
				resultsTable.getColumns().add(column);
			}

			resultsTable.getItems().setAll(result.getData());

		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	private void setQueryParamPane(Node n)
	{
		queryParamPane.getChildren().clear();
		queryParamPane.getChildren().add(n);
	}

}