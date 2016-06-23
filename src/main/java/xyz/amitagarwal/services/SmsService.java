package xyz.amitagarwal.services;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by amit.agarwal on 6/4/16.
 */
public class SmsService {

    private final Logger logger = Logger.getLogger(RedisService.class);

    public String sendSMS(String messageToSend) {
        try {
            // Construct data
            String user = "username=" + "< email-id >";
            String hash = "&hash=" + "< hash value >";
            String message = "&message=" + messageToSend;
            String sender = "&sender=" + "TXTLCL";
            String numbers = "&numbers=" + "< mobile number >";

            logger.info("Message to be Sent : " + messageToSend);
            HttpURLConnection conn = (HttpURLConnection) new URL("http://api.textlocal.in/send/?").openConnection();
            String data = user + hash + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();
            return stringBuffer.toString();

        } catch (Exception e) {
            logger.error("Error while sending message " , e);
            return "Error" + e;
        }


    }
}
