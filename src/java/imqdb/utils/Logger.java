package imqdb.utils;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static void log(String message){
        String filename = "sql/query_log.sql";
        try {
            Logger.tryAppendToFile(message, filename);
            }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void tryAppendToFile(String message, String filename)
            throws Exception {

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String final_message = "-------------\r\n-- Query date and time: "
                + formatter.format(date) + "\r\n\r\n" + message + "\r\n\r\n";

        FileOutputStream fos = new FileOutputStream(filename, true);
        fos.write(final_message.getBytes());
        fos.close();
    }

}
