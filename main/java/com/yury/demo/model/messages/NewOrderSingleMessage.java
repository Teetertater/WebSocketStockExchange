package com.yury.demo.model.messages;


import java.time.ZonedDateTime;
import java.util.Date;

// Message for creating a new order
// Follows FIX 5.0 template
// https://www.onixs.biz/fix-dictionary/5.0/msgType_D_68.html
public class NewOrderSingleMessage {
    private String messageType = "D";

    private String timestamp;
    private Boolean ordType = true; //Market or Limit (FIX <40>), market = true
    private Boolean side;           //Buy or sell (FIX <54>), market = true
    private String symbol;          //FIX <55>
    private Integer orderQty;       //Order quantity (FIX <38>
    private Double price;           //price for limit orders (FIX <44>)
    private String clOrdID;         //Unique ID of order assigned by institution/intermediary ( FIX <11>)
    private String checksum;        // FIX <10>>

    public NewOrderSingleMessage(){ };

    public NewOrderSingleMessage(String timestamp, Boolean ordType, Boolean side, String symbol,
                                 Integer orderQty, Double price, String clOrdID, String checksum) {
        this.timestamp = timestamp;
        this.ordType = ordType;
        this.side = side;
        this.symbol = symbol;
        this.orderQty = orderQty;
        this.price = price;
        this.clOrdID = clOrdID;
        this.checksum = checksum;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getOrdType() {
        return ordType;
    }

    public Boolean getSide() {
        return side;
    }

    public String getSymbol() {
        return symbol;
    }

    public Integer getOrderQty() {return orderQty; }

    public Double getPrice() { return price; }

    public String getClOrdID() {
        return clOrdID;
    }

    public String getChecksum() {
        return checksum;
    }

    @Override
    public String toString() {
        String timestampString = ((this.timestamp == null) ? "null" : this.timestamp.toString());
        String ordTypeString = ((this.ordType == null) ? "null" : this.ordType.toString());
        String sideString = ((this.side == null) ? "null" : this.side.toString());
        String symbolString = ((this.symbol == null) ? "null" : this.symbol);
        String orderQtyString = ((this.orderQty == null) ? "null" : this.orderQty.toString());
        String priceString = ((this.price == null) ? "null" : this.price.toString());

        return "{" +
                "\"timestamp\": \"" + timestampString + "\"" +
                ", \"ordType\":" + ordTypeString +
                ", \"side\":" + sideString +
                ", \"symbol\": \"" + symbolString + "\"" +
                ", \"orderQty\":" + orderQtyString +
                ", \"price\":" + priceString +
                "}";
    }
}