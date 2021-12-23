# IMQdB

|Class|Description|
|---|---|
|App|Main JavaFX setup|
|SqliteConnection|Handles connecting to sqlite .db file|
|Query|Holds some query metadata and gives us a reference to the queries controller object so that we can access it from ControllerMain|
|QueryController (interface)|Forces implementation of an execute method, which should return the result set of an actual sql query|
|QueryFactory|For creating all the queries at the start of the program|
|Qc*|Classes that implement the QueryController interface and implement any query specific fxml logic, and the sql query itself|
|ControllerMain|FX controller of the main application interface|
|ControllerArtistDetails|FX Controller for the artist detail section, on the right side of the main interface|
|ControllerMovieDetails|FX Controller for the movie detail section, on the right side of the main interface|
|DataResult|Wrapper for ResultSet, lets us store the result in the TableView|
|TableWrapper|Wrapper class for the TableView, allows for easily creating and filling table with SQL ResultSets|
|UtilQueryCache|For loading language, genre, title (role) and country values into choice-boxes. After the first load, values will be stored in a static ArrayList so we are not repeating queries|
|UtilQueryPair|Holds a field and an ID. For example, an instance generated by getLanguages will hold ("English", "3"), allowing us to query on ID rather than name|

## Query Descriptions

| Query                              |Description|
|------------------------------------|-----|
| Frequent Collaborators             |Checks with artists have worked with other artists multiple times. Can filter by artists, artist and collaborator roles and minimum collaborations|
| Highest Gross Income by Year       |Self explanatory|
| Billion Dollar Club                |Artists who have worked on movies that grossed over $1b USD. Can filter on artist category|
| Family Fun                         |Shows which artists worked on a movie with a family member. Can filter on which family member, artist roles, movie year and minimum budget|
| Money Generated by Artist + Family |Shows how much money an artists family has generated. Can filter which family members to include, artist role and movie years.|
| Most or Least Watched Movies       |Shows the movies that have the most or fewest number of ratings. Can be filtered on Genre, Language, country, artist, or years.|

## Creating a query

1. Create a query controller object that implements the QueryController interface
2. Create an FXML file that defines the parameter UI
   1. Use either an HBox or VBox as the root element 
   2. Try not to use any hard-coded values for layout information, set it to COMPUTED_SIZE
3. Set the fx:controller of the .fxml file to the QueryController you created at step 1 
4. Update the `generate()` method of QueryFactory by creating a new Query object and adding it to the list
   1. Give the query a nice name
   2. Give it the path to its .fxml file ***relative to the resources/imqdb directory***
   3. Ensure that the sql is logged using the Logger.log() function before the statement is prepared.

### Other Info + Naming

* imdb.db file should go in src/resources/imqdb/
* Query parameter fxml files should go in the `qc` directory start with "q_"
* QueryController implementations should go in the `qc` package and start with "Qc"

### Artifacts

Build -> Build Artifacts -> IMQDB.jar

### Deployment

What we need to submit

* db/imdb.db
* IMQDB.jar
* JavaFX sdk

Javafx SDK can be downloaded from https://gluonhq.com/products/javafx/

We should include the x64 SDKs for Windows, Mac and Linux.

Then, instructions as follows...

1. Unzip db/imdb.zip to db/imdb.db
2. Unzip javafx sdk that corresponds to your OS, do not change name
3. From project root:
   1. Windows10: `java --module-path "openjfx-17.0.1_windows-x64_bin-sdk/javafx-sdk-17.0.1/lib" --add-modules javafx.controls,javafx.fxml -jar IMQDB.jar App`
   2. Linux: `java --module-path "openjfx-17.0.1_linux-x64_bin-sdk/javafx-sdk-17.0.1/lib" --add-modules javafx.controls,javafx.fxml -jar IMQDB.jar App`
