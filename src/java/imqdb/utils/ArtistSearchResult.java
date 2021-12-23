package imqdb.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistSearchResult {

	public String id;
	public String name;
	public String birth_name;
	public String height;
	public String bio;

	public String place_of_birth;
	public String country_of_birth;
	public String date_of_birth;

	public String place_of_death;
	public String country_of_death;
	public String date_of_death;
	public String reason_of_death;

	public ArtistSearchResult(ResultSet rs)
	{
		try {
			this.id = rs.getString("imdb_name_id");
			this.name = rs.getString("name");
			this.birth_name = rs.getString("birth_name");
			this.height = rs.getString("height");
			this.bio = rs.getString("bio");
			this.place_of_birth = rs.getString("place_of_birth");
			this.place_of_death = rs.getString("place_of_death");
			this.country_of_birth = rs.getString("birth_country");
			this.country_of_death = rs.getString("death_country");
			this.date_of_birth = rs.getString("date_of_birth");
			this.date_of_death = rs.getString("date_of_death");
			this.reason_of_death = rs.getString("reason_of_death");
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public String toString()
	{
		return name + " (" + id + ")";
	}

}
