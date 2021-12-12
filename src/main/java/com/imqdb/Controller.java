package com.imqdb;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;

public class Controller {
	@FXML
	private TabPane paramTabPane;
	@FXML
	private ChoiceBox<String> querySelector;

	private SingleSelectionModel<Tab> queryPaneSelectionModel;

	/*
	Like a constructor, but called after everything is setup, put any component setup in here.
	According to https://www.youtube.com/watch?v=9XJicRt_FaI 2:07:24, this should maybe be different but
	fuck it this is cleaner.
	*/
	@FXML
	public void initialize()
	{
		// Tab Pane stuff
		// Use this for setting active query pane
		queryPaneSelectionModel = paramTabPane.getSelectionModel();

		// Query selector
		querySelector.setValue("Select a query!");
		querySelector.getItems().add("Query 1");
		querySelector.getItems().add("Query 2");

		// Whenever selection changes, this method is called
		querySelector.setOnAction(this::onQuerySelectionChanged);
	}

	public void onQuerySelectionChanged(ActionEvent event)
	{
		int queryIdx = querySelector.getSelectionModel().getSelectedIndex();
		queryPaneSelectionModel.select(queryIdx);
	}

	@FXML
	protected void onRunBtnClick()
	{
		System.out.println("TEST");
	}
}