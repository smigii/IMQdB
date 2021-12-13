package com.imqdb;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class ControllerMain {
	@FXML
	private ChoiceBox<Query> querySelector;
	@FXML
	private AnchorPane queryParamPane;

	/*
	Like a constructor, but called after everything is setup, put any component setup in here.
	According to https://www.youtube.com/watch?v=9XJicRt_FaI 2:07:24, this should maybe be different but
	fuck it this is cleaner.
	*/
	@FXML
	public void initialize()
	{
		// Query selector
//		querySelector.setValue("Select a query!");
		querySelector.getItems().addAll(QueryFactory.generate());

		// Whenever selection changes, this method is called
		querySelector.setOnAction(this::onQuerySelectionChanged);
	}

	public void onQuerySelectionChanged(ActionEvent event)
	{
		Node n = querySelector.getSelectionModel().getSelectedItem().getFxml();
		queryParamPane.getChildren().clear();
		queryParamPane.getChildren().add(n);
	}

	@FXML
	protected void onRunBtnClick()
	{
		System.out.println("TEST");
	}

}