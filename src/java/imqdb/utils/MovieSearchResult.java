package imqdb.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieSearchResult {

	public String id;
	public String title;
	public String year;
	public String duration;
	public String production;

	public String currency;
	public Long budget;
	public Long domestic;
	public Long worldwide;

	public Float imdb_score;
	public Integer metascore;
	public Long num_votes;

	public String genres;
	public String languages;
	public String countries;

	public MovieSearchResult(ResultSet rs)
	{
		try {
			this.id = rs.getString("imdb_title_id");
			this.title = rs.getString("original_title");
			this.year = rs.getString("year");
			this.duration = rs.getString("duration");
			this.production = rs.getString("production_company");
			this.currency = rs.getString("currency");
			this.budget = rs.getLong("budget_currency");
			this.domestic = rs.getLong("usa_gross_income");
			this.worldwide = rs.getLong("worldwide_gross_income");
			this.imdb_score = rs.getFloat("avg_vote");
			this.metascore = rs.getInt("metascore");
			this.num_votes = rs.getLong("votes");
			this.genres = rs.getString("genres");
			this.countries = rs.getString("countries");
			this.languages = rs.getString("languages");
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public String toString()
	{
		return (year == null) ? title : title + " (" + year + ")";
	}

}
