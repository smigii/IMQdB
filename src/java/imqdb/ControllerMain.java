package imqdb;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.sql.*;
import java.util.ArrayList;

public class ControllerMain {
	@FXML private ChoiceBox<Query> querySelector;
	@FXML private AnchorPane queryParamPane;
	@FXML private TableView<String[]> resultsTable;

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
		QueryBus.setController(currentQuery.getController());
	}

	@FXML
	protected void onRunBtnClick()
	{
		// TODO: make this work
		if(!QueryBus.hasController())
			return;

		try {
			resultsTable.getColumns().clear();
			ResultSet rs = QueryBus.getController().execute(db);

			if (rs != null) {
				ResultSetMetaData rsmd = rs.getMetaData();

				int nCols = rsmd.getColumnCount();
//				while(rs.next()) {
//					String[] arr = new String[nCols];
//					for(int i = 1; i <= nCols; i++){
//						arr[i] = rs.getString(i);
//					}
//					resultsTable.getItems().add(arr);
//				}

				for(int i = 1; i <= nCols; i++){
					String colName = rsmd.getColumnLabel(i);
					TableColumn<String[], String> tc = new TableColumn<>(colName);
					int finalI = i;
//					tc.setCellValueFactory(x -> x[finalI]);
					resultsTable.getColumns().add(tc);
//					tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
//						@Override
//						public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
//							String[] x = p.getValue();
//							if (x != null && x.length>0) {
//								return new SimpleStringProperty(x[0]);
//							} else {
//								return new SimpleStringProperty("<no name>");
//							}
//						}
//					});
				}



				System.out.println("DONE");
			}
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