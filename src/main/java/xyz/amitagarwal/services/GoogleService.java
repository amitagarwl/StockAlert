package xyz.amitagarwal.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import xyz.amitagarwal.data.Config;
import xyz.amitagarwal.data.StockDetails;
import xyz.amitagarwal.data.StockLimit;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by amit.agarwal on 6/4/16.
 */
public class GoogleService {

    private final Logger logger = Logger.getLogger(GoogleService.class);
    OkHttpClient client = new OkHttpClient();
    private Config config;

    public GoogleService(Config config) {
        this.config = config;

    }

    private String run(String googleFinanceURL) throws IOException {
        Request request = new Request.Builder().url(googleFinanceURL).build();
        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            return responseData;
        } catch (IOException e) {
            logger.error(" Error in Network call to Google ", e);
        }
        return "Error";
    }

    public ArrayList<StockDetails> getGoogleFinanceData(ArrayList<StockLimit> listOfStocks) {
        String googleEndPoint = config.getGoogleEndPoint();
        StringBuilder temp = new StringBuilder();
        ArrayList<StockDetails> listOfStockDetails = new ArrayList<StockDetails>();
        if (listOfStocks.size() == 1) {
            temp.append(":" + listOfStocks.get(0).getName());
        } else {
            temp.append(":");
            for (StockLimit stock : listOfStocks) {
                temp.append(stock.getName() + ",");
            }
            temp.deleteCharAt(temp.length() - 1);
            logger.info(" End Point - " + temp.toString());
        }
        String url = googleEndPoint.concat(temp.toString());
        logger.info(" Google Finance URL : " + url);
        try {
            String googleFinanceData = run(url).replaceFirst("//", "");
            ObjectMapper objectMapper = new ObjectMapper();
            listOfStockDetails = objectMapper.readValue(googleFinanceData, new TypeReference<ArrayList<StockDetails>>() {
            });
            System.out.println("\n");
            for (StockDetails live : listOfStockDetails) {
                System.out.printf("%-10s : %-10s : %-10s : %-10s%n", live.getTickerName(), live.getLastTradedPrice(), live.getChange(), live.getChangePercentage()+" %");
            }
            System.out.println("\n");
            return listOfStockDetails;

        } catch (IOException e) {
            logger.error("Error in Network Call ", e);
        }
        return listOfStockDetails;
    }
}

