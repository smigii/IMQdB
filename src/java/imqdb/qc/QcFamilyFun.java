package imqdb.qc;

import imqdb.QueryController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcFamilyFun implements QueryController {


	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		PreparedStatement ps = db.prepareStatement("select m.original_title as \"Movie\", a1.name as \"Artist\", t1.title as Role, a2.name as \"Family Member\", t2.title as \"Family Member Role\", Relation as \"Family Member Relation\" from (\n" +
			"\t-- Artists worked with parent\n" +
			"\tselect\n" +
			"\t\tpa.imdb_name_id, tp.title_id, pa.parent_id as family_id, tp2.title_id as family_role_id, tp.imdb_title_id, \"Parent\" as \"Relation\"\n" +
			"\tfrom parents_artist pa\n" +
			"\n" +
			"\tinner join title_principals tp on tp.imdb_name_id = pa.imdb_name_id\n" +
			"\tinner join title_principals tp2 on tp2.imdb_name_id = pa.parent_id\n" +
			"\n" +
			"\twhere\n" +
			"\t\ttp.imdb_title_id = tp2.imdb_title_id\n" +
			"\t\n" +
			"\tunion\n" +
			"\t\n" +
			"\t-- Artists worked with child\n" +
			"\tselect\n" +
			"\t\tca.imdb_name_id, tp.title_id, ca.child_imdb_id as family_id, tp2.title_id as family_role_id, tp.imdb_title_id, \"Child\" as \"Relation\"\n" +
			"\tfrom children_artist ca\n" +
			"\n" +
			"\tinner join title_principals tp on tp.imdb_name_id = ca.imdb_name_id\n" +
			"\tinner join title_principals tp2 on tp2.imdb_name_id = ca.child_imdb_id\n" +
			"\n" +
			"\twhere\n" +
			"\t\ttp.imdb_title_id = tp2.imdb_title_id\n" +
			") as x\n" +
			"\n" +
			"inner join artist a1 on x.imdb_name_id = a1.imdb_name_id\n" +
			"inner join artist a2 on x.family_id = a2.imdb_name_id\n" +
			"inner join titles t1 on x.title_id = t1.title_id\n" +
			"inner join titles t2 on x.family_role_id = t2.title_id\n" +
			"inner join movies m  on x.imdb_title_id = m.imdb_title_id\n"
			);
		return ps.executeQuery();
	}
}
