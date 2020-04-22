package com.yury.demo.model.book;

import java.util.Comparator;

public class Order implements Comparator<Order>, Comparable<Order> {
    private String timestamp;
    private Boolean ordType = true; //Market or Limit (FIX <40>), market = true
    private Boolean side;           //Buy or sell (FIX <54>), buy = true
    private String symbol;          //FIX <55>
    private Integer orderQty;       //Order quantity (FIX <38>
    private Double price;           //price for limit orders (FIX <44>)
    private String clOrdID;         //Unique ID of order assigned by institution/intermediary ( FIX <11>)

    public Order(){ };

    public Order(String timestamp, Boolean ordType, Boolean side, String symbol,
                                 Integer orderQty, Double price, String clOrdID) {
        this.timestamp = timestamp;
        this.ordType = ordType;
        this.side = side;
        this.symbol = symbol;
        this.orderQty = orderQty;
        this.price = price;
        this.clOrdID = clOrdID;
    }

    public String getTimestamp() {
        return timestamp;
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

    @Override
    public int compareTo(Order order){
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

}
