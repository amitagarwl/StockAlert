package xyz.amitagarwal;

import com.github.vivekkothari.YamlParser;
import org.apache.log4j.Logger;
import xyz.amitagarwal.data.Config;
import xyz.amitagarwal.data.StockDetails;
import xyz.amitagarwal.data.StockLimit;
import xyz.amitagarwal.services.GoogleService;
import xyz.amitagarwal.services.RedisService;
import xyz.amitagarwal.services.SmsService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import static com.jayway.restassured.path.json.JsonPath.from;

/**
 * Created by amit.agarwal on 6/4/16.
 */
public class MainApplication {

    private static Logger logger = Logger.getLogger(MainApplication.class);
    private Config config;


    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1) {
            logger.error(" Please Provide a Config file as parameter");
            return;
        }
        logger.info(" ********** Starting Now **********");
        MainApplication app = new MainApplication();
        app.config = YamlParser.load(new FileInputStream(args[0]), Config.class);
        app.processData();
        logger.info(" ********** Ending Now ********** ");
    }

    private void processData() {
        RedisService redisService = new RedisService(config);
        ArrayList<StockLimit> listOfStocks = redisService.readData();
        if (listOfStocks.isEmpty()) {
            logger.error(" Redis does not have required data ");
            return;
        }
        GoogleService googleService = new GoogleService(config);
        ArrayList<StockDetails> liveStockData = googleService.getGoogleFinanceData(listOfStocks);
        if (listOfStocks.size() != liveStockData.size()) {
            logger.error("Size Mismatch! Should not have happened");
            return;
        }
        StringBuilder message = new StringBuilder();
        Iterator<StockDetails> it1 = liveStockData.iterator();
        Iterator<StockLimit> it2 = listOfStocks.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            StockDetails liveStock = it1.next();
            StockLimit stockLimit = it2.next();
            if (Double.parseDouble(liveStock.getLastTradedPrice()) >= Double.parseDouble(stockLimit.getUpperLimit())) {
                redisService.incrementUpperLimit(stockLimit);
                logger.info(liveStock.getTickerName() + ": " + liveStock.getLastTradedPrice() + " : crossed upper limit");
                message.append(liveStock.getTickerName() + ": " + liveStock.getLastTradedPrice() + " : crossed upper limit");
            } else if (Double.parseDouble(liveStock.getLastTradedPrice()) <= Double.parseDouble(stockLimit.getLowerLimit())) {
                redisService.decrementLowerLimit(stockLimit);
                logger.info(liveStock.getTickerName() + ": " + liveStock.getLastTradedPrice() + " : crossed lower limit");
                message.append(liveStock.getTickerName() + ": " + liveStock.getLastTradedPrice() + " : crossed lower limit");

            }
        }
        if (message.length() == 0) {
            logger.info(" No Alerts for any of the Stocks");
        } else {
            logger.info("Message to be Sent :" + message.toString());
            SmsService sms = new SmsService();
            String response = sms.sendSMS(message.toString());
            logger.info("TextLocal response : " + response);
            assert from(response).get("status").toString().equalsIgnoreCase("success");

        }


    }
}
