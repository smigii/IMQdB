package imqdb.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static String sessionPath;

    public static String getSessionPath()
    {
        if(sessionPath == null) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SS");
            String timestamp = formatter.format(date);
            sessionPath = "sql/query_log_" + timestamp + ".sql";
        }
        return sessionPath;
    }

    public static void logQueryAttempt(String query){
        try {
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            String message = "--------------- Query date and time: "
                + formatter.format(date) + "\r\n\r\n" + query + "\r\n\r\n";
            Logger.tryAppendToFile(message);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void logSqlError(SQLException e)
    {
        try {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String timestamp = formatter.format(date);
            Files.createDirectories(Path.of("err"));
            String final_message = "--- QUERY ERROR --------------- \r\ndate and time: " + timestamp + "\r\n/* " + e.getMessage() + " */\r\n\r\n";

            Logger.tryAppendToFile(final_message);
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    private static void tryAppendToFile(String message) throws IOException {
        FileOutputStream fos = new FileOutputStream(getSessionPath(), true);
        fos.write(message.getBytes());
        fos.close();
    }

}
