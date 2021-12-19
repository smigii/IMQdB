package imqdb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TableWrapper {

	private final TableView<List<Object>> table;

	public TableWrapper()
	{
		table = new TableView<>();
	}

	public void fillTable(ResultSet rs) throws SQLException
	{
		table.getColumns().clear();

		ResultSetMetaData rsmd = rs.getMetaData();
		int nCols = rsmd.getColumnCount();

		List<List<Object>> data = new ArrayList<>();
		List<String> columns = new ArrayList<>();

		for(int i = 1; i <= nCols; i++){
			columns.add(rsmd.getColumnLabel(i));
		}

		while(rs.next()){
			List<Object> row = new ArrayList<>();
			for(int i = 1; i <= nCols; i++){
				row.add(rs.getObject(i));
			}
			data.add(row);
		}

		DataResult result = new DataResult(columns, data);

		for(int i = 0; i < result.getNumColumns(); i++){
			int colIdx = i;
			TableColumn<List<Object>, Object> column = new TableColumn<>(result.getColumnName(i));
			column.setCellValueFactory(cellData ->
				new SimpleObjectProperty<>(cellData.getValue().get(colIdx)));
			table.getColumns().add(column);
		}

		table.getItems().setAll(result.getData());
	}

	public TableView<List<Object>> getTable()
	{
		return table;
	}

}
