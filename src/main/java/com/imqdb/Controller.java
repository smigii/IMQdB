package com.imqdb;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;

public class Controller {
	@FXML
	private TabPane paramTabPane;

	private int t = 1;

	@FXML
	protected void onHelloButtonClick()
	{
		//welcomeText.setText("Welcome to JavaFX Application!");
	}

	@FXML
	protected void onRunBtnClick()
	{
		System.out.println("TEST");
		SingleSelectionModel<Tab> selectionModel = paramTabPane.getSelectionModel();
		selectionModel.select(t);
		t = (t+1)%2;
	}
}