package imqdb.db;

import imqdb.utils.Logger;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class LocalDatabase implements IDatabase {

	private final Connection db;

	public LocalDatabase()
	{
		db = SqliteConnection.getConnection();
	}

	@Override
	public void runQuery(String sql, Consumer<ResultSet> callback)
	{
		Task<ResultSet> task = new Task<ResultSet>() {
			@Override protected ResultSet call() throws Exception {
				Logger.logQueryAttempt(sql);
				try {
					PreparedStatement ps = db.prepareStatement(sql);
					return ps.executeQuery();
				}
				catch(SQLException e) {
					Logger.logSqlError(e, sql);
					return null;
				}
			}
		};

		task.setOnSucceeded(ev -> callback.accept(task.getValue()));

		ThreadWrapper tw = new ThreadWrapper(task);
		tw.start();
	}

	public void runQueryTest(String sql, Consumer<String> callback)
	{
		Task<ResultSet> task = new Task<ResultSet>() {
			@Override protected ResultSet call() throws Exception {
				Logger.logQueryAttempt(sql);
				try {
					PreparedStatement ps = db.prepareStatement(sql);
					return ps.executeQuery();
				}
				catch(SQLException e) {
					Logger.logSqlError(e, sql);
//					ErrorWindow.CreateSqlErrorWindow();
					return null;
				}
			}
		};

		task.setOnSucceeded(ev -> {
			callback.accept("SUCCEEDED!!!");
		});

		ThreadWrapper tw = new ThreadWrapper(task);
		tw.start();
	}

}


class ThreadWrapper implements Runnable {

	private final Task<ResultSet> task;
	private final Thread thread;

	public ThreadWrapper(Task<ResultSet> task)
	{
		this.task = task;
		this.thread = new Thread(task);
		this.thread.setDaemon(true);
	}

	public void run()
	{
		task.run();
	}

	public void start()
	{
		thread.start();
	}

}