package xyz.amitagarwal.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

/**
 * Created by amit.agarwal on 6/4/16.
 */
@Data
@JsonIgnoreProperties({"id", "e", "l", "l_cur", "s", "ltt", "lt", "lt_dts", "c_fix", "cp_fix", "ccol", "pcls_fix"})
public class StockDetails {

    private String id;
    private String tickerName;
    private String e;
    private String l;
    private String lastTradedPrice;
    private String l_cur;
    private String s;
    private String ltt;
    private String lt;
    private String lt_dts;
    private String change;
    private String c_fix;
    private String changePercentage;
    private String cp_fix;
    private String ccol;
    private String pcls_fix;

    @JsonSetter("t")
    public void setTickerName(String tickerName) {
        this.tickerName = tickerName;
    }

    @JsonSetter("l_fix")
    public void setLastTradedPrice(String lastTradedPrice) {
        this.lastTradedPrice = lastTradedPrice;
    }
    @JsonSetter("c")
    public void setChange( String change){
        this.change = change;
    }

    @JsonSetter("cp")
    public void setChangePercentage( String changePercentage){
        this.changePercentage = changePercentage;
    }



}