package imqdb.utils;

import javafx.scene.control.ChoiceBox;
import java.util.ArrayList;


public record UtilQueryPair(String name, String id) {

	public static UtilQueryPair ANY = new UtilQueryPair("Any", "*");

	public String toString()
	{
		return name;
	}

	public boolean isAny()
	{
		return id.equals("*");
	}

	public static void fillChoiceBoxAddAny(ChoiceBox<UtilQueryPair> choiceBox, ArrayList<UtilQueryPair> pairs)
	{
		choiceBox.getItems().add(UtilQueryPair.ANY);
		choiceBox.getItems().addAll(pairs);
		choiceBox.setValue(UtilQueryPair.ANY);
	}

}