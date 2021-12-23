package imqdb.qc;

import imqdb.utils.UtilQueries;
import imqdb.QueryController;
import imqdb.utils.UtilQueryPair;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QcFamilyFun implements QueryController {

	@FXML private ChoiceBox<UtilQueryPair> artistRoleBox;
	@FXML private ChoiceBox<UtilQueryPair> familyRoleBox;
	@FXML private CheckBox famBoxChildren;
	@FXML private CheckBox famBoxParents;
	@FXML private CheckBox famBoxSpouses;
	@FXML private CheckBox famBoxRelatives;
	@FXML private Spinner<Integer> minYear;
	@FXML private Spinner<Integer> maxYear;
	@FXML private Spinner<Integer> minBudget;

	@FXML public void initialize()
	{
		artistRoleBox.getItems().add(UtilQueryPair.ANY);
		familyRoleBox.getItems().add(UtilQueryPair.ANY);
		artistRoleBox.getItems().addAll(UtilQueries.getTitles());
		familyRoleBox.getItems().addAll(UtilQueries.getTitles());
		artistRoleBox.setValue(UtilQueryPair.ANY);
		familyRoleBox.setValue(UtilQueryPair.ANY);
	}

	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		String childrenSection = "select\n" +
			"\t\tca.imdb_name_id, tp.title_id, ca.child_imdb_id as family_id, tp2.title_id as family_role_id, tp.imdb_title_id, \"Child\" as \"Relation\"\n" +
			"\tfrom children_artist ca\n" +
			"\n" +
			"\tinner join title_principals tp on tp.imdb_name_id = ca.imdb_name_id\n" +
			"\tinner join title_principals tp2 on tp2.imdb_name_id = ca.child_imdb_id\n" +
			"\n" +
			"\twhere\n" +
			"\t\ttp.imdb_title_id = tp2.imdb_title_id";
		String parentSection = "select\n" +
			"\t\tpa.imdb_name_id, tp.title_id, pa.parent_id as family_id, tp2.title_id as family_role_id, tp.imdb_title_id, \"Parent\" as \"Relation\"\n" +
			"\tfrom parents_artist pa\n" +
			"\n" +
			"\tinner join title_principals tp on tp.imdb_name_id = pa.imdb_name_id\n" +
			"\tinner join title_principals tp2 on tp2.imdb_name_id = pa.parent_id\n" +
			"\n" +
			"\twhere\n" +
			"\t\ttp.imdb_title_id = tp2.imdb_title_id";
		String spouseSection = "select\n" +
			"\t\tsa.imdb_name_id, tp.title_id, sa.spouse_id as family_id, tp2.title_id as family_role_id, tp.imdb_title_id, \"Spouse\" as \"Relation\"\n" +
			"\tfrom spouses_artist sa\n" +
			"\n" +
			"\tinner join title_principals tp on tp.imdb_name_id = sa.imdb_name_id\n" +
			"\tinner join title_principals tp2 on tp2.imdb_name_id = sa.spouse_id\n" +
			"\n" +
			"\twhere\n" +
			"\t\ttp.imdb_title_id = tp2.imdb_title_id";
		String relativeSection = "select\n" +
			"\t\tra.imdb_name_id, tp.title_id, ra.relative_id as family_id, tp2.title_id as family_role_id, tp.imdb_title_id, \"Relative\" as \"Relation\"\n" +
			"\tfrom relatives_artist ra\n" +
			"\n" +
			"\tinner join title_principals tp on tp.imdb_name_id = ra.imdb_name_id\n" +
			"\tinner join title_principals tp2 on tp2.imdb_name_id = ra.relative_id\n" +
			"\n" +
			"\twhere\n" +
			"\t\ttp.imdb_title_id = tp2.imdb_title_id";

		ArrayList<String> unions = new ArrayList<>();
		if(famBoxChildren.isSelected())
			unions.add(childrenSection);
		if(famBoxParents.isSelected())
			unions.add(parentSection);
		if(famBoxSpouses.isSelected())
			unions.add(spouseSection);
		if(famBoxRelatives.isSelected())
			unions.add(relativeSection);

		String unionSection = String.join("\nunion\n", unions);

		if(unionSection.isEmpty())
			return null;

		String artistRole = "";
		if(!artistRoleBox.getValue().getId().equals("*"))
			artistRole = "t1.title_id = " + artistRoleBox.getValue().getId() + " and\n";

		String familyRole = "";
		if(!familyRoleBox.getValue().getId().equals("*"))
			familyRole = "t2.title_id = " + familyRoleBox.getValue().getId() + " and\n";

		PreparedStatement ps = db.prepareStatement(
			"select m.original_title as \"Movie\", m.year as \"Year\", a1.name as \"Artist\", t1.title as Role, a2.name as \"Family Member\", t2.title as \"Family Member Role\", Relation as \"Family Member Relation\" from (\n" +
				"\n" + unionSection + "\n" +
				") as x\n" +
				"\n" +
				"inner join artist a1 on x.imdb_name_id = a1.imdb_name_id\n" +
				"inner join artist a2 on x.family_id = a2.imdb_name_id\n" +
				"inner join titles t1 on x.title_id = t1.title_id\n" +
				"inner join titles t2 on x.family_role_id = t2.title_id\n" +
				"inner join movies m  on x.imdb_title_id = m.imdb_title_id\n" +
				"where\n" +
				"m.year >= " + minYear.getValue() + " and\n" +
				"m.year <= " + maxYear.getValue() + " and\n" +
				artistRole +
				familyRole +
				"m.budget_currency >= " + minBudget.getValue() + " and m.currency = \"USD\"\n" +
				"order by\n" +
				"\tm.imdb_title_id"
			);
		return ps.executeQuery();
	}
}
