package xyz.amitagarwal.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import xyz.amitagarwal.data.Config;
import xyz.amitagarwal.data.StockLimit;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by amit.agarwal on 6/4/16.
 */
public class RedisService {

    private Config config;
    private final Logger logger = Logger.getLogger(RedisService.class);

    public RedisService(Config config) {
        this.config = config;
    }

    public ArrayList<StockLimit> readData() {
        ArrayList<StockLimit> listOfStock = new ArrayList<StockLimit>();
        try {
            Jedis jedis = new Jedis(config.getRedisEndPoint());
            Map<String, String> map = jedis.hgetAll(config.getBucketName());
            jedis.close();
            ObjectMapper objectMapper = new ObjectMapper();
            for (Object key : map.keySet()) {
                listOfStock.add(objectMapper.readValue(map.get(key), StockLimit.class));
            }
            logger.info(" Successfully read data from Redis : ");
            for (StockLimit stock : listOfStock) {
                logger.info(stock.toString());
            }
            return listOfStock;

        } catch (Exception e) {
            logger.error(" Error Reading Redis EndPoints", e);
        }
        return listOfStock;
    }

    public void incrementUpperLimit(StockLimit stock) {
        try {
            Jedis jedis = new Jedis(config.getRedisEndPoint());
            double currentUpperLimit = Double.parseDouble(stock.getUpperLimit());
            double newupperLimit = currentUpperLimit + (2 * (currentUpperLimit) / 100.0);
            logger.info("New Upper Limit for " + stock.getName() + " : " + newupperLimit);
            stock.setUpperLimit(String.valueOf(newupperLimit));
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(stock);
            jedis.hset(config.getBucketName(), stock.getName(), value);
            jedis.close();


        } catch (Exception e) {
            logger.error(" Error setting upper limit into Redis", e);
        }

    }

    public void decrementLowerLimit( StockLimit stock){
        try {
            Jedis jedis = new Jedis(config.getRedisEndPoint());
            double currentLowerLimit = Double.parseDouble(stock.getLowerLimit());
            double newLowerLimit = currentLowerLimit - (2 * (currentLowerLimit) / 100.0);
            logger.info("New lower Limit for " + stock.getName() + " : " + newLowerLimit);
            stock.setLowerLimit(String.valueOf(newLowerLimit));
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(stock);
            jedis.hset(config.getBucketName(), stock.getName(), value);
            jedis.close();


        } catch (Exception e) {
            logger.error(" Error setting lower limit into Redis", e);
        }



    }

}
