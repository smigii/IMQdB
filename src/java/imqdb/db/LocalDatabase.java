package imqdb.db;

import imqdb.utils.ArtistSearchResult;
import imqdb.utils.Logger;
import imqdb.utils.MovieSearchResult;
import imqdb.utils.UtilQueryPair;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class LocalDatabase implements IDatabase {

	private final Connection connection;
	private final ArrayList<UtilQueryPair> genres;
	private final ArrayList<UtilQueryPair> languages;
	private final ArrayList<UtilQueryPair> titles;
	private final ArrayList<UtilQueryPair> countries;

	public LocalDatabase()
	{
		connection = SqliteConnection.getConnection();
		genres = fill("genres", "genre", "genre_id");
		languages = fill("languages", "language", "language_id");
		countries = fill("countries", "country", "country_id");
		titles = fill("titles", "title", "title_id");
	}

	private ArrayList<UtilQueryPair> fill(String table, String field, String id)
	{
		ArrayList<UtilQueryPair> list = new ArrayList<>();
		try {
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

	@Override
	public void runQuery(String sql, Consumer<ResultSet> callback)
	{
		Task<ResultSet> task = new Task<ResultSet>() {
			@Override protected ResultSet call() throws Exception {
				Logger.logQueryAttempt(sql);
				try {
					PreparedStatement ps = connection.prepareStatement(sql);
					return ps.executeQuery();
				}
				catch(SQLException e) {
					Logger.logSqlError(e, sql);
					return null;
				}
			}
		};

		task.setOnSucceeded(ev -> callback.accept(task.getValue()));
		dispatchThread(task);
	}

	public void artistLookup(String artist, Consumer<ArrayList<ArtistSearchResult>> callback)
	{
		ArrayList<ArtistSearchResult> asrList = new ArrayList<>();

		if(artist == null || artist.isEmpty()) {
			// TODO: Wasteful
			callback.accept(asrList);
		}

		Task<ArrayList<ArtistSearchResult>> task = new Task<ArrayList<ArtistSearchResult>>() {
			@Override
			protected ArrayList<ArtistSearchResult> call() throws Exception
			{
				try {
					PreparedStatement ps = connection.prepareStatement("select\n" +
						"\tartist.*, c1.country as birth_country, c2.country as death_country from artist\n" +
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
		};

		task.setOnSucceeded(ev -> callback.accept(task.getValue()));
		dispatchThread(task);
	}

	public void movieLookup(String movie, Consumer<ArrayList<MovieSearchResult>> callback)
	{
		ArrayList<MovieSearchResult> msrList = new ArrayList<>();

		if(movie == null || movie.isEmpty()) {
			// TODO: Wasteful
			callback.accept(msrList);
		}

		Task<ArrayList<MovieSearchResult>> task = new Task<ArrayList<MovieSearchResult>>() {
			@Override
			protected ArrayList<MovieSearchResult> call() throws Exception
			{
				try {
					Connection connection = SqliteConnection.getConnection();
					PreparedStatement ps = connection.prepareStatement("select\n" +
						"\tmovies.*,\n" +
						"\tproduction_company,\n" +
						"\tgroup_concat(distinct country) as \"countries\",\n" +
						"\tgroup_concat(distinct genre) as \"genres\",\n" +
						"\tgroup_concat(distinct language) as \"languages\"\n" +
						"from movies\n" +
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
		};

		task.setOnSucceeded(ev -> callback.accept(task.getValue()));
		dispatchThread(task);
	}

	public ArrayList<UtilQueryPair> getGenres()
	{
		return genres;
	}

	public ArrayList<UtilQueryPair> getLanguages()
	{
		return languages;
	}

	public ArrayList<UtilQueryPair> getCountries()
	{
		return countries;
	}

	public ArrayList<UtilQueryPair> getTitles()
	{
		return titles;
	}

	private <T> void dispatchThread(Task<T> task)
	{
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
		System.out.println("THREAD STARTED");
	}

}
