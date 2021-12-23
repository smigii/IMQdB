package imqdb.qc;

import imqdb.QueryController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QcArtistMoneyGenerated implements QueryController {

	@Override
	public ResultSet execute(Connection db) throws SQLException
	{
		PreparedStatement ps = db.prepareStatement(
			"select\n" +
			"	fam_movies.name as \"Artist\",\n" +
			"	group_concat(fam_movies.original_title,'; ') as \"Movies Included in Query\",\n" +
			"	sum(fam_movies.worldwide_gross_income) as \"Worldwide Revenue Generated\",\n" +
			"	sum(fam_movies.usa_gross_income) as \"Domestic Revenue Generated\",\n" +
			"	fam_names.family_names as \"Family Members Included\"\n" +
			"from (\n" +
			"	-- Get all movies involving artist or artist family member\n" +
			"	select distinct\n" +
			"		a.imdb_name_id,\n" +
			"		a.name,\n" +
			"		m.*\n" +
			"	from (\n" +
			"\n" +
			"		select a.imdb_name_id, a.imdb_name_id as fam_id from artist a\n" +
			"		\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, sa.spouse_id as fam_id from artist a\n" +
			"		inner join spouses_artist sa on a.imdb_name_id = sa.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, ca.child_imdb_id as fam_id from artist a\n" +
			"		inner join children_artist ca on a.imdb_name_id = ca.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, pa.parent_id as fam_id from artist a\n" +
			"		inner join parents_artist pa on a.imdb_name_id = pa.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, ra.relative_id as fam_id from artist a\n" +
			"		inner join relatives_artist ra on a.imdb_name_id = ra.imdb_name_id\n" +
			"\n" +
			"	) as x\n" +
			"	inner join title_principals tp on\n" +
			"		tp.imdb_name_id = x.fam_id\n" +
			"	inner join movies m on\n" +
			"		tp.imdb_title_id = m.imdb_title_id\n" +
			"	inner join artist a on\n" +
			"		x.imdb_name_id = a.imdb_name_id\n" +
			"	where\n" +
			"		m.currency = \"USD\"\n" +
			"\n" +
			") as fam_movies\n" +
			"inner join (\n" +
			"	-- Get the list of family members\n" +
			"	select \n" +
			"		fam_ids.imdb_name_id,\n" +
			"		group_concat(fam_names.name,'; ') as \"family_names\"\n" +
			"	from (\n" +
			"		select a.imdb_name_id, a.imdb_name_id as fam_id from artist a\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, sa.spouse_id as fam_id from artist a\n" +
			"		inner join spouses_artist sa on a.imdb_name_id = sa.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, ca.child_imdb_id as fam_id from artist a\n" +
			"		inner join children_artist ca on a.imdb_name_id = ca.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, pa.parent_id as fam_id from artist a\n" +
			"		inner join parents_artist pa on a.imdb_name_id = pa.imdb_name_id\n" +
			"\n" +
			"		union\n" +
			"\n" +
			"		select a.imdb_name_id, ra.relative_id as fam_id from artist a\n" +
			"		inner join relatives_artist ra on a.imdb_name_id = ra.imdb_name_id\n" +
			"\n" +
			"	) as fam_ids\n" +
			"	inner join artist fam_names on fam_ids.fam_id = fam_names.imdb_name_id\n" +
			"	group by fam_ids.imdb_name_id\n" +
			") fam_names\n" +
			"on fam_movies.imdb_name_id = fam_names.imdb_name_id\n" +
			"-- where\n" +
			"	-- fam_movies.imdb_name_id = \"nm0905154\"\n" +
			"group by\n" +
			"	fam_movies.imdb_name_id\n" +
			"order by fam_movies.imdb_title_id");
		return ps.executeQuery();
	}

}
