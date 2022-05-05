package imqdb.db;

import imqdb.utils.ArtistSearchResult;
import imqdb.utils.MovieSearchResult;
import imqdb.utils.UtilQueryPair;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.function.Consumer;

public interface IDatabase {

	void runQuery(String sql, Consumer<ResultSet> callback);

	void artistLookup(String artist, Consumer<ArrayList<ArtistSearchResult>> callback);

	void movieLookup(String movie, Consumer<ArrayList<MovieSearchResult>> callback);

	ArrayList<UtilQueryPair> getGenres();

	ArrayList<UtilQueryPair> getLanguages();

	ArrayList<UtilQueryPair> getCountries();

	ArrayList<UtilQueryPair> getTitles();

}
