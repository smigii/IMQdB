package imqdb;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.*;

public class ControllerMain {
	@FXML
	private ChoiceBox<Query> querySelector;
	@FXML
	private AnchorPane queryParamPane;

	private Connection db;

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
		setQueryParamPane(QueryFactory.dummy.getFxml());

		// Whenever selection changes, this method is called
		querySelector.setOnAction(this::onQuerySelectionChanged);

	}

	public void onQuerySelectionChanged(ActionEvent event)
	{
		Node n = querySelector.getValue().getFxml();
		setQueryParamPane(n);
	}

	@FXML
	protected void onRunBtnClick()
	{
		System.out.println("TEST");
		try {
			PreparedStatement ps = db.prepareStatement("select * from genres");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				System.out.println(rs.getString("genre") + rs.getString("genre_id"));
			}
			System.out.println("TEST");
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	private void setQueryParamPane(Node n)
	{
		queryParamPane.getChildren().clear();
		queryParamPane.getChildren().add(n);
	}

}