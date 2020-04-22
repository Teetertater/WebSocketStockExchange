package com.yury.demo.model.book;

public class Transaction {
    private Order buyOrder;
    private Order sellOrder;
    private String saleTimeStamp;

    public Transaction() {};

    public Transaction(Order buyOrder, Order sellOrder, String sale_timestamp) {
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.saleTimeStamp = sale_timestamp;
    }

    public Order getBuyOrder() {
        return buyOrder;
    }

    public Order getSellOrder() {
        return sellOrder;
    }

    public String getSaleTimeStamp() {
        return saleTimeStamp;
    }
}
