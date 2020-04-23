package com.yury.demo.book;

import java.util.Comparator;

// Message template for Orders
// Follows FIX 5.0 template
// https://www.onixs.biz/fix-dictionary/5.0/msgType_D_68.html
public class Order implements Comparator<Order>, Comparable<Order> {
    private String timestamp;
    private Boolean ordType = true; //Market or Limit (FIX <40>), market = true
    private Boolean side;           //Buy or sell (FIX <54>), buy = true
    private String symbol;          //FIX <55>
    private Integer orderQty;       //Order quantity (FIX <38>
    private Double price;           //price for limit orders (FIX <44>)
    private String clOrdID;         //Unique ID of order assigned by institution/intermediary ( FIX <11>)
    private Double checksum;

    public Order(){ };

    public Order(String timestamp, Boolean ordType, Boolean side, String symbol,
                                 Integer orderQty, Double price, String clOrdID, Double checksum) {
        this.timestamp = timestamp;
        this.ordType = ordType;
        this.side = side;
        this.symbol = symbol;
        this.orderQty = orderQty;
        this.price = price;
        this.clOrdID = clOrdID;
        this.checksum = checksum;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

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

    public void setOrderQty(Integer orderQty) { this.orderQty = orderQty; }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public String getClOrdID() {
        return clOrdID;
    }

    public Double getChecksum() { return checksum; }

    @Override
    public int compareTo(Order order){
        if (order == null || order.getPrice() == null){
            return -1;
        }
        if (order.getPrice() > this.price) {
            return this.price.compareTo(order.getPrice());
        } else {
            return this.timestamp.compareTo(order.getTimestamp());
        }
    };

    @Override
    public int compare(Order order, Order order1){
        if (order1.getPrice() > order.getPrice()) {
            return order.getPrice().compareTo(order1.getPrice());
        } else {
            return order.getTimestamp().compareTo(order1.getTimestamp());
        }
    };

    @Override
    public String toString(){
        String timestampString = ((this.timestamp == null) ? "null" : this.timestamp.toString());
        String ordTypeString = ((this.ordType == null) ? "null" : this.ordType.toString());
        String sideString = ((this.side == null) ? "null" : this.side.toString());
        String symbolString = ((this.symbol == null) ? "null" : this.symbol);
        String orderQtyString = ((this.orderQty == null) ? "null" : this.orderQty.toString());
        String priceString = ((this.price == null) ? "null" : this.price.toString());

        return priceString + " " + timestampString;
        /**
        return "{" +
                "\"timestamp\": \"" + timestampString + "\"" +
                ", \"ordType\":" + ordTypeString +
                ", \"side\":" + sideString +
                ", \"symbol\": \"" + symbolString + "\"" +
                ", \"orderQty\":" + orderQtyString +
                ", \"price\":" + priceString +
                "}";**/
    }

}
