# IMQdB

|Class|Description|
|---|---|
|App|Main JavaFX setup|
|SqliteConnection|Handles connecting to sqlite .db file|
|Query|Holds some query metadata and gives us a reference to the queries controller object so that we can access it from ControllerMain|
|QueryController (interface)|Forces implementation of an execute method, which should return the result set of an actual sql query|
|QueryFactory|For creating all the queries at the start of the program|
|Qc*|Classes that implement the QueryController interface and implement any query specific fxml logic, and the sql query itself|
|DataResult|Wrapper for ResultSet, lets us store the result in the TableView|
|TableWrapper|Wrapper class for the TableView, allows for easily creating and filling table with SQL ResultSets|

## Creating a query

1. Create a query controller object that implements the QueryController interface
2. Create an FXML file that defines the parameter UI
   1. Use either an HBox or VBox as the root element 
   2. Try not to use any hard-coded values for layout information, set it to COMPUTED_SIZE
3. Set the fx:controller of the .fxml file to the QueryController you created at step 1 
4. Update the `generate()` method of QueryFactory by creating a new Query object and adding it to the list
   1. Give the query a nice name
   2. Give it the path to its .fxml file ***relative to the resources/imqdb directory***

### Other Info + Naming

* imdb.db file should go in src/resources/imqdb/
* Query parameter fxml files should go in the `qc` directory start with "q_"
* QueryController implementations should go in the `qc` package and start with "Qc"

### Artifacts

1. Build -> Build Artifacts -> IMQDB.jar
2. Move the make sure there is a db/imqdb.db file in the directory of jar
3. Unzip javafx sdk in lib/
4. From project root: `java --module-path "lib/javafx-sdk-17.0.1\lib" --add-modules javafx.controls,javafx.fxml -jar out/artifacts/IMQDB_jar/IMQDB.jar App`
