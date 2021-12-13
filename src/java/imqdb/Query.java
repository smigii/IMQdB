package imqdb;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import java.io.IOException;

public class Query {

	private String name;
	private QueryController controller;
	private Node node;

	public Query(String name, String fxmlPath)
	{
		this.name = name;
		try {
			FXMLLoader l = new FXMLLoader(getClass().getResource(fxmlPath));
			this.node = l.load();
			this.controller = l.<QueryController>getController();
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

	public Node getNode()
	{
		return node;
	}

	public QueryController getController()
	{
		return controller;
	}

	public String toString()
	{
		return name;
	}

}
