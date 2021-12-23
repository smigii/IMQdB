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
				"where artist.name like ?");
			ps.setString(1, artist);

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

	public static ArrayList<MovieSearchResult> movieLookup(String movie)
	{
		ArrayList<MovieSearchResult> msrList = new ArrayList<>();

		if(movie == null || movie.isEmpty())
			return msrList;

		try {
			Connection connection = SqliteConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement("select\n" +
				"\tmovies.*,\n" +
				"\tproduction_company,\n" +
				"\tgroup_concat(distinct country) as \"countries\",\n" +
				"\tgroup_concat(distinct genre) as \"genres\",\n" +
				"\tgroup_concat(distinct language) as \"languages\"\n" +
				"from movies\n" +
				"\n" +
				"inner join production_companies on production_companies.production_id = movies.production_id\n" +
				"inner join movie_country on movie_country.imdb_title_id = movies.imdb_title_id\n" +
				"inner join countries on movie_country.country_id = countries.country_id\n" +
				"inner join movie_genre on movie_genre.imdb_title_id = movies.imdb_title_id\n" +
				"inner join genres on movie_genre.genre_id = genres.genre_id\n" +
				"inner join movie_language on movie_language.imdb_title_id = movies.imdb_title_id\n" +
				"inner join languages on movie_language.language_id = languages.language_id\n" +
				"where\n" +
				"\tmovies.original_title like ?\n" +
				"group by movies.imdb_title_id;");
			ps.setString(1, movie);

			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				MovieSearchResult msr = new MovieSearchResult(rs);
				msrList.add(msr);
			}
			return msrList;
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return msrList;
		}
	}

}
