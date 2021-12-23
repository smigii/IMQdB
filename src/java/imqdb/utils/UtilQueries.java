package imqdb.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UtilQueries {

	private static ArrayList<UtilQueryPair> genres;
	private static ArrayList<UtilQueryPair> languages;
	private static ArrayList<UtilQueryPair> countries;
	private static ArrayList<UtilQueryPair> titles;

	public static ArrayList<UtilQueryPair> getGenres()
	{
		if(genres == null) {
			genres = fill("genres", "genre", "genre_id");
		}
		return genres;
	}

	public static ArrayList<UtilQueryPair> getLanguages()
	{
		if(languages == null) {
			languages = fill("languages", "language", "language_id");
		}
		return languages;
	}

	public static ArrayList<UtilQueryPair> getCountries()
	{
		if(countries == null) {
			countries = fill("countries", "country", "country_id");
		}
		return countries;
	}

	public static ArrayList<UtilQueryPair> getTitles()
	{
		if(titles == null) {
			titles = fill("titles", "title", "title_id");
		}
		return titles;
	}

	private static ArrayList<UtilQueryPair> fill(String table, String field, String id)
	{
		ArrayList<UtilQueryPair> list = new ArrayList<>();
		try {
			Connection connection = SqliteConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement("select * from " + table);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new UtilQueryPair(rs.getString(field), rs.getString(id)));
			}
			return list;
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static ArrayList<ArtistSearchResult> artistLookup(String artist)
	{
		ArrayList<ArtistSearchResult> asrList = new ArrayList<>();

		if(artist == null || artist.isEmpty())
			return asrList;

		try {
			Connection connection = SqliteConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement("select artist.*, c1.country as birth_country, c2.country as death_country from artist\n" +
				"left join countries c1 on artist.country_of_birth_id = c1.country_id\n" +
				"left join countries c2 on artist.country_of_death_id = c2.country_id\n" +
				"where artist.name like \"" + artist + "\"");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				ArtistSearchResult asr = new ArtistSearchResult(rs);
				asrList.add(asr);
			}
			return asrList;
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return asrList;
		}
	}

}
