package com.imqdb;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import java.io.IOException;

public class Query {

	private String name;
	private Node fxml;

	public Query(String name, String fxmlPath)
	{
		this.name = name;
		try {
			this.fxml = FXMLLoader.load(getClass().getResource(fxmlPath));
		}
		catch(IOException e) {
			// TODO: Handle this less shittely
			System.out.println(e.toString());
		}
	}

	public String getName()
	{
		return name;
	}

	public Node getFxml()
	{
		return fxml;
	}

	public String toString()
	{
		return name;
	}

}
