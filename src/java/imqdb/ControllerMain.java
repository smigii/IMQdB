package imqdb;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.sql.*;

public class ControllerMain {
	@FXML private ChoiceBox<Query> querySelector;
	@FXML private AnchorPane queryParamPane;
	@FXML private VBox mainVbox;
	private TableWrapper tableWrapper;

	private final Connection db;

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

		// Table
		tableWrapper = new TableWrapper();
		mainVbox.getChildren().add(tableWrapper.getTable());

	}

	public void onQuerySelectionChanged(ActionEvent event)
	{
		Query currentQuery = querySelector.getValue();
		Node n = currentQuery.getNode();
		setQueryParamPane(n);
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

			tableWrapper.fillTable(rs);
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