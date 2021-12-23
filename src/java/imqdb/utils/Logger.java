package imqdb.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Logger {

    private static String sessionTimestamp;
    private static String errPathPrefix = "err/sql_error_";
    private static String logPathPrefix = "log/sql_log_";

    public static String getSessionTimestamp()
    {
        if(sessionTimestamp == null) {
            Random random = new Random(System.currentTimeMillis());
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            sessionTimestamp = formatter.format(date);
            sessionTimestamp += "-" + random.nextInt(100, 999);
        }
        return sessionTimestamp;
    }

    public static void logQueryAttempt(String query){
        try {
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            String message = "--------------- Query date and time: "
                + formatter.format(date) + "\r\n\r\n" + query + "\r\n\r\n";
            String path = logPathPrefix + getSessionTimestamp() + ".sql";
            Logger.tryAppendToFile(message, path);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void logSqlError(SQLException e, String sql)
    {
        try {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss:SS z");
            String timestamp = formatter.format(date);

            String final_message =
                "--------------- QUERY ERROR:  --------------- \r\n--date and time: " + timestamp + "\r\n" +
                "/* " + e.getMessage() + " */\r\n\r\n-- QUERY: \r\n\r\n" +
                sql + "\r\n\r\n";

            String path = errPathPrefix + getSessionTimestamp() + ".sql";
            Logger.tryAppendToFile(final_message, path);
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    private static void tryAppendToFile(String message, String path) throws IOException {
        Files.createDirectories(Path.of("log/"));
        Files.createDirectories(Path.of("err/"));
        FileOutputStream fos = new FileOutputStream(path, true);
        fos.write(message.getBytes());
        fos.close();
    }

}
